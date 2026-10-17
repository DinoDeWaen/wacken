"""Safety gates must reject an unsafe target before any APK installation."""
import os
from pathlib import Path
import subprocess
import tempfile
import unittest

RUNNER = Path(__file__).resolve().parents[1] / "verify-signed-upgrade.sh"


class SignedUpgradeRunnerSafetyTest(unittest.TestCase):
    def run_gate(self, serial="emulator-5554", existing=False, candidate_cert="stable", candidate_code=42, booted=True):
        with tempfile.TemporaryDirectory() as temporary:
            root = Path(temporary)
            log = root / "actions"
            adb = root / "adb"
            adb.write_text("""#!/bin/sh
case "$*" in
  devices) printf 'List of devices attached\\nemulator-5554\\tdevice\\n';;
  *'getprop ro.kernel.qemu') echo 1;;
  *'getprop sys.boot_completed') echo "$TEST_BOOTED";;
  *'pm path be.wacken.planner') if [ "$TEST_EXISTING" = 1 ]; then echo package:existing.apk; fi;;
  *install*) echo INSTALL >>"$TEST_LOG";;
esac
""")
            tools = root / "tools"
            tools.mkdir()
            signer = tools / "apksigner"
            signer.write_text("""#!/bin/sh
case "$*" in *candidate.apk*) cert="$TEST_CANDIDATE_CERT";; *) cert=stable;; esac
echo "Signer #1 certificate SHA-256 digest: $cert"
""")
            aapt = tools / "aapt"
            aapt.write_text("""#!/bin/sh
case "$*" in *candidate.apk*) code="$TEST_CANDIDATE_CODE";; *) code=41;; esac
echo "package: name='be.wacken.planner' versionCode='$code' versionName='fixture'"
""")
            for script in (adb, signer, aapt):
                script.chmod(0o755)
            apks = [root / (name + ".apk") for name in ("baseline", "candidate", "probe")]
            for apk in apks:
                apk.write_bytes(b"fixture")
            environment = dict(os.environ, ANDROID_ADB=str(adb), ANDROID_BUILD_TOOLS=str(tools),
                TEST_EXISTING="1" if existing else "0", TEST_BOOTED="1" if booted else "0", TEST_LOG=str(log),
                TEST_CANDIDATE_CERT=candidate_cert, TEST_CANDIDATE_CODE=str(candidate_code))
            result = subprocess.run(["sh", str(RUNNER), serial, *map(str, apks), str(root / "report")],
                env=environment, capture_output=True, text=True, timeout=10)
            self.assertNotEqual(0, result.returncode, result.stdout + result.stderr)
            self.assertFalse(log.exists(), "Safety gate allowed an install")
            return result.stderr

    def test_physical_device_is_rejected_before_install(self):
        self.assertIn("fresh isolated emulator", self.run_gate(serial="physical-device"))

    def test_booting_emulator_is_rejected_before_install(self):
        self.assertIn("finish booting", self.run_gate(booted=False))

    def test_existing_installation_is_never_replaced(self):
        self.assertIn("Refusing an existing installation", self.run_gate(existing=True))

    def test_signer_mismatch_is_rejected_before_install(self):
        self.assertIn("certificate mismatch", self.run_gate(candidate_cert="different"))

    def test_downgrade_is_rejected_before_install(self):
        self.assertIn("higher versionCode", self.run_gate(candidate_code=40))

    def test_same_version_requires_explicit_intermediate_validation_option(self):
        self.assertIn("higher versionCode", self.run_gate(candidate_code=41))


if __name__ == "__main__":
    unittest.main()
