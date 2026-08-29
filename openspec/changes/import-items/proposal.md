# import-items

## Why

The user is migrating their everyday grocery lists over from the Listonic app.
Today the items screen only supports adding items one at a time via the FAB, so
moving an existing list means retyping every item. This change lets the user
append a whole exported shopping list at once by pasting the plain-text lines
Listonic produces.

## What Changes

- Add a pure, JVM-testable parser (`domain` layer) that converts pasted text
  into item names: one per line, leading bullet stripped (from `•`, `-`, `*`),
  surrounding whitespace trimmed, blank lines skipped, duplicates kept, and any
  `/` inside a name preserved verbatim.
- Add an atomic batch-append operation to the item data layer
  (`createAll(listId, names)`): every parsed item is inserted in a single Room
  transaction (all-or-nothing), each with its own UUID and a sequentially
  increasing creation timestamp so imported items keep their pasted order under
  the existing `created_at` sort instead of falling back to random UUID order.
- Turn the items screen FAB into a small anchored menu with two actions: "Add
  item" (existing create dialog) and "Import from Listonic".
- Add an import dialog with a multi-line paste field. The target is always the
  currently open list (no list selector). The confirm button stays disabled
  while the pasted text yields no items.
- Wire `ItemsViewModel.importItems(text)` to parse the text and delegate to the
  batch append; an empty parse result is a no-op that never reaches the data
  layer.
- No Room schema change (insert-only), no new dependencies, no migration.

## Capabilities

### New Capabilities

- `items-import`: Importing a set of items into an existing shopping list from
  pasted plain text — parsing pasted lines into item names, atomically
  appending them to the target list, and the items-screen entry points (FAB
  menu + import dialog) that drive it.

### Modified Capabilities

- none (single new capability covers this change end-to-end)

## Impact

- **New code**: `domain/ListonicImportParser`; a private multi-line import
  dialog and FAB menu in `ui/items/ItemsScreen.kt`;
  `ItemsViewModel.importItems`.
- **Modified code**: `data/ShoppingItemDao` (`insertAll`),
  `data/ShoppingItemRepository` + `data/RoomShoppingItemRepository`
  (`createAll`), `ui/items/ItemsViewModel.kt`, `ui/items/ItemsScreen.kt`;
  test doubles (`FakeShoppingItemRepository` gains `createAll`).
- **Removed code**: none.
- **New dependencies**: none. **Schema change**: none (Room version stays 1).
- **Tests**: JVM `ListonicImportParserTest`; JVM `ItemsViewModelTest` additions
  for the import action (success + blocked cases); instrumented
  `RoomShoppingItemRepositoryTest` additions for the atomic batch append
  (including an all-or-nothing rollback case). No UI automation tests
  (deferred).
- **Not in scope**: importing from the lists screen; deduplicating against
  existing list items; quantities/units; `strings.xml` extraction; a UI-level
  import summary/snackbar.