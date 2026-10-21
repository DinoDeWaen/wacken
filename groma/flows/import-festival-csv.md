---
type: Groma Flow
title: Review local festival CSV
groma:
  id: import-festival-csv
---

The admin reviews five optional local CSV files against an active festival snapshot. Only explicitly accepted valid rows are applied, missing companion files remain unchanged, and stale data or combined-reference errors reject publication. This is the TSV fallback/local Room workflow, with no shared-source import API. Canonical catalog records, other festivals, ratings/history and pending work are retained. A Room failure rolls back its transaction and restores TSV backups; restoration failure keeps the backup and reports recovery information.

## Steps

| From | To | Action |
| --- | --- | --- |
| [festival-data-admin](../actors/festival-data-admin.md) | [importcsvactivity](../systems/wacken/containers/android-application/components/importcsvactivity.md) | Select local master-data CSV inputs |
| [importcsvactivity](../systems/wacken/containers/android-application/components/importcsvactivity.md) | [localcsvimportreviewdialog](../systems/wacken/containers/android-application/components/localcsvimportreviewdialog.md) | Open retained review with unchecked row choices |
| [localcsvimportreviewdialog](../systems/wacken/containers/android-application/components/localcsvimportreviewdialog.md) | [reviewfestivalcsvusecase](../systems/wacken/containers/android-application/components/reviewfestivalcsvusecase.md) | Review changes and explicitly apply accepted keys |
| [reviewfestivalcsvusecase](../systems/wacken/containers/android-application/components/reviewfestivalcsvusecase.md) | [tsvimporttransaction](../systems/wacken/containers/android-application/components/tsvimporttransaction.md) | Revalidate combined data and stale snapshot within supplied transaction |
| [tsvimporttransaction](../systems/wacken/containers/android-application/components/tsvimporttransaction.md) | [wackendatabase](../systems/wacken/containers/android-application/components/wackendatabase.md) | Run replacement in supplied Room transaction with TSV backup protection |
| [reviewfestivalcsvusecase](../systems/wacken/containers/android-application/components/reviewfestivalcsvusecase.md) | [syncedbandrepository](../systems/wacken/containers/android-application/components/syncedbandrepository.md) | Publish accepted master data inside that transaction |
| [syncedbandrepository](../systems/wacken/containers/android-application/components/syncedbandrepository.md) | [filebackedstorage](../systems/wacken/containers/android-application/components/filebackedstorage.md) | Replace local TSV data while preserving canonical catalog |
| [syncedbandrepository](../systems/wacken/containers/android-application/components/syncedbandrepository.md) | [wackendatabase](../systems/wacken/containers/android-application/components/wackendatabase.md) | Publish matching Room cache or roll back on failure |
