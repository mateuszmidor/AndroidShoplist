## Why

Change 05 of the roadmap is the polish pass: the core list/item CRUD plus bought markings work (Changes 01–04), but two workflows are unpolished and data safety is only half-configured. Deleting a list is one irreversible tap away (an item deletion is cheap to recreate, a whole list is not), the lists screen gives no idea which list still has shopping left, and the auto-backup XML rules are still Android Studio sample templates, so the app's declared backup intent is not actually pinned down.

## What Changes

- Deleting a **list** now requires an explicit confirmation dialog that names the list and its item count; deleting an **item** stays a single tap, as before.
- Each **row on the lists screen** shows a per-list summary — total item count and bought count — updated live as items are added, checked, or removed.
- The **items screen top bar** shows the opened list's name instead of the generic "Items" title.
- The **auto-backup rules** (`backup_rules.xml` for API < 31, `data_extraction_rules.xml` for API 31+) are populated to include the Room database (`shoplist.db`), replacing the template placeholders.
- The duplicated `NameDialog` composable is extracted into a single shared component used by both screens.
- All UI strings — the ones added here (confirm-dialog text, summary line) included — are hardcoded in the composables, consistent with the existing screens; nothing is externalized into `strings.xml`.

## Capabilities

### New Capabilities

None — all behaviour lands in existing capabilities.

### Modified Capabilities

- `list-persistence`: The list observation stream now returns a per-list summary (list fields plus total and bought item counts) from one reactive query, and a new observe-by-id operation is added for the items screen title.
- `lists-screen`: Deleting a list requires confirmation before removal; each list row displays a "total items · bought" summary line that stays live as items change.
- `items-screen`: The top bar title of the items screen shows the opened list's name rather than the generic "Items" label.

## Impact

- **Data layer**: `ShoppingListDao` — `observeAll()` becomes `observeListSummaries()` (correlated subqueries over `shopping_items`) and gains `observeById(id)`; new `ListSummary` row type; `ShoppingListRepository.observeLists()` return type changes to `Flow<List<ListSummary>>` (interface change ripples into `RoomShoppingListRepository`, `FakeShoppingListRepository`, VM tests, integration tests) and gains `observeList(id)`.
- **ViewModel**: `ListsViewModel` maps summaries; `ItemsViewModel` gains the list repository in its constructor and combines the item stream with the list-name stream.
- **UI**: `ListsScreen` (summary line, delete-confirm dialog), `ItemsScreen` (title from state, shared `NameDialog`), new shared `NameDialog` in `ui/common`, `App.kt` constructor wiring.
- **Resources**: `backup_rules.xml` + `data_extraction_rules.xml` (database include). No `strings.xml` changes — all strings stay hardcoded.
- **Tests**: DAO + repository integration tests for summaries and observe-by-id; unit + integration tests for both ViewModels updated/extended.
- **No schema migration** (query- and UI-only). **No new dependencies**.

## Out of Scope

- Externalizing UI strings into `strings.xml` (the ARCHITECTURE resources principle is deferred; all strings stay hardcoded for now).
- Confirmation for deleting items (deliberately kept instant).
- Manual export/import of lists (Change 06 territory).