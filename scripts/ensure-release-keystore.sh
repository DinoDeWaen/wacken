#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DEFAULT_STORE_FILE="$ROOT_DIR/.local/release/wacken-release.jks"

read_secret() {
  local service="$1"
  security find-generic-password -s "$service" -w 2>/dev/null || true
}

write_secret() {
  local service="$1"
  local value="$2"
  while security delete-generic-password -s "$service" >/dev/null 2>&1; do
    :
  done
  security add-generic-password -U -s "$service" -a "$USER" -w "$value" >/dev/null
}

store_file="$(read_secret WACKEN_RELEASE_STORE_FILE)"
store_password="$(read_secret WACKEN_RELEASE_STORE_PASSWORD)"
key_alias="$(read_secret WACKEN_RELEASE_KEY_ALIAS)"
key_password="$(read_secret WACKEN_RELEASE_KEY_PASSWORD)"

if [[ -z "$store_password" || -z "$key_alias" || -z "$key_password" ]]; then
  echo "Missing release signing secrets in macOS keychain." >&2
  echo "Required services: WACKEN_RELEASE_STORE_PASSWORD, WACKEN_RELEASE_KEY_ALIAS, WACKEN_RELEASE_KEY_PASSWORD" >&2
  exit 1
fi

if [[ -z "$store_file" || "$store_file" == /private/tmp/* || "$store_file" == /tmp/* || ! -f "$store_file" ]]; then
  store_file="$DEFAULT_STORE_FILE"
  write_secret WACKEN_RELEASE_STORE_FILE "$store_file"
fi

mkdir -p "$(dirname "$store_file")"

if [[ ! -f "$store_file" ]]; then
  keytool -genkeypair \
    -storetype PKCS12 \
    -keystore "$store_file" \
    -storepass "$store_password" \
    -alias "$key_alias" \
    -keypass "$key_password" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -dname "CN=Wacken Planner 2026, OU=Release, O=Wacken Planner, L=Belgium, ST=Belgium, C=BE" \
    >/dev/null
  echo "Generated release keystore at $store_file"
else
  keytool -list \
    -keystore "$store_file" \
    -storepass "$store_password" \
    -alias "$key_alias" \
    >/dev/null
  echo "Verified release keystore at $store_file"
fi

echo "WACKEN_RELEASE_STORE_FILE=$store_file"
