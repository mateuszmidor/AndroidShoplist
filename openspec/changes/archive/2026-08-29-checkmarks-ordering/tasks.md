## 1. Data layer

- [x] 1.1 Add `toggleBought(id: UUID)` query to `ShoppingItemDao`: `UPDATE shopping_items SET bought = NOT bought WHERE id = :id`
- [x] 1.2 Update `ShoppingItemDao.observeByList` ordering to `ORDER BY bought ASC, created_at ASC, id ASC`
- [x] 1.3 Add `toggleBought(id: UUID)` to the `ShoppingItemRepository` interface
- [x] 1.4 Implement `toggleBought` in `RoomShoppingItemRepository` by delegating to the DAO

## 2. Test double + ViewModel

- [x] 2.1 Extend `FakeShoppingItemRepository` with `toggleBought` and emit items in data-layer order (unchecked first by `createdAt`, then checked by `createdAt`) after every mutation
- [x] 2.2 Add `toggleItemBought(id: UUID)` to `ItemsViewModel`, delegating to `repository.toggleBought` in `viewModelScope` (write-path per ADR-0008)

## 3. UI

- [x] 3.1 Rewrite `ItemRow` as a `Row` with a Material3 `Checkbox` and the item text; tapping the row or the checkbox calls `onToggleBought(item.id)`; bought text renders struck-through and dimmed (`TextDecoration.LineThrough`, `onSurfaceVariant`); long-press context menu (rename/delete) unchanged
- [x] 3.2 Thread `onToggleBought: (UUID) -> Unit` through `ItemsScreen` and wire it to `viewModel::toggleItemBought` in `App.kt`

## 4. Tests

- [x] 4.1 `ShoppingItemDaoTest` (androidTest): toggle flips `bought` and preserves identity; mixed items emit unchecked-first-then-checked, each section by `createdAt`; unchecking restores an item to its creation-time position in the unchecked section
- [x] 4.2 `ItemsViewModelTest` (unit): `toggleItemBought` flips the flag in state; emitted state groups unchecked first then checked by creation time via the fake
- [x] 4.3 `ItemsViewModelIntegrationTest` (androidTest): full bought/unbought round-trip over real Room — toggle marks bought and reorders, toggle back restores original position, database reflects both transitions

## 5. Verification

- [x] 5.1 Run `./gradlew test` (unit tests) and confirm green
- [x] 5.2 Run `./gradlew connectedDebugAndroidTest` (DAO + integration tests) on the target device and confirm green
- [x] 5.3 Run `openspec validate checkmarks-ordering --type change --strict` before archiving