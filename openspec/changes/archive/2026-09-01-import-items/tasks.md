# Implementation Tasks — import-items

## 1. Domain parser (pure business logic, ADR-0011)

- [x] 1.1 Create `domain/ListonicImportParser.kt` in package
  `org.mateuszmidor.shoplist.domain` exposing `object ListonicImportParser` with
  `fun parse(text: String): List<String>` (pure, no Android types, no IO)
- [x] 1.2 Implement the parsing rules: split on `\n` (tolerating `\r\n`), trim
  surrounding whitespace per line, strip at most one leading bullet from
  `•`, `-`, `*` plus any following whitespace, skip lines that end up empty
  (including bare bullet lines), keep duplicates, and preserve `/` characters
  verbatim

## 2. Data layer: atomic batch append

- [x] 2.1 Add `@Insert suspend fun insertAll(items: List<ShoppingItemEntity>)`
  to `ShoppingItemDao` (single transaction → all-or-nothing)
- [x] 2.2 Add `suspend fun createAll(listId: UUID, names: List<String>):
  List<UUID>` to the `ShoppingItemRepository` interface
- [x] 2.3 Implement `createAll` in `RoomShoppingItemRepository`: generate a UUID
  per name and a sequential creation timestamp (`createdAt = base + index`) so
  the imported batch keeps its pasted order under the `created_at` sort, call
  `dao.insertAll`, and return the generated IDs

## 3. ViewModel import action

- [x] 3.1 Add `fun importItems(text: String)` to `ItemsViewModel`: parse via
  `ListonicImportParser.parse`, return without any data-layer call when the
  result is empty (blocked case), otherwise launch
  `repository.createAll(listId, parsed)` in `viewModelScope` (ADR-0008)

## 4. Items screen entry points

- [x] 4.1 Turn the FAB into an anchored menu: keep the `FloatingActionButton`
  showing "+", add a `rememberSaveable fabMenuVisible` state, and a
  `DropdownMenu` with two items — "Add item" (opens the existing create dialog)
  and "Import from Listonic" (opens the new import dialog)
- [x] 4.2 Add a private multi-line import dialog inside `ItemsScreen.kt` (per
  project decision): an `OutlinedTextField` of about 4 lines, no list selector,
  a confirm button whose `enabled` is
  `ListonicImportParser.parse(text).isNotEmpty()`, confirm invokes the import
  and closes, cancel closes without importing
- [x] 4.3 Add the `onImportText: (String) -> Unit` parameter to `ItemsScreen`
  and wire the FAB menu items to the create dialog / import dialog respectively
- [x] 4.4 In `App.kt`, pass `onImportText = viewModel::importItems` when
  rendering the items screen

## 5. Unit tests (JVM)

- [x] 5.1 Write `ListonicImportParserTest`: strips `•`, `-`, and `*` bullets;
  trims surrounding whitespace; skips blank and bare-bullet lines; keeps
  duplicates; preserves slashes; tolerates CRLF; blank/whitespace-only/`•`-only
  input yields an empty list
- [x] 5.2 Extend `FakeShoppingItemRepository` with `createAll` (append each name
  with an increasing `createdAt`, re-sort through the existing `order` helper)
- [x] 5.3 Extend `ItemsViewModelTest`: successful import appends all parsed
  items to the target list in pasted order; import of non-parseable text makes
  no data-layer call and leaves state unchanged; cross-list isolation holds
  after an import

## 6. Integration tests (instrumented, in-memory Room)

- [x] 6.1 Extend `RoomShoppingItemRepositoryTest`: `createAll` persists every
  item scoped to the target list with `bought = false` and sequential
  timestamps, emitted in pasted order after pre-existing unchecked items
- [x] 6.2 Add the atomicity case: a batch in which at least one item violates
  the list ownership constraint throws, and none of the batch items are
  persisted (list stream unchanged) — proving all-or-nothing

## 7. Verification

- [x] 7.1 Run `make build` and confirm the app compiles
- [x] 7.2 Run `make test` (JVM unit tests) with all green
- [x] 7.3 Run `make connectedTest` (instrumented tests) with all green
- [x] 7.4 Run `./gradlew lint` and resolve any new issues
- [x] 7.5 Run `openspec validate import-items --type change --strict` and
  resolve any issues
- [x] 7.6 Manual smoke check on the target device (Samsung Galaxy A52): open a
  list, tap the FAB, verify the menu shows "Add item" and "Import from
  Listonic"; paste Listonic-exported text with bullets and confirm the items
  appear in pasted order; verify the import action stays disabled for a
  blank/whitespace-only/`•`-only paste; verify cancel imports nothing