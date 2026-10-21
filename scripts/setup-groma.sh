#!/bin/sh
set -eu
root=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
bun_bin=${BUN:-bun}
[ "$("$bun_bin" --version)" = "1.4.3" ] || { echo 'Bun 1.4.3 is required.' >&2; exit 1; }
patch="$root/tools/groma/container-fix.patch"
pin="$root/tools/groma/pin.json"
expected=$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["patchSha256"])' "$pin")
actual=$(shasum -a 256 "$patch" | cut -d ' ' -f 1)
[ "$actual" = "$expected" ] || { echo 'Groma patch checksum mismatch.' >&2; exit 1; }
cache="$root/.local/groma"
mkdir -p "$cache"
build=$(mktemp -d "$cache/build.XXXXXX")
commit=$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["commit"])' "$pin")
git clone --quiet https://github.com/MrLesk/groma.md.git "$build/source"
git -C "$build/source" checkout --quiet "$commit"
git -C "$build/source" apply "$patch"
(cd "$build/source" && "$bun_bin" install --frozen-lockfile && "$bun_bin" scripts/build.ts "$cache/groma.next")
mv "$cache/groma.next" "$cache/groma"
cp "$pin" "$cache/pin.json"
(cd "$cache" && shasum -a 256 groma > binary.sha256)
echo "Built pinned Groma at $cache/groma"
echo "Source checkout: $build/source"
