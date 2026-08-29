## Context

Changes 01–04 delivered working list and item CRUD, bought toggling, and
two-section ordering. Change 05 is the polish + data-safety pass. Three
frictions drive it:

- **Irreversible list deletion** — the lists screen deletes on a single context-menu tap (ADR-0009 already flags the UI as the mitigation point for cascade deletion). Items are cheap to recreate; whole lists are not.
- **No per-list visibility** — `ListsUiState` carries only `ShoppingListEntity`s; the lists screen cannot show how much shopping each list still has left.
- **Backup config is a stub** — the manifest already declares `allowBackup`, `dataExtractionRules`, and `fullBackupContent`, but both XML files are untouched Android Studio templates, so the backup intent (ARCHITECTURE: disaster recovery MEDIUM) is not actually pinned to the database.

In-force ADRs constrain the approach: Room 3 reactive flow queries (0006),
stateless ViewModels reading storage as Flows (0008, single write path),
manual DI via `AppContainer` (0001), and items related to lists with cascade
delete + `list_id` index (0009). Navigation is type-safe (0007).

## Goals / Non-Goals

**Goals:**
- Deleting a list requires an explicit confirmation naming the list and its item count; deleting an item stays instant.
- The lists screen shows each list's total and bought item counts, updating live.
- The items screen top bar shows the opened list's name instead of a generic label.
- The Room database is explicitly included in auto-backup for both API < 31 and API 31+.
- All UI strings — new and existing — stay hardcoded in composables; no strings are externalized into `strings.xml`.
- Tests cover the new data-layer queries and updated ViewModel state.

**Non-Goals:**
- Externalizing any UI strings into `strings.xml` (the ARCHITECTURE resources principle is deferred; hardcoded strings remain, matching the current screens).
- Confirmation for deleting items.
- Backup of shared preferences (none exist) or any manual export/import (Change 06).
- Any schema migration or new dependencies.

## Decisions

### D1 — Per-list summary arrives as one reactive query, not a combine

`ShoppingListDao.observeListSummaries()` returns `Flow<List<ListSummary>>`
where `ListSummary(id, name, createdAt, totalCount, boughtCount)` comes from
one SELECT over `shopping_lists` with correlated subqueries on
`shopping_items`:

```sql
SELECT l.id AS id,
       l.name AS name,
       l.created_at AS createdAt,
       (SELECT COUNT(*) FROM shopping_items i WHERE i.list_id = l.id)
           AS totalCount,
       (SELECT COUNT(*) FROM shopping_items i WHERE i.list_id = l.id
            AND i.bought = 1) AS boughtCount
FROM shopping_lists l
ORDER BY l.created_at ASC, l.id ASC
```

**Why over the alternative (two flows + `combine` in the ViewModel):** a single
query emits names and counts atomically — no transient window where counts lag
a list change, no extra flow to wire. Room invalidates a SELECT on every table
it references, including subqueries, so item inserts, toggles, and deletions
re-emit the lists stream automatically. It also keeps the ViewModel a thin
mapping layer per ADR-0008. The `list_id` index from ADR-0009 keeps the
subqueries cheap at single-user scale.

**Consequences:** `ShoppingListRepository.observeLists()` changes its return
type to `Flow<List<ListSummary>>`; DAO method renamed from `observeAll()` to
`observeListSummaries()`. `FakeShoppingListRepository` and all tests that read
list streams are updated. UUID and boolean conversion reuse the existing
converters — no schema change.

### D2 — Items screen title reads the list name from storage

`ShoppingListDao.observeById(id): Flow<ShoppingListEntity?>` plus
`ShoppingListRepository.observeList(id)`. `ItemsViewModel` already observes the
item stream; it gains the list repository in its constructor and derives state
with `combine(observeItems(listId), observeList(listId)) { items, list ->
ItemsUiState(items, list?.name) }`.

**Why:** matches ADR-0008 (storage is the single source of truth; state is
derived, not held), and the two top bars then both show names — the concrete
consistency gap behind "final pass on UI consistency". **Alternative
rejected:** passing the name through navigation arguments would duplicate a
value that can change (e.g. after a rename) and is held in the wrong layer.

**Edge case:** navigating directly to a `null` list name renders an empty
title briefly; a single-user app reaches the items screen only from a live
list, so this is cosmetic.

### D3 — Delete confirmation is presentation-only

The lists screen holds `deleteTargetId` local state and renders an
`AlertDialog` (`Delete list "%1$s" and its %2$d items?`) whose confirm calls
the existing `onDeleteList(id)`. The ViewModel and repository write path are
unchanged.

**Why:** confirmation is a UI concern; the write path stays exactly the
long-press → delete flow (ADR-0008). **Alternative rejected:** threading
"confirm first" through the ViewModel would add VM state and tests for what
the composable already owns — dialog visibility lives in the screen, like the
existing create/rename dialogs.

### D4 — Auto-backup explicitly includes the database

- `backup_rules.xml` (API < 31): `<full-backup-content><include domain="database" path="."/></full-backup-content>`
- `data_extraction_rules.xml` (API 31+): a `<cloud-backup>` and a
  `<device-transfer>` block each include `domain="database" path="."`,
  replacing the template comments.

**Why:** default full-backup already covers database files, but the template
files declare nothing and may mislead; an explicit include under
`database` path `.` states intent, survives future `exclude` additions, and
covers the `shoplist.db` `-wal`/`-shm` sidecars. The manifest wiring
(`allowBackup`, `dataExtractionRules`, `fullBackupContent`) already exists from
scaffolding and stays.

### D5 — Extract the duplicated `NameDialog`; keep all strings hardcoded

Both screens carry a verbatim private `NameDialog`. It moves to a single
`ui/common/NameDialog.kt` consumed by both. All UI strings — the confirm-dialog
labels and the summary line included — stay hardcoded in the composables, in
line with every existing screen; nothing is added to `strings.xml`. This
deliberately defers the ARCHITECTURE "UI strings in resource files" principle
until a dedicated strings cleanup change.

## Risks / Trade-offs

- **Per-row correlated subqueries** cost one index scan over `shopping_items` per list per emission -> Mitigation: `list_id` index already exists (ADR-0009); realistic volumes (tens of lists × hundreds of items) are trivial on the A52. Not a correctness risk.
- **`observeLists()` name now returns summaries** -> Slight misnomer; the DAO method is renamed to `observeListSummaries()` to be honest, and the repository method keeps its name because it is the lists screen's observation contract.
- **"1 items" plural edge** in the confirm message -> Single-user English (localisation LOW, per ARCHITECTURE); accepted and noted, not wired to plural resources.
- **Delete confirmation has no automated UI test** -> ARCHITECTURE defers UI tests; verification is a manual step on the device plus the existing ViewModel-level delete tests.
- **Items-screen title absent for a missing list** -> Cosmetic, transient; no crash (nullable mapping).

## Migration Plan

No schema change, no data migration, no rollback risk. Deployment is a normal
incremental build; rollback is a revert of code without data impact. Backup
rule files only take effect on the next backup run.

## Open Questions

None. The in-force ADR set (0001–0009) fully constrains this design; no
supersession is proposed.