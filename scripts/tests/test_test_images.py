"""Acquisition behavior with a fake Docker CLI; no network or containers."""
import json
import os
from pathlib import Path
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[2]
FAKE = r'''#!/usr/bin/env python3
import json, os, sys
from pathlib import Path
args=sys.argv[1:]
log=Path(os.environ["IMAGE_TEST_LOG"])
with log.open("a") as out: out.write(json.dumps(args)+"\n")
mode=os.environ["IMAGE_TEST_MODE"]
state=Path(os.environ["IMAGE_TEST_STATE"])
saved=json.loads(state.read_text()) if state.exists() else []
if args[0]=="info":
    print(os.environ.get("IMAGE_TEST_ARCH","arm64"))
elif args[:2]==["image","inspect"]:
    ref=args[2]
    if mode=="cached" or ref in saved:
        print(json.dumps([ref if mode!="bad-identity" else ref.split("@")[0]+"@sha256:wrong"]))
    else: sys.exit(1)
elif args[0]=="pull":
    ref=args[1]
    if mode=="both-down" or (mode=="primary-down" and ref.startswith("public.ecr.aws/")):
        print("Registry refused download",file=sys.stderr);sys.exit(125)
    state.write_text(json.dumps(saved+[ref]))
else: sys.exit(2)
'''

class TestImages(unittest.TestCase):
    def run_helper(self, mode, arch="arm64"):
        with tempfile.TemporaryDirectory(prefix="wacken-image-test-") as temp:
            directory=Path(temp)
            docker=directory/"docker";docker.write_text(FAKE);docker.chmod(0o755)
            log=directory/"log"
            env=dict(os.environ,PATH=temp+os.pathsep+os.environ["PATH"],
                IMAGE_TEST_LOG=str(log),IMAGE_TEST_STATE=str(directory/"state"),
                IMAGE_TEST_MODE=mode,IMAGE_TEST_ARCH=arch)
            result=subprocess.run(["bash","-c",
                'set -euo pipefail; source "$1"; prepare_test_images; printf "%s\\n%s\\n" "$WACKEN_TEST_DB_IMAGE" "$WACKEN_TEST_API_IMAGE"',
                "test",str(ROOT/"scripts/test-container-images.sh")],
                env=env,text=True,capture_output=True)
            calls=[json.loads(line) for line in log.read_text().splitlines()] if log.exists() else []
            return result,calls
    def test_cached_verified_images_need_no_download(self):
        result,calls=self.run_helper("cached")
        self.assertEqual(0,result.returncode,result.stderr)
        self.assertEqual(2,len(result.stdout.splitlines()))
        self.assertFalse(any(call[0]=="pull" for call in calls))
    def test_primary_refusal_uses_identical_pinned_official_mirrors(self):
        result,calls=self.run_helper("primary-down")
        self.assertEqual(0,result.returncode,result.stderr)
        refs=result.stdout.splitlines()
        self.assertTrue(refs[0].startswith("docker.io/supabase/postgres@sha256:"))
        self.assertTrue(refs[1].startswith("docker.io/postgrest/postgrest@sha256:"))
        pulls=[call[1] for call in calls if call[0]=="pull"]
        self.assertEqual(4,len(pulls))
        self.assertEqual(pulls[0].split("@")[1],pulls[1].split("@")[1])
        self.assertEqual(pulls[2].split("@")[1],pulls[3].split("@")[1])
    def test_unavailable_sources_fail_without_skipping_validation(self):
        result,calls=self.run_helper("both-down")
        self.assertNotEqual(0,result.returncode)
        self.assertIn("Could not acquire verified test image",result.stderr)
        self.assertEqual("",result.stdout)
    def test_successful_pull_with_wrong_identity_is_not_accepted(self):
        result,_=self.run_helper("bad-identity")
        self.assertNotEqual(0,result.returncode)
        self.assertIn("Could not acquire verified test image",result.stderr)
    def test_unknown_daemon_architecture_fails_clearly(self):
        result,calls=self.run_helper("cached","unsupported")
        self.assertNotEqual(0,result.returncode)
        self.assertIn("Unsupported Docker architecture",result.stderr)
        self.assertFalse(any(call[0]=="pull" for call in calls))
    def test_amd64_uses_verified_platform_digest(self):
        result,_=self.run_helper("cached","x86_64")
        self.assertEqual(0,result.returncode,result.stderr)
        self.assertIn("sha256:d5424e9f4d0c21d63991c3f3dd77881f23f722c3a212452a61bc91924b79e8c8",result.stdout)

if __name__=="__main__":
    unittest.main()
