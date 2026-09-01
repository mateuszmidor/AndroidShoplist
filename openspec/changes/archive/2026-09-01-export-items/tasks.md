# Implementation Tasks — export-items

## 1. Domain formatter (pure business logic, ADR-0011)

- [x] 1.1 Create `domain/ListonicExportFormatter.kt` in package
  `org.mateuszmidor.shoplist.domain` exposing `object ListonicExportFormatter`
  with `fun format(items: List<ShoppingItemEntity>): String` (pure, no Android
  types, no IO)
- [x] 1.2 Implement the formatting rule: one item per line, each line a `• `
  bullet prefix followed by the item's name, joining all items in the given
  order; a blank item name still yields its bullet-prefixed line, and an empty
  list yields an empty string

## 2. Data layer: one-shot snapshot query

- [x] 2.1 Add a non-reactive suspend query to `ShoppingItemDao` that selects all
  items for a `list_id`, ordered `bought ASC, created_at ASC` (same display order
  as the reactive Flow, ADR-0006)
- [x] 2.2 Add `suspend fun getAllByList(listId: UUID): List<ShoppingItemEntity>`
  to the `ShoppingItemRepository` interface
- [x] 2.3 Implement `getAllByList` in `RoomShoppingItemRepository` by delegating
  to the new DAO query

## 3. ViewModel export action

- [x] 3.1 Widen the `ListsViewModel` constructor to also take
  `ShoppingItemRepository` and a `ClipboardManager`/`Context` (ADR-0001, ADR-0008):
  rename the existing `repository` parameter to `listRepository` and add the
  item repository plus the clipboard/context needed for copy + Toast
- [x] 3.2 Add `fun exportListItems(listId: UUID)` to `ListsViewModel`: fetch
  items via `itemRepository.getAllByList`, format with
  `ListonicExportFormatter`, copy the result to the clipboard (labelled
  "ShopList"), and show a Toast confirmation — all inside `viewModelScope`
- [x] 3.3 Guard the action so an empty item list still exports (an empty string
  is copied) rather than being silently dropped

## 4. Lists screen entry point

- [x] 4.1 Add an "Export items" `DropdownMenuItem` to the long-press context
  menu in `ListsScreen`, alongside Rename and Delete, and route it through a new
  `onExportItems(listId)` callback that closes the menu
- [x] 4.2 Add the `onExportItems: (UUID) -> Unit` parameter to `ListsScreen` and
  thread it through `ListContent`/`ListRow`
- [x] 4.3 Obtain the clipboard/context at the wiring point and pass
  `onExportItems = viewModel::exportListItems` when rendering the lists screen
  in `App.kt`

## 5. Unit tests (JVM)

- [x] 5.1 Write `ListonicExportFormatterTest`: bullet-prefixes every line in the
  given order, preserves a slash in a name, includes all items regardless of any
  `bought` marker, renders a blank item name as its bullet line, and produces an
  empty string for an empty list
- [x] 5.2 Extend `FakeShoppingItemRepository` with `getAllByList` using the
  existing order semantics used by `observeItems`
- [x] 5.3 Extend `ListsViewModelTest`: `exportListItems` calls
  `getAllByList`, formats the fetched items, and writes the expected text to the
  clipboard; an empty item set still copies an empty string

## 6. Integration tests (instrumented, in-memory Room)

- [x] 6.1 Extend `RoomShoppingItemRepositoryTest`: `getAllByList` returns only
  items scoped to the given list, in `bought ASC, created_at ASC` order, and
  returns an empty list for a list with no items

## 7. Verification

- [x] 7.1 Run `make build` and confirm the app compiles
- [x] 7.2 Run `make test` (JVM unit tests) with all green
- [x] 7.3 Run `make connectedTest` (instrumented tests) with all green
- [x] 7.4 Run `./gradlew lint` and resolve any new issues
- [x] 7.5 Run `openspec validate export-items --type change --strict` and
  resolve any issues
- [x] 7.6 Manual smoke check on the target device: long-press a list, verify the
  context menu shows Export items alongside Rename and Delete, export a list with
  mixed bought/unbought items, and validate the clipboard text is bullet-prefixed
  and round-trips back through the import dialog