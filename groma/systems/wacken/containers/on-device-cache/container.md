---
type: C4 Container
title: On-device cache
status: stable
groma:
  id: on-device-cache
  parent: wacken
  technology: Room / SQLite
---

Owned on-device wacken-cache.db storage. Room entities, DAOs, migrations and repositories execute inside the Android application; this storage boundary does not own their Java source. WackenDatabase.get opens the cache through Room.databaseBuilder. The Java scanner does not model storage execution.
