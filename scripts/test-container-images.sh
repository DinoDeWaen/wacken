#!/usr/bin/env bash
# Immutable official images verified against both registries on 2026-10-17.
# This file is sourced by disposable test runners; it does not change production.
verified_test_image() {
  local reference="$1" identities
  identities=$(docker image inspect "$reference" --format '{{json .RepoDigests}}' 2>/dev/null) || return 1
  python3 -c 'import json,sys
expected=sys.argv[1].split("@",1)[1]
try:
    matches=any(value.split("@",1)[-1]==expected for value in json.loads(sys.argv[2]) or [])
except (ValueError,TypeError):
    matches=False
sys.exit(0 if matches else 1)' "$reference" "$identities"
}

acquire_test_image() {
  local primary="$1" mirror="$2" reference
  for reference in "$primary" "$mirror"; do
    if verified_test_image "$reference"; then printf '%s\n' "$reference"; return 0; fi
  done
  # Two sources, one attempt each. Never downgrade, change version, or skip a gate.
  for reference in "$primary" "$mirror"; do
    if docker pull "$reference" >&2 && verified_test_image "$reference"; then
      printf '%s\n' "$reference"; return 0
    fi
  done
  printf 'Could not acquire verified test image from either official registry: %s\n' "$primary" >&2
  return 1
}

prepare_test_images() {
  local architecture database_digest api_digest
  architecture=$(docker info --format '{{.Architecture}}') || return 1
  case "$architecture" in
    aarch64|arm64)
      database_digest=sha256:f20491444ff7818267e652ced923991fdab24fc07e0eb2bacfba26ab72a45563
      api_digest=sha256:60f641667a5e0a024c2c990cf1d1c01a49996bad849d7b57f16afd97d486ade2 ;;
    x86_64|amd64)
      database_digest=sha256:d5424e9f4d0c21d63991c3f3dd77881f23f722c3a212452a61bc91924b79e8c8
      api_digest=sha256:bb289d00570b569525e1e22fb77c49cd17c17ca3f41da9ee2b4351a35027551b ;;
    *) printf 'Unsupported Docker architecture: %s\n' "$architecture" >&2; return 1 ;;
  esac
  WACKEN_TEST_DB_IMAGE=$(acquire_test_image "public.ecr.aws/supabase/postgres@$database_digest" "docker.io/supabase/postgres@$database_digest") || return 1
  WACKEN_TEST_API_IMAGE=$(acquire_test_image "public.ecr.aws/supabase/postgrest@$api_digest" "docker.io/postgrest/postgrest@$api_digest") || return 1
}
