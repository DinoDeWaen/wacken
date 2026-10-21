#!/bin/sh
set -eu
root=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cache="$root/.local/groma"
[ -x "$cache/groma" ] && cmp -s "$root/tools/groma/pin.json" "$cache/pin.json" || {
  echo 'Build the pinned Groma tool with sh scripts/setup-groma.sh (Bun 1.4.3 required).' >&2
  exit 1
}
expected=$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["patchSha256"])' "$root/tools/groma/pin.json")
actual=$(shasum -a 256 "$root/tools/groma/container-fix.patch" | cut -d ' ' -f 1)
[ "$actual" = "$expected" ] || { echo 'Groma patch checksum mismatch; review the pin.' >&2; exit 1; }
(cd "$cache" && shasum -a 256 -c binary.sha256 >/dev/null)
exec "$cache/groma" "$@"
