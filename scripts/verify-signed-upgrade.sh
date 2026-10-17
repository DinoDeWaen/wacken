#!/bin/sh
# Explicit fixture-only signed upgrade, never an existing user installation.
set -eu
if [ "$#" -lt 5 ] || [ "$#" -gt 6 ]; then
  echo "Usage: $0 emulator-serial baseline.apk candidate.apk signed-probe.apk report-directory [--allow-same-version]" >&2
  exit 2
fi
UPGRADE_SERIAL=$1
UPGRADE_BASELINE=$2
UPGRADE_CANDIDATE=$3
UPGRADE_PROBE=$4
UPGRADE_REPORT=$5
UPGRADE_SDK=${ANDROID_SDK_ROOT:-"$HOME/Library/Android/sdk"}
UPGRADE_ADB=${ANDROID_ADB:-"$UPGRADE_SDK/platform-tools/adb"}
UPGRADE_BUILD_TOOLS=${ANDROID_BUILD_TOOLS:-"$UPGRADE_SDK/build-tools/36.0.0"}
case "$UPGRADE_SERIAL" in emulator-*) ;; *) echo "Only a fresh isolated emulator is supported." >&2; exit 2;; esac
if [ "$#" = 6 ] && [ "$6" != --allow-same-version ]; then echo "Unknown option." >&2; exit 2; fi
for apk in "$UPGRADE_BASELINE" "$UPGRADE_CANDIDATE" "$UPGRADE_PROBE"; do
  test -f "$apk" || { echo "Missing APK: $apk" >&2; exit 2; }
done
UPGRADE_CONNECTED=$("$UPGRADE_ADB" devices | awk 'NR>1 && $2=="device" {print $1}')
test "$UPGRADE_CONNECTED" = "$UPGRADE_SERIAL" || { echo "Connect only the selected emulator." >&2; exit 2; }
test "$("$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell getprop ro.kernel.qemu | tr -d '\r')" = 1 || exit 2
test "$("$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell getprop sys.boot_completed | tr -d '\r')" = 1 ||
  { echo "Wait for the fresh emulator to finish booting." >&2; exit 2; }
test -z "$("$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell pm path be.wacken.planner | tr -d '\r')" ||
  { echo "Refusing an existing installation. Start a separate fresh emulator data directory." >&2; exit 2; }
mkdir -p "$UPGRADE_REPORT"
for entry in baseline candidate probe; do
  case "$entry" in baseline) apk=$UPGRADE_BASELINE;; candidate) apk=$UPGRADE_CANDIDATE;; probe) apk=$UPGRADE_PROBE;; esac
  "$UPGRADE_BUILD_TOOLS/apksigner" verify --verbose --print-certs "$apk" >"$UPGRADE_REPORT/$entry-signature.txt" 2>&1
  "$UPGRADE_BUILD_TOOLS/aapt" dump badging "$apk" >"$UPGRADE_REPORT/$entry-metadata.txt"
  shasum -a 256 "$apk" >"$UPGRADE_REPORT/$entry-sha256.txt"
done
UPGRADE_CERT=$(sed -n 's/^Signer #1 certificate SHA-256 digest: //p' "$UPGRADE_REPORT/baseline-signature.txt")
test -n "$UPGRADE_CERT" || exit 2
for entry in candidate probe; do
  test "$(sed -n 's/^Signer #1 certificate SHA-256 digest: //p' "$UPGRADE_REPORT/$entry-signature.txt")" = "$UPGRADE_CERT" ||
    { echo "Signing certificate mismatch: $entry" >&2; exit 2; }
done
for entry in baseline candidate; do
  grep -q "^package: name='be.wacken.planner' " "$UPGRADE_REPORT/$entry-metadata.txt" || exit 2
done
UPGRADE_OLD_CODE=$(sed -n "s/^package:.*versionCode='\([0-9]*\)'.*/\1/p" "$UPGRADE_REPORT/baseline-metadata.txt")
UPGRADE_NEW_CODE=$(sed -n "s/^package:.*versionCode='\([0-9]*\)'.*/\1/p" "$UPGRADE_REPORT/candidate-metadata.txt")
if [ "$#" = 6 ]; then
  test "$UPGRADE_NEW_CODE" -ge "$UPGRADE_OLD_CODE" || exit 2
else
  test "$UPGRADE_NEW_CODE" -gt "$UPGRADE_OLD_CODE" || { echo "Final candidate must have a higher versionCode." >&2; exit 2; }
fi
UPGRADE_WIFI=$("$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell settings get global wifi_on | tr -d '\r')
UPGRADE_FLIGHT=$("$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell settings get global airplane_mode_on | tr -d '\r')
UPGRADE_API=$("$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell getprop ro.build.version.sdk | tr -d '\r')
case "$UPGRADE_WIFI:$UPGRADE_FLIGHT" in [0123]:[01]) ;; *) echo "Emulator is not ready." >&2; exit 2;; esac
set_flight() {
  if [ "$UPGRADE_API" -ge 30 ]; then
    if [ "$1" = 1 ]; then action=enable; else action=disable; fi
    "$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell cmd connectivity airplane-mode "$action"
  else
    "$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell settings put global airplane_mode_on "$1"
    if [ "$1" = 1 ]; then action=true; else action=false; fi
    "$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell am broadcast -a android.intent.action.AIRPLANE_MODE --ez state "$action" >/dev/null
  fi
}
restore() {
  "$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell am force-stop be.wacken.planner >/dev/null 2>&1 || true
  set_flight "$UPGRADE_FLIGHT"
  if [ "$UPGRADE_WIFI" = 1 ] || [ "$UPGRADE_WIFI" = 3 ]; then action=enable; else action=disable; fi
  "$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell svc wifi "$action"
}
trap restore EXIT HUP INT TERM
set_flight 1
"$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell svc wifi disable
# Airplane mode disables cellular radio without relying on a SIM/phone service.
test "$("$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell settings get global airplane_mode_on | tr -d '\r')" = 1 || exit 2
"$UPGRADE_ADB" -s "$UPGRADE_SERIAL" install "$UPGRADE_BASELINE" >"$UPGRADE_REPORT/install-baseline.txt"
"$UPGRADE_ADB" -s "$UPGRADE_SERIAL" install "$UPGRADE_PROBE" >"$UPGRADE_REPORT/install-probe.txt"
probe_phase() {
  "$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell am instrument -w -e upgradePhase "$1" \
    be.wacken.planner.test/be.wacken.planner.SignedUpgradeInstrumentation >"$UPGRADE_REPORT/$1.txt"
  grep -q 'upgrade_result=PASS' "$UPGRADE_REPORT/$1.txt" &&
    grep -q 'INSTRUMENTATION_CODE: -1' "$UPGRADE_REPORT/$1.txt" ||
    { cat "$UPGRADE_REPORT/$1.txt" >&2; exit 1; }
}
probe_phase seed
# Red control: the same assertions must reject the old Room9 installation.
"$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell am instrument -w -e upgradePhase verify \
  be.wacken.planner.test/be.wacken.planner.SignedUpgradeInstrumentation >"$UPGRADE_REPORT/before-update-rejected.txt"
grep -q 'upgrade_result=FAIL' "$UPGRADE_REPORT/before-update-rejected.txt" &&
  grep -q 'Candidate migrated to Room12' "$UPGRADE_REPORT/before-update-rejected.txt" ||
  { cat "$UPGRADE_REPORT/before-update-rejected.txt" >&2; exit 1; }
"$UPGRADE_ADB" -s "$UPGRADE_SERIAL" shell am force-stop be.wacken.planner
"$UPGRADE_ADB" -s "$UPGRADE_SERIAL" install -r "$UPGRADE_CANDIDATE" >"$UPGRADE_REPORT/install-update.txt"
probe_phase verify
printf 'PASS: signed upgrade retained auth, metadata, festival identity, history, pending work and safe established provenance. versionCode %s -> %s.\n' "$UPGRADE_OLD_CODE" "$UPGRADE_NEW_CODE" | tee "$UPGRADE_REPORT/result.txt"
