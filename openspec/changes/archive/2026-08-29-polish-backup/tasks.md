## 1. Data layer

- [x] 1.1 Add `ListSummary` data class (`id: UUID`, `name: String`, `createdAt: Long`, `totalCount: Int`, `boughtCount: Int`) in the data package
- [x] 1.2 Replace `ShoppingListDao.observeAll()` with `observeListSummaries(): Flow<List<ListSummary>>` using correlated subqueries over `shopping_items` (total via `COUNT(*)`, bought via `SUM(bought = 1)`), ordered `created_at ASC, id ASC` (ADR-0010)
- [x] 1.3 Add `observeById(id: UUID): Flow<ShoppingListEntity?>` to `ShoppingListDao`
- [x] 1.4 Update `ShoppingListRepository.observeLists()` return type to `Flow<List<ListSummary>>` (spec: lists observable with per-list summaries); add `observeList(id): Flow<ShoppingListEntity?>`
- [x] 1.5 Implement both in `RoomShoppingListRepository`

## 2. Test doubles + ViewModels

- [x] 2.1 Update `FakeShoppingListRepository`: `observeLists()` returns `ListSummary` rows, add `observeList(id)`, and seed item counts per list so summaries are deterministic
- [x] 2.2 Update `ListsViewModel` to map `ListSummary` rows into `ListsUiState`. `deleteList`/`createList`/`renameList` write paths unchanged (ADR-0008)
- [x] 2.3 Give `ItemsViewModel` the list repository in its constructor; derive `ItemsUiState(items, listName)` via `combine(observeItems(listId), observeList(listId))`
- [x] 2.4 Provide a lists-repository fake in the items test package for the new `ItemsViewModel` constructor

## 3. UI

- [x] 3.1 Extract the duplicated `NameDialog` into `ui/common/NameDialog.kt`; delete the private copies from `ListsScreen` and `ItemsScreen` (D5)
- [x] 3.2 `ListsScreen`/`ListRow`: render a secondary summary line from `ListSummary` (total items · bought count)
- [x] 3.3 `ListsScreen`: delete from the list context menu opens a confirmation dialog ("Delete list \"<name>\" and its <count> items?") instead of deleting immediately; confirm calls `onDeleteList(id)`, cancel closes (D3)
- [x] 3.4 `ItemsScreen`: top bar title renders `uiState.listName` instead of the hardcoded "Items"
- [x] 3.5 `App.kt`: pass `container.shoppingListRepository` into the `ItemsViewModel` initializer

## 4. Backup resources

- [x] 4.1 Keep all UI strings hardcoded in the composables (confirm-dialog labels, summary line); make no `strings.xml` additions
- [x] 4.2 `backup_rules.xml`: include `domain="database" path="."` for full backup (API < 31)
- [x] 4.3 `data_extraction_rules.xml`: include `domain="database" path="."` in both `<cloud-backup>` and `<device-transfer>`; strip template comments (API 31+)

## 5. Tests

- [x] 5.1 `ShoppingListDaoTest`: summaries carry total and bought counts, zero counts for empty lists, counts update on item insert/toggle/delete, and `observeById` emits/omits correctly
- [x] 5.2 `RoomShoppingListRepositoryTest`: `observeLists()` returns summaries; `observeList(id)` returns the list or nothing
- [x] 5.3 `ListsViewModelTest`: UI state exposes summaries from the fake; existing create/rename/delete and blank-name guards still pass
- [x] 5.4 `ListsViewModelIntegrationTest`: summary round-trip over real Room (lists + seeded items via the item DAO) reflects item mutations
- [x] 5.5 `ItemsViewModelTest`: UI state carries the list name via the combined flow; existing item ops unaffected
- [x] 5.6 `ItemsViewModelIntegrationTest`: real-Room combine surfaces both items and the list name

## 6. Verification

- [x] 6.1 Run `./gradlew test` (unit tests) and confirm green
- [x] 6.2 Run `./gradlew connectedDebugAndroidTest` (DAO/integration tests) on the target device and confirm green
- [x] 6.3 Manual device pass: delete-list confirmation dialog (confirm + cancel), per-list summary on the lists screen, and list name in the items top bar
- [x] 6.4 Run `openspec validate polish-backup --type change --strict` before archiving
