"""Exercise emulator network setup/restore without touching a real device."""
import os
from pathlib import Path
import subprocess
import tempfile
import unittest

RUNNER = Path(__file__).resolve().parents[1] / "verify-android-smoke.sh"
ADB = """#!/usr/bin/env python3
import json, os, sys
from pathlib import Path
args=sys.argv[1:]
root=Path(os.environ["SMOKE_FIXTURE"])
with (root/"calls").open("a") as calls: calls.write(json.dumps(args)+"\\n")
if args[:1]==["-s"]: args=args[3:]
if args==["devices"]: print("List of devices attached\\nemulator-5554\\tdevice")
elif args==["getprop","ro.kernel.qemu"]: print("1")
elif args==["getprop","ro.build.version.sdk"]: print(os.environ["SMOKE_API"])
elif args[:3]==["settings","get","global"]:
 key=args[3]
 print((root/"flight").read_text() if key=="airplane_mode_on" else "1")
elif args[:3]==["settings","put","global"] and args[3]=="airplane_mode_on": (root/"flight").write_text(args[4])
elif args[:3]==["cmd","connectivity","airplane-mode"]: (root/"flight").write_text("1" if args[3]=="enable" else "0")
"""

class AndroidSmokeRunnerTest(unittest.TestCase):
    def run_fixture(self, api, fail=False):
        with tempfile.TemporaryDirectory(prefix="wacken-smoke-runner-") as directory:
            root=Path(directory)
            (root/"adb").write_text(ADB)
            (root/"adb").chmod(0o755)
            (root/"flight").write_text("0")
            (root/"gradlew").write_text("#!/bin/sh\nprintf '%s\\n' \"$@\" > gradle-args\nexit "+("1" if fail else "0")+"\n")
            (root/"gradlew").chmod(0o755)
            env={**os.environ,"ANDROID_ADB":str(root/"adb"),"SMOKE_FIXTURE":directory,
                 "SMOKE_API":str(api),"JAVA_HOME":"fixture"}
            result=subprocess.run(["sh",str(RUNNER),"emulator-5554","all"],cwd=root,env=env,capture_output=True,text=True)
            import json
            calls=[json.loads(line) for line in (root/"calls").read_text().splitlines()]
            self.assertEqual("0",(root/"flight").read_text(),"Previous airplane mode must be restored")
            self.assertIn(":app:connectedDebugAndroidTest",(root/"gradle-args").read_text())
            self.assertEqual(1 if fail else 0,result.returncode,result.stderr)
            return calls

    def test_api23_uses_supported_settings_and_broadcast_then_restores(self):
        calls=self.run_fixture(23)
        self.assertFalse(any("cmd" in call for call in calls))
        self.assertIn(["-s","emulator-5554","shell","am","broadcast","-a","android.intent.action.AIRPLANE_MODE","--ez","state","true"],calls)
        self.assertIn(["-s","emulator-5554","shell","am","broadcast","-a","android.intent.action.AIRPLANE_MODE","--ez","state","false"],calls)

    def test_api36_uses_connectivity_command_and_restores(self):
        calls=self.run_fixture(36)
        self.assertIn(["-s","emulator-5554","shell","cmd","connectivity","airplane-mode","enable"],calls)
        self.assertIn(["-s","emulator-5554","shell","cmd","connectivity","airplane-mode","disable"],calls)

    def test_test_failure_still_restores_old_emulator_network(self):
        self.run_fixture(23,fail=True)

if __name__=="__main__":
    unittest.main()
