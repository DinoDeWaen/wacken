#!/usr/bin/env python3
"""Owned-emulator evidence using the existing offline native smoke runner."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("serial")
    parser.add_argument("report", type=Path)
    parser.add_argument("--narrow", action="store_true", help="Temporarily use320x480dp")
    parser.add_argument("--talkback", action="store_true", help="Require the installed real TalkBack service")
    args = parser.parse_args()
    root = Path(__file__).resolve().parent.parent
    report = args.report.resolve()
    sdk = Path(os.environ.get("ANDROID_SDK_ROOT", os.environ.get("ANDROID_HOME", str(Path.home()/"Library/Android/sdk"))))
    adb = os.environ.get("ANDROID_ADB") or shutil.which("adb") or str(sdk/"platform-tools/adb")
    def call(*values):
        return subprocess.check_output([adb, *values], text=True).strip()
    def shell(*values):
        return call("-s", args.serial, "shell", *values)
    connected = [line.split()[0] for line in call("devices").splitlines()[1:] if len(line.split()) > 1 and line.split()[1] == "device"]
    if not args.serial.startswith("emulator-") or connected != [args.serial] or shell("getprop", "ro.kernel.qemu") != "1":
        raise SystemExit("Connect only the explicitly selected owned test emulator.")
    report.mkdir(parents=True, exist_ok=False)
    secure = {key:shell("settings", "get", "secure", key) for key in (
        "enabled_accessibility_services", "accessibility_enabled", "touch_exploration_enabled")}
    size, density = shell("wm", "size"), shell("wm", "density")
    device_directory = "/sdcard/Download/wacken-3.0-" + re.sub(r"[^A-Za-z0-9_.-]", "_", report.name)
    metadata = {"serial":args.serial, "fingerprint":shell("getprop", "ro.build.fingerprint"),
        "originalSize":size, "originalDensity":density, "originalAccessibility":secure,
        "narrow":args.narrow, "talkbackRequired":args.talkback, "deviceDirectory":device_directory,
        "source":subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=root, text=True).strip()}
    result = None
    try:
        if args.narrow:
            shell("wm", "size", "640x960")
            shell("wm", "density", "320")
        metadata["auditSize"], metadata["auditDensity"] = shell("wm", "size"), shell("wm", "density")
        env = os.environ.copy()
        env["ORG_GRADLE_PROJECT_android.testInstrumentationRunnerArguments.evidenceDirectory"] = device_directory
        env["ORG_GRADLE_PROJECT_android.testInstrumentationRunnerArguments.talkbackAudit"] = str(args.talkback).lower()
        test = "be.wacken.planner.CriticalFlowsAndroidTest#pairedReleaseAuditAtDoubleFontPreservesStateAndExposesReachableControls"
        with (report/"run.log").open("w") as log:
            result = subprocess.run(["sh", "scripts/verify-android-smoke.sh", args.serial, test], cwd=root, env=env, stdout=log, stderr=subprocess.STDOUT)
        outputs = root/"app/build/outputs/androidTest-results/connected/debug"
        if outputs.exists():
            shutil.copytree(outputs, report/"native-results")
        call("-s", args.serial, "pull", device_directory, str(report/"screenshots"))
        for name in ("app-debug.apk",):
            apk = root/"app/build/outputs/apk/debug"/name
            if apk.exists():metadata[name+".sha256"] = hashlib.sha256(apk.read_bytes()).hexdigest()
        for relative in ("app/src/androidTest/java/be/wacken/planner/NativeAccessibilityEvidence.java", "app/src/androidTest/java/be/wacken/planner/CriticalFlowsAndroidTest.java", "app/src/androidTest/java/be/wacken/planner/NativeUiTestRunner.java", "app/src/main/java/be/wacken/planner/AppearanceController.java"):
            metadata[relative+".sha256"] = hashlib.sha256((root/relative).read_bytes()).hexdigest()
        metadata["exitCode"] = result.returncode
    finally:
        for key,value in secure.items():
            shell("settings", "delete", "secure", key) if value == "null" else shell("settings", "put", "secure", key, value)
        old_size = re.search(r"Override size: (\S+)", size)
        old_density = re.search(r"Override density: (\S+)", density)
        shell("wm", "size", old_size.group(1) if old_size else "reset")
        shell("wm", "density", old_density.group(1) if old_density else "reset")
        metadata["restored"] = (shell("wm", "size") == size and shell("wm", "density") == density
            and all(shell("settings", "get", "secure", key) == value for key,value in secure.items()))
        (report/"device-build.json").write_text(json.dumps(metadata, indent=2)+"\n")
    if result is None or result.returncode:
        raise SystemExit("Native audit failed; inspect " + str(report))
    if not metadata["restored"]:
        raise SystemExit("Owned emulator settings were not restored.")
    screenshots = list((report/"screenshots").glob("*.png"))
    if len(screenshots) != 22:
        raise SystemExit("Expected22 actual paired native screenshots, found"+str(len(screenshots)))
    if args.talkback and not any("TALKBACK_ACTIVE" in path.read_text(errors="replace") for path in (report/"native-results").rglob("logcat-*.txt")):
        raise SystemExit("Missing actual TalkBack service evidence.")
    print("PASS: paired200% native audit;22 screenshots; settings restored; " + str(report))


if __name__ == "__main__":
    main()
