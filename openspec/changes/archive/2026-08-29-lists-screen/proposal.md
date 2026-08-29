# lists-screen

## Why

The app currently shows a placeholder. This change delivers the first real
feature - the lists screen - proving the full UI -> ViewModel -> Repository ->
Room pipeline end-to-end on the target device, and establishes the navigation
and per-screen patterns every later screen will mirror.

## What Changes

- Create `ListsViewModel` exposing `StateFlow<ListsUiState>` backed by the
  existing `ShoppingListRepository` reactive stream
- Create the Lists screen (LazyColumn) rendering all shopping lists
- Implement a FAB (+) to create a new list via name dialog
- Implement a long-press context menu on a list row with rename (prefilled
  dialog) and delete actions
- Introduce Jetpack Navigation Compose with type-safe (`@Serializable`) routes,
  with the lists screen as the start destination
- Add a placeholder items route (`items/{listId}`) to prove navigation wiring;
  the real items screen arrives in a later change
- New schemas (Room/Compose) are NOT introduced beyond what persistence already
  provides - no data model changes

## Capabilities

### New Capabilities

- `lists-screen`: The UI feature that displays all shopping lists, creates new
  lists, renames and deletes existing lists via the interaction pattern, and
  navigates to a per-list items screen

### Modified Capabilities

- none (list persistence behaviour is unchanged)

## Impact

- **New dependencies**: `androidx.navigation:navigation-compose` (2.10.0),
  `androidx.lifecycle:lifecycle-viewmodel-compose` and
  `lifecycle-runtime-compose` (2.11.0), `kotlinx-serialization-json` (1.11.0)
  plus the Kotlin serialization plugin (version = Kotlin); `kotlinx-coroutines-test`
  also added to `testImplementation` for JVM ViewModel tests
- **Version bump**: `lifecycle-runtime-ktx` 2.6.1 -> 2.11.0 to keep a single
  coherent lifecycle line
- **New code**: `ui/lists/ListsViewModel.kt`, `ui/lists/ListsScreen.kt`,
  navigation graph wiring in `ui/App.kt`, placeholder items screen
- **Modified code**: `App.kt` (replace placeholder with NavHost),
  `AppContainer` unchanged (repository already exposed), `gradle/libs.versions.toml`
  and `app/build.gradle.kts` (dependencies and plugins)
- **Tests**: JVM unit tests for `ListsViewModel` (fake repository + coroutines
  test dispatcher); instrumented integration tests wiring ViewModel to an
  in-memory Room database
- **No** data schema migration (Room entities untouched)