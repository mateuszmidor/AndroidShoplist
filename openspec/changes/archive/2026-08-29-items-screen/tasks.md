# Implementation Tasks — items-screen

## 1. Item data layer (entity, DAO, repository)

- [x] 1.1 Create `ShoppingItemEntity` (`data/ShoppingItemEntity.kt`) with
  `@Entity(tableName = "shopping_items")`: `id: UUID` `@PrimaryKey`, `listId:
  UUID` `@ColumnInfo(index = true)` with `@ForeignKey(entity =
  ShoppingListEntity::class, parentColumns = ["id"], childColumns = ["listId"],
  onDelete = CASCADE)`, `name: String`, `bought: Boolean = false`
  `@ColumnInfo("bought")`, `createdAt: Long` `@ColumnInfo("created_at")`
- [x] 1.2 Create `ShoppingItemDao` (`data/ShoppingItemDao.kt`) mirroring
  `ShoppingListDao`: `observeByList(listId): Flow<List<ShoppingItemEntity>>`
  (`WHERE list_id = :listId ORDER BY created_at, id`), `@Insert insert`, and
  `renameById(id, name)` / `deleteById(id)` by item UUID
- [x] 1.3 Create `ShoppingItemRepository` interface mirroring
  `ShoppingListRepository`: `observeItems(listId)`, `create(listId, name): UUID`,
  `rename(id, name)`, `delete(id)`
- [x] 1.4 Create `RoomShoppingItemRepository` delegating to the DAO; `create`
  generates a UUID and `createdAt`; mirror `RoomShoppingListRepository`

## 2. Database + AppContainer wiring

- [x] 2.1 Add `ShoppingItemEntity::class` to `ShoppingDatabase` `entities` and
  add `abstract fun shoppingItemDao(): ShoppingItemDao`; schema version stays 1
- [x] 2.2 Add `shoppingItemRepository: ShoppingItemRepository =
  RoomShoppingItemRepository(database.shoppingItemDao())` to `AppContainer`

## 3. Items UI state + ViewModel

- [x] 3.1 Create `ItemsUiState` (`ui/items/ItemsUiState.kt`) holding
  `List<ShoppingItemEntity>` (empty default)
- [x] 3.2 Create `ItemsViewModel(repository, listId)` exposing
  `StateFlow<ItemsUiState>` from `repository.observeItems(listId)` via
  `stateIn(viewModelScope, WhileSubscribed(5_000), ...)`
- [x] 3.3 Implement `addItem(name)` trimming the name, ignoring blank input, and
  calling `repository.create(listId, trimmed)`
- [x] 3.4 Implement `renameItem(id, name)` trimming and delegating to
  `repository.rename`
- [x] 3.5 Implement `deleteItem(id)` delegating to `repository.delete`

## 4. Items screen (Compose UI)

- [x] 4.1 Create `ItemsScreen(uiState, onAddItem, onRenameItem, onDeleteItem,
  onBack)` with a Scaffold, `TopAppBar` titled "Items" with a back
  navigation icon, and a `LazyColumn` of items with `key(item.id)`
- [x] 4.2 Add a FAB (+) that opens the create-item dialog
- [x] 4.3 Add the create dialog (`AlertDialog` + text field): OK disabled/ignored
  on blank input; cancel closes without creating (duplicate `NameDialog` locally,
  mirroring `ListsScreen`'s private dialog)
- [x] 4.4 Add long-press context menu (`combinedClickable` + anchored
  `DropdownMenu`, single `menuTarget` in `rememberSaveable`) offering Rename and
  Delete
- [x] 4.5 Add the rename dialog prefilled with the current name; wire delete to
  `onDeleteItem`

## 5. Navigation wiring

- [x] 5.1 In `App.kt`, replace the `PlaceholderItemsScreen` usage in the
  `composable<Items>` block with the real `ItemsScreen`: obtain
  `ItemsViewModel(container.shoppingItemRepository, route.listId.value)` via the
  `viewModel { ... }` initializer closing over `AppContainer` (ADR-0008), collect
  with `collectAsStateWithLifecycle`, wire `onBack = navController.popBackStack()`
- [x] 5.2 Delete `ui/items/PlaceholderItemsScreen.kt`
- [x] 5.3 Verify launch -> lists -> tap a list shows that list's items -> back
  returns to lists with state preserved

## 6. ItemsViewModel unit tests (JVM)

- [x] 6.1 Create `FakeShoppingItemRepository` backed by `MutableStateFlow`,
  scoped by list, for tests
- [x] 6.2 Test that observe state lists items in repository emission order
- [x] 6.3 Test that `addItem` with a trimmed valid name emits the new item as
  last element of the target list
- [x] 6.4 Test that items of another list do not appear (list scoping)
- [x] 6.5 Test that `addItem` with a blank/whitespace name emits no change
- [x] 6.6 Test that `renameItem` updates the item's name in emitted state
- [x] 6.7 Test that `deleteItem` removes the item from emitted state

## 7. Integration tests (instrumented, in-memory Room)

- [x] 7.1 Write `ShoppingItemDaoTest`: observeByList filters by list and orders by
  creation time; insert/rename/delete; deleting a list cascades to its items
- [x] 7.2 Write `RoomShoppingItemRepositoryTest`: create returns a stored UUID
  scoped to the list; rename preserves id/createdAt; delete; survived re-open
- [x] 7.3 Write `ItemsViewModelIntegrationTest`: ViewModel over
  `RoomShoppingItemRepository` on an in-memory `ShoppingDatabase`; add/rename/
  delete propagate to uiState and the database

## 8. Verification

- [x] 8.1 Run `./gradlew test` (JVM unit tests) and
  `./gradlew connectedAndroidTest` (instrumented) with all green
- [x] 8.2 Run `./gradlew lint` and resolve any new issues
- [x] 8.3 Run `openspec validate items-screen --type change --strict` and resolve
  any issues
- [x] 8.4 Manual smoke check on the target device (Samsung Galaxy A52): create a
  list, add/rename/delete items, verify a list with items deletes cleanly
  (cascade), navigate lists <-> items with state preserved on back
