# items-screen

## Why

The items destination currently renders a placeholder that shows only the list
UUID. This change delivers the second real feature - the per-list items screen -
which proves the full UI -> ViewModel -> Repository -> Room pipeline for a
second, list-scoped screen and completes the two-screen navigation model
(ARCHITECTURE.md, Change 03 of TASKS.md).

## What Changes

- Create `ShoppingItemEntity` (`id: UUID`, `listId: UUID`, `name: String`,
  `bought: Boolean = false`, `createdAt: Long`) with a **cascade foreign key** to
  `shopping_lists.id`: deleting a list deletes its items.
- Create `ShoppingItemDao` with per-list `Flow` observations and CRUD operations.
- Create `ShoppingItemRepository` interface and `RoomShoppingItemRepository`
  implementation, mirroring the lists data layer.
- Wire `shoppingItemRepository` into `AppContainer`.
- Create `ItemsUiState` and a per-list `ItemsViewModel` exposing
  `StateFlow<ItemsUiState>`.
- Create the Items screen (LazyColumn) showing the items of one list, with a FAB
  (+) to add an item and a long-press context menu for rename/delete -
  mirroring the lists screen interaction pattern.
- Replace the placeholder items destination with the real items screen wired to
  `ItemsViewModel`, reusing the existing `Items(listId)` type-safe route; back
  navigation to lists already exists.
- Delete `PlaceholderItemsScreen`.
- Write JVM unit tests for `ItemsViewModel` and instrumented integration tests
  for the item data layer (DAO, repository, ViewModel over in-memory room).

## Capabilities

### New Capabilities

- `items-data`: Persistence and retrieval of item rows within a shopping list -
  created with a generated UUID, list scoping, creation-time ordering, rename,
  deletion, and cascade deletion when the owning list is removed.
- `items-screen`: The UI feature that displays the items of a selected list,
  adds new items, renames and deletes existing items via the established
  interaction pattern, and provides back navigation to the lists screen.

### Modified Capabilities

- none (list persistence and lists-screen behaviour are unchanged)

## Impact

- **New code**: `data/ShoppingItemEntity.kt`, `data/ShoppingItemDao.kt`,
  `data/ShoppingItemRepository.kt`, `data/RoomShoppingItemRepository.kt`,
  `ui/items/ItemsUiState.kt`, `ui/items/ItemsViewModel.kt`,
  `ui/items/ItemsScreen.kt`; test fakes and suites under `src/test` and
  `src/androidTest`.
- **Modified code**: `ShoppingDatabase` (add item entity + DAO), `AppContainer`
  (expose item repository), `ui/App.kt` (replace placeholder destination with the
  real items screen + ViewModel).
- **Removed code**: `ui/items/PlaceholderItemsScreen.kt`.
- **New dependencies**: none. **Schema change**: new `shopping_items` table; no
  migration (schema version stays 1).
- **Tests**: JVM `ItemsViewModelTest` (fake repository); instrumented
  `ShoppingItemDaoTest`, `RoomShoppingItemRepositoryTest`,
  `ItemsViewModelIntegrationTest`. No UI automation tests (deferred).
- **Not in scope**: bought toggle UI and bought/unbought ordering (Change 04) -
  the `bought` column exists in the schema but is not surfaced in the UI here;
  `strings.xml` extraction (Change 05).
