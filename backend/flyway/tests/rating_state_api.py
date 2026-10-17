"""Real HTTP contract checks for the disposable rating-state database."""
import base64
import concurrent.futures
import hashlib
import hmac
import json
import os
import time
import urllib.error
import urllib.request
import uuid

base = os.environ["WACKEN_RATING_API"]
secret = "rating-state-http-fixture-secret-never-use-outside-tests"
owner = "11111111-1111-1111-1111-111111111111"


def token(user):
    def encode(value):
        return base64.urlsafe_b64encode(json.dumps(value, separators=(",", ":")).encode()).rstrip(b"=")
    data = encode({"alg": "HS256", "typ": "JWT"}) + b"." + encode(
        {"role": "authenticated", "sub": user, "exp": int(time.time()) + 300})
    return (data + b"." + base64.urlsafe_b64encode(hmac.new(secret.encode(), data, hashlib.sha256).digest()).rstrip(b"=")).decode()


def request(path, user=owner, payload=None):
    headers = {"Authorization": "Bearer " + token(user), "Content-Type": "application/json"}
    req = urllib.request.Request(base + path, headers=headers,
        data=json.dumps(payload).encode() if payload is not None else None)
    try:
        with urllib.request.urlopen(req, timeout=15) as response:
            return response.status, json.load(response)
    except urllib.error.HTTPError as error:
        return error.code, json.load(error)


def command(value, operation=None):
    return {"p_operation_id": operation or str(uuid.uuid4()), "p_festival_id": "rating-active",
            "p_band_id": "rating-band", "p_rating": value, "p_recorded_at": "2026-10-17T15:00:00Z"}


# Identical concurrent/repeated requests confirm one positive history event.
positive = command(5)
with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
    results = list(pool.map(lambda _: request("/rpc/save_festival_after_rating", payload=positive), range(4)))
assert all(status == 200 and body["confirmed"] for status, body in results), results
status, events = request("/personal_band_rating_events?select=id&id=eq." + positive["p_operation_id"])
assert status == 200 and len(events) == 1, (status, events)

# A reset survives a late retry of the earlier operation.
reset = command(0)
assert request("/rpc/save_festival_after_rating", payload=reset)[0] == 200
assert request("/rpc/save_festival_after_rating", payload=positive)[0] == 200
status, after = request("/festival_after_ratings?select=rating,requires_review,band:bands(name)&festival_id=eq.rating-active")
assert status == 200 and after == [{"rating": 0, "requires_review": False, "band": {"name": "Rating Band"}}], (status, after)
status, current = request("/personal_band_ratings?select=rating,source_festival_id,band:bands(name)")
assert status == 200 and current[0]["rating"] == 4 and current[0]["source_festival_id"] == "rating-archived", (status, current)

# RLS and the guarded RPC isolate owners and reject outsiders/archived edits.
status, rows = request("/festival_after_ratings?select=rating", "22222222-2222-2222-2222-222222222222")
assert status == 200 and rows == [], (status, rows)
member_payload = command(4)
assert request("/rpc/save_festival_after_rating", "22222222-2222-2222-2222-222222222222", member_payload)[0] == 200
status, rows = request("/festival_after_ratings?select=rating&festival_id=eq.rating-active", "22222222-2222-2222-2222-222222222222")
assert status == 200 and rows == [{"rating": 4}], (status, rows)
status, rows = request("/festival_after_ratings?select=rating&festival_id=eq.rating-active")
assert status == 200 and rows == [{"rating": 0}], (status, rows)
assert request("/rpc/save_festival_after_rating", "33333333-3333-3333-3333-333333333333", command(3))[0] == 403
archived = command(3)
archived["p_festival_id"] = "rating-archived"
assert request("/rpc/save_festival_after_rating", payload=archived)[0] == 403
print("Real PostgREST rating API: concurrent replay, durable reset, canonical joins, no promotion, owner isolation and denied edits passed.")

# Archive retries are identical and promote only the final nonempty member value.
assert request("/rpc/archive_festival", "33333333-3333-3333-3333-333333333333",
               {"p_festival_id": "rating-active"})[0] == 403
with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
    archives = list(pool.map(lambda _: request("/rpc/archive_festival",
        payload={"p_festival_id": "rating-active"}), range(4)))
assert all(status == 200 and body["confirmed"] for status, body in archives), archives
assert all(body == archives[0][1] for _, body in archives), archives
status, current = request("/personal_band_ratings?select=rating,source_festival_id")
assert status == 200 and current == [{"rating": 4, "source_festival_id": "rating-archived"}], (status, current)
status, current = request("/personal_band_ratings?select=rating,source_festival_id",
                         "22222222-2222-2222-2222-222222222222")
assert status == 200 and current == [{"rating": 4, "source_festival_id": "rating-active"}], (status, current)
assert request("/rpc/save_festival_after_rating", payload=command(1))[0] == 403

# A new planning snapshot keeps the established opinion, and old archive replay
# cannot overwrite the newer source after another complete festival.
new_festival = {"p_request_id": str(uuid.uuid4()),
    "p_group_id": "00000000-0000-0000-0000-000000000001",
    "p_festival_id": "archive-race", "p_festival_name": "Archive Race", "p_band_names": ["Rating Band"]}
status, created = request("/rpc/create_festival_from_bands", payload=new_festival)
assert status == 200 and created["planning_ratings"] == [{"band_name": "Rating Band", "rating": 4}], (status, created)
before_race = command(2)
before_race["p_festival_id"] = "archive-race"
assert request("/rpc/save_festival_after_rating", payload=before_race)[0] == 200
racing = command(5)
racing["p_festival_id"] = "archive-race"
with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
    edit = pool.submit(request, "/rpc/save_festival_after_rating", payload=racing)
    archive = pool.submit(request, "/rpc/archive_festival", payload={"p_festival_id": "archive-race"})
    edited, archived_result = edit.result(), archive.result()
assert archived_result[0] == 200, archived_result
assert edited[0] in (200, 403), edited
expected = 5 if edited[0] == 200 else 2
status, current = request("/personal_band_ratings?select=rating,source_festival_id")
assert status == 200 and current == [{"rating": expected, "source_festival_id": "archive-race"}], (status, current)
assert request("/rpc/archive_festival", payload={"p_festival_id": "rating-active"})[0] == 200
status, current = request("/personal_band_ratings?select=rating,source_festival_id")
assert current == [{"rating": expected, "source_festival_id": "archive-race"}], current
print("Real PostgREST archive API: concurrent replay, member promotion, reset retention, edit race and old-replay source protection passed.")

# Corrections use reviewed revisions and immutable audit; concurrent edits cannot
# silently overwrite a value reviewed before a different correction.
status, review = request("/rpc/review_archived_rating", payload={
    "p_festival_id": "archive-race", "p_band_id": "rating-band", "p_kind": "AFTER"})
assert status == 200 and review["rating"] == expected, (status, review)
new_value = 1 if expected != 1 else 3
correction = {"p_operation_id": str(uuid.uuid4()), "p_festival_id": "archive-race",
    "p_band_id": "rating-band", "p_kind": "AFTER", "p_expected_rating": expected,
    "p_expected_revision": review["revision"], "p_new_rating": new_value}
with concurrent.futures.ThreadPoolExecutor(max_workers=3) as pool:
    replies = list(pool.map(lambda _: request("/rpc/correct_archived_rating", payload=correction), range(3)))
assert all(status == 200 and body["confirmed"] for status, body in replies), replies
assert all(body == replies[0][1] for _, body in replies), replies
status, audit = request("/rating_corrections?select=previous_value,new_value,original_recorded_at,corrected_at")
assert status == 200 and len(audit) == 1, (status, audit)
assert audit[0]["previous_value"] == expected and audit[0]["new_value"] == new_value, audit
assert audit[0]["original_recorded_at"] == review["recorded_at"], (audit, review)
status, current = request("/personal_band_ratings?select=rating,source_festival_id")
assert current == [{"rating": new_value, "source_festival_id": "archive-race"}], current
stale = dict(correction, p_operation_id=str(uuid.uuid4()), p_new_rating=4)
assert request("/rpc/correct_archived_rating", payload=stale)[0] == 400
status, review = request("/rpc/review_archived_rating", payload={
    "p_festival_id": "archive-race", "p_band_id": "rating-band", "p_kind": "AFTER"})
first = dict(correction, p_operation_id=str(uuid.uuid4()), p_expected_rating=new_value,
             p_expected_revision=review["revision"], p_new_rating=4)
second = dict(first, p_operation_id=str(uuid.uuid4()), p_new_rating=2)
with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
    one = pool.submit(request, "/rpc/correct_archived_rating", payload=first)
    two = pool.submit(request, "/rpc/correct_archived_rating", payload=second)
    competing = [one.result(), two.result()]
assert sorted(status for status, _ in competing) == [200, 400], competing
status, private_audit = request("/rating_corrections?select=operation_id",
                               "22222222-2222-2222-2222-222222222222")
assert status == 200 and private_audit == [], (status, private_audit)
status, current_before = request("/personal_band_ratings?select=rating,source_festival_id")
status, old = request("/rpc/review_archived_rating", payload={
    "p_festival_id": "rating-active", "p_band_id": "rating-band", "p_kind": "AFTER"})
old_correction = dict(correction, p_operation_id=str(uuid.uuid4()), p_festival_id="rating-active",
    p_expected_rating=old["rating"], p_expected_revision=old["revision"], p_new_rating=3)
assert request("/rpc/correct_archived_rating", payload=old_correction)[0] == 200
status, current_after = request("/personal_band_ratings?select=rating,source_festival_id")
assert current_before == current_after, (current_before, current_after)
print("Real PostgREST corrections: reviewed revision, concurrent conflict, audit/replay, original date, owner isolation and newer-source preservation passed.")
