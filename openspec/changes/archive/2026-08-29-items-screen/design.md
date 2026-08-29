# items-screen — Design

## Context

After change 02 the app has: a functional lists data layer (Room over a
`shopping_lists` table exposed as a reactive `Flow<List<ShoppingListEntity>>`
via `ShoppingListRepository`), a `ListsViewModel`/`ListsScreen`, and a
two-screen navigation model wired with type-safe routes (ADR-0007). The `Items`
route already exists and renders `PlaceholderItemsScreen`, which only prints the
passed list UUID. There is no item entity, item data layer, or real items screen
yet.

This change builds the items screen - the second real feature - mirroring the
lists pattern (ARCHITECTURE.md "consistency of approaches": lists and items
screens are mirror structures), and completes the two-screen navigation model.
It also introduces the `shopping_items` table with a cascade foreign key so that
deleting a list (change 02) correctly removes its items.

Constraints in force: ADR-0001 manual DI via `AppContainer`; ADR-0002 single
`:app` module; ADR-0004 current stable components only; ADR-0006 Room 3 via KSP;
ADR-0007 type-safe nav routes; ADR-0008 ViewModels via `viewModel(CreationExtras)`
initializer. No existing ADR is contradicted or superseded by this design.

## Goals / Non-Goals

**Goals:**
- An item data layer (entity / DAO / repository / Room impl) mirroring the lists
  layer, scoped per shopping list.
- A list-scoped `ItemsViewModel` exposing `StateFlow<ItemsUiState>`, created for
  the `Items(listId)` route and sourced via ADR-0008.
- A real `ItemsScreen` (LazyColumn) with FAB (+) add and long-press
  rename/delete - the ARCHITECTURE interaction pattern - replacing the
  placeholder.
- Cascade deletion: removing a shopping list removes its items.
- The `bought` boolean baked into the schema now (no UI) so change 04 needs no
  Room migration between 03 and 04.
- JVM `ItemsViewModel` unit tests and instrumented integration tests for DAO,
  repository, and ViewModel over in-memory Room.

**Non-Goals:**
- The bought toggle and bought/unbought two-section ordering UI (change 04) -
  the `bought` column exists in the schema but is not surfaced in this change's
  UI, and ordering stays by creation time for now.
- Per-list summary counts on the lists screen (change 05).
- `strings.xml` resource extraction (change 05) - UI text stays hardcoded,
  matching the lists screen.
- UI automation tests - deferred per ARCHITECTURE.md.
- A shared/hosted `NameDialog` - the dialog stays private to each screen
  (project decision), mirroring the lists screen's private dialog.
- Manual reordering, quantities/units per item - out of scope (ARCHITECTURE).

## Decisions

### 1. Item data model mirrors lists, scoped by list, with a cascade FK

```
ShoppingItemEntity(tableName = "shopping_items")
  id         : UUID            @PrimaryKey
  listId     : UUID            @ColumnInfo(index = true)  FK -> shopping_lists.id
  name       : String
  bought     : Boolean = false @ColumnInfo("bought")
  createdAt  : Long            @ColumnInfo("created_at")

ShoppingItemDao
  observeByList(listId) : Flow<List<ShoppingItemEntity>>   WHERE list_id = :listId
                                                            ORDER BY created_at, id
  insert, renameById(id, name), deleteById(id)             (by globally-unique item UUID)
```

`ShoppingItemEntity` carries `@ForeignKey(entity = ShoppingListEntity::class,
parentColumns=["id"], childColumns=["listId"], onDelete = CASCADE)`. Deleting a
list then removes its items in the same transaction - the relational model
prevents orphaned rows. `bought` is defined now (default false) purely so the
schema is stable before change 04 toggles it; no toggle UI ships in this change.

The DAO mirrors the lists DAO shape: observation is scoped by `listId`; rename
and delete operate by the globally-unique item `id` (as `ShoppingListDao`
renameById/deleteById do for lists). Ordering is `created_at, id` for now;
change 04 adds the bought split.

_Alternatives considered:_ flat item table with no FK - rejected: leaves orphaned
rows when a list is deleted (data leak), and nothing in change 02 cleans them up;
deferring `bought` to change 04 - rejected: forces a schema version 1→2 migration
between two consecutive changes when the column could simply exist from the
start.

### 2. Add item capacity to the database and AppContainer

`ShoppingDatabase` gains `ShoppingItemEntity` in `entities` and
`abstract fun shoppingItemDao(): ShoppingItemDao`. Schema version stays **1**
(a brand-new table needs no migration). `AppContainer` adds
`shoppingItemRepository: ShoppingItemRepository =
RoomShoppingItemRepository(database.shoppingItemDao())`, mirroring the lists
repository wiring (ADR-0001).

### 3. ItemsViewModel is list-scoped and reads via Flow, writes via repository

```
ItemsViewModel(repository: ShoppingItemRepository, listId: UUID) : ViewModel()

uiState =
  repository.observeItems(listId)
    .map { ItemsUiState(items = it) }
    .stateIn(viewModelScope, WhileSubscribed(5_000), ItemsUiState())

addItem(name)     // trim; ignore blank; repository.create(listId, trimmed)
renameItem(id, s) // trim; ignore blank; repository.rename(id, trimmed)
deleteItem(id)    // repository.delete(id)
```

The `listId` is a constructor argument supplied by the navigation destination, so
the ViewModel is scoped to one list and mirrors `ListsViewModel` writes-via-
repository / reads-via-Flow (ADR-0008). `ItemsUiState(items =
List<ShoppingItemEntity> = emptyList())` exposes entities directly - same
"no UI-model mapping" decision as lists (the 5-field value type has a stable id
and no opinionated behaviour).

_Alternatives:_ reading `listId` once and passing it to every repository call -
same outcome; constructor injection keeps it explicit and matches ADR-0008's
thin initializer.

### 4. ItemsScreen mirrors ListsScreen; NameDialog stays private per screen

`ItemsScreen(uiState, onAddItem, onRenameItem, onDeleteItem, onBack)`:
- Scaffold + `TopAppBar` titled **"Items"** with a back navigation icon;
- FAB (+) opening a create-item dialog;
- `LazyColumn` with `key(item.id)`; each row uses `combinedClickable` (long-press
  opens the context menu);
- a single anchored `DropdownMenu` (one `menuTarget` in `rememberSaveable`)
  offering **Rename** / **Delete**, exactly as the lists screen;
- create/rename `AlertDialog` with a text field - **duplicated privately inside
  this file** (per project decision), matching `ListsScreen`'s private dialog
  rather than hoisting a shared component.

**"Items"** as a static title is a deliberate scope cut (per project decision):
it mirrors the placeholder, needs no extra query, and showing the parent list's
name is deferred to change 05 polish.

### 5. Navigation replaces the placeholder with the real items screen

The existing `Items(listId)` route, `ListId` wrapper, `ListIdSerializer`, and
`ListIdNavType` (ADR-0007) are reused unchanged. In `App.kt` the
`composable<Items>` block:
- reads the route via `backStackEntry.toRoute<Items>()`;
- obtains `ItemsViewModel(container.shoppingItemRepository, route.listId.value)`
  through the `viewModel { ... }` initializer closing over `AppContainer`
  (ADR-0008);
- collects `uiState` with `collectAsStateWithLifecycle` and renders
  `ItemsScreen`, wiring `onBack = navController.popBackStack()`.

`PlaceholderItemsScreen.kt` is deleted. `ListsScreen.onOpenList` already
navigates to `Items(listId)` - unchanged.

### 6. Test strategy (mirrors lists)

- **JVM unit test** (`src/test`): `FakeShoppingItemRepository` (in-memory
  `MutableStateFlow` keyed/scoped by list) + `ItemsViewModelTest` using
  `runTest` / `Dispatchers.setMain`. Cases: state lists items in repository
  emission order; `addItem` trims and appends to the target list; blank name
  ignored; `renameItem` updates; `deleteItem` removes; **items of another list
  do not appear** (list-scoping regression guard).
- **Instrumented integration** (`src/androidTest`, in-memory `ShoppingDatabase`):
  - `ShoppingItemDaoTest`: observeByList filters by list and orders by creation
    time; insert/rename/delete; **cascade** - deleting a list removes its items.
  - `RoomShoppingItemRepositoryTest`: create returns a stored UUID scoped to the
    list; rename preserves id/createdAt; delete; re-open survival (mirrors the
    lists repository test).
  - `ItemsViewModelIntegrationTest`: ViewModel over the real Room repo - create /
    rename / delete surface in `uiState` and the database.

## Risks / Trade-offs

- [Adding a cascade FK across tables introduces a new Room relationship]
  → Well-understood, standard Room feature (ADR-0006); exercised directly by the
  DAO cascade test; schema is still version 1 (no migration risk).
- [Duplicated `NameDialog` between screens drifts out of sync]
  → Accepted per project decision; both dialogs are tiny and stable; change 05's
  UI-consistency pass can revisit hoisting if it becomes worthwhile.
- [`bought` column present but unused in this change]
  → Intentional: avoids a 03→04 migration; lint treats the unused column as data,
    not code, so no warning; explicitly tracked as the change-04 seam.
- [Entity-in-UI couples items screen to storage shape]
  → Same accepted trade-off as lists for a 5-field value type; revisit via ADR if
    the entity grows opinionated fields.
- [Static "Items" title omits the list name]
  → Deferred to change 05 (per project decision); no functional impact.

## Migration Plan

Greenfield feature: introduces a new `shopping_items` table (no existing item
data to migrate, schema version stays 1). No deployment is involved (local app).
Rollback is reverting the change; existing list data in Room is untouched and
retained. The only "migration"-adjacent step is the Room schema export
regenerating to include the new entity, applied through the existing KSP/Room
toolchain (ADR-0006).

## Open Questions

- None blocking. Verify empirically at implementation start:
  - Room 3.0.2 `@ForeignKey` + `@ColumnInfo(index=true)` + `onDelete = CASCADE`
    across the two entities compiles and generates correctly under KSP, and the
    cascade fires at the database level on list delete.
  - The `Items(listId)` route decodes and the `ItemsViewModel` initializer
    receives the expected UUID on the target device.
  - No in-force ADR is revisited; no supersession anticipated.
