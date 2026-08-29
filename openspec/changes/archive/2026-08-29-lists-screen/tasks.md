# Implementation Tasks — lists-screen

## 1. Dependencies and version alignment

- [x] 1.1 Add `navigation-compose` 2.10.0 to `gradle/libs.versions.toml`
- [x] 1.2 Bump `lifecycle-runtime-ktx` from 2.6.1 to 2.11.0 in the version catalog
- [x] 1.3 Add `lifecycle-viewmodel-compose` and `lifecycle-runtime-compose` 2.11.0 to the version catalog
- [x] 1.4 Add `kotlinx-serialization-json` 1.11.0 to the version catalog
- [x] 1.5 Add the Kotlin serialization Gradle plugin (`id` serialization, version ref `kotlin`) to `app/build.gradle.kts` plugins
- [x] 1.6 Add `kotlinx-coroutines-test` to `testImplementation` (in addition to existing `androidTestImplementation`) in `app/build.gradle.kts`
- [x] 1.7 Apply all new dependencies in `app/build.gradle.kts` and verify the project still configures and compiles (no UI code yet)

## 2. ListsViewModel and UI state

- [x] 2.1 Create `ListsUiState` holding `List<ShoppingListEntity>` (empty default) in the lists package
- [x] 2.2 Create `ListsViewModel(ShoppingListRepository)` exposing `StateFlow<ListsUiState>` derived from `repo.observeLists()` via `stateIn(viewModelScope, WhileSubscribed(5_000), ...)`
- [x] 2.3 Implement `createList(name)` that trims the name, ignores blank input, and delegates to `repo.create`
- [x] 2.4 Implement `renameList(id, name)` that trims and delegates to `repo.rename`
- [x] 2.5 Implement `deleteList(id)` that delegates to `repo.delete`

## 3. Lists screen (Compose UI)

- [x] 3.1 Create `ListsScreen(uiState, onNavigateToItems, ...)` rendering a `LazyColumn` of lists with `key(list.id)`
- [x] 3.2 Add a Scaffold with a FAB (+) that opens the create-list dialog
- [x] 3.3 Add the create dialog (`AlertDialog` + text field): OK disabled/ignored on blank input; cancel closes without creating
- [x] 3.4 Add long-press context menu (`combinedClickable` + anchored `DropdownMenu`, single `menuTarget` in `rememberSaveable`) offering Rename and Delete
- [x] 3.5 Add the rename dialog prefilled with the current name (OK on trimmed non-empty, cancel leaves unchanged)
- [x] 3.6 Wire delete from the context menu to `viewModel.deleteList`

## 4. Navigation and placeholder items route

- [x] 4.1 Define type-safe `@Serializable` routes (`Lists`, `Items(listId)`) in the navigation layer
- [x] 4.2 Add the reusable `KSerializer<UUID>` (canonical string form) applied to the `listId` route argument
- [x] 4.3 Replace the `App` placeholder with a `NavHost` whose start destination is `Lists`
- [x] 4.4 Obtain `ListsViewModel` on the lists destination via the `viewModel` CreationExtras initializer closing over `AppContainer` (ADR-0008), collect state with `collectAsStateWithLifecycle`
- [x] 4.5 Add a placeholder items destination rendering the passed `listId` (replaced by the real items screen in change 03)
- [x] 4.6 Verify launch shows the lists screen, tapping a row navigates to the placeholder items screen, and back returns to lists

## 5. ViewModel unit tests (JVM)

- [x] 5.1 Create a fake `ShoppingListRepository` backed by `MutableStateFlow` for tests
- [x] 5.2 Test that observe state lists entities in repository emission order
- [x] 5.3 Test that `createList` with a trimmed valid name emits the new list as last element
- [x] 5.4 Test that `createList` with a blank/whitespace name emits no change
- [x] 5.5 Test that `renameList` updates the list's name in emitted state
- [x] 5.6 Test that `deleteList` removes the list from emitted state

## 6. Integration tests (instrumented, in-memory Room)

- [x] 6.1 Add androidTest wiring `ListsViewModel` over `RoomShoppingListRepository` on an in-memory `ShoppingDatabase`
- [x] 6.2 Test that the ViewModel surfaces a list created through its own `createList`
- [x] 6.3 Test that rename and delete through the ViewModel propagate to emitted state and the Room database

## 7. Verification

- [x] 7.1 Run the full test suites: `./gradlew test` (JVM unit) and `./gradlew connectedAndroidTest` (instrumented) with all green
- [x] 7.2 Run `openspec validate lists-screen --type change --strict` and resolve any issues
- [x] 7.3 Manual smoke check on the target device (Samsung Galaxy A52): create, rename, delete, navigate lists <-> items with state preserved on back