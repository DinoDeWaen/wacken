#!/bin/sh
# Offline native smoke checks on an explicitly selected Android emulator.
set -eu
WACKEN_TEST_SERIAL=${1:-emulator-5554}
WACKEN_TEST_CLASS=${2:-be.wacken.planner.CriticalFlowsAndroidTest}
WACKEN_ADB=${ANDROID_ADB:-$(command -v adb || printf '%s' "$HOME/Library/Android/sdk/platform-tools/adb")}
case "$WACKEN_TEST_SERIAL" in emulator-*) ;; *) echo "Select an emulator serial; this runner changes only its test network." >&2; exit 2;; esac
WACKEN_CONNECTED=$("$WACKEN_ADB" devices | awk 'NR>1 && $2 == "device" {print $1}')
if [ "$WACKEN_CONNECTED" != "$WACKEN_TEST_SERIAL" ]; then echo "Connect only the selected test emulator before running." >&2; exit 2; fi
if [ "$("$WACKEN_ADB" -s "$WACKEN_TEST_SERIAL" shell getprop ro.kernel.qemu | tr -d '\r')" != 1 ]; then
  echo "The selected target is not an emulator." >&2; exit 2
fi
WACKEN_WIFI=$("$WACKEN_ADB" -s "$WACKEN_TEST_SERIAL" shell settings get global wifi_on | tr -d '\r')
WACKEN_DATA=$("$WACKEN_ADB" -s "$WACKEN_TEST_SERIAL" shell settings get global mobile_data | tr -d '\r')
case "$WACKEN_WIFI:$WACKEN_DATA" in [012]:[01]) ;; *) echo "Emulator settings are not ready; wait for boot before running." >&2; exit 2;; esac
restore_network() {
  if [ "$WACKEN_WIFI" = 1 ] || [ "$WACKEN_WIFI" = 2 ]; then "$WACKEN_ADB" -s "$WACKEN_TEST_SERIAL" shell svc wifi enable; fi
  if [ "$WACKEN_DATA" = 1 ]; then "$WACKEN_ADB" -s "$WACKEN_TEST_SERIAL" shell svc data enable; fi
}
trap restore_network EXIT HUP INT TERM
"$WACKEN_ADB" -s "$WACKEN_TEST_SERIAL" shell svc wifi disable
"$WACKEN_ADB" -s "$WACKEN_TEST_SERIAL" shell svc data disable
if [ -z "${JAVA_HOME:-}" ] && [ "$(uname)" = Darwin ]; then
  JAVA_HOME=$(/usr/libexec/java_home -v 21)
  export JAVA_HOME
fi
set -- :app:connectedDebugAndroidTest
if [ "$WACKEN_TEST_CLASS" != all ]; then set -- "$@" "-Pandroid.testInstrumentationRunnerArguments.class=$WACKEN_TEST_CLASS"; fi
ANDROID_SERIAL="$WACKEN_TEST_SERIAL" ./gradlew "$@"
