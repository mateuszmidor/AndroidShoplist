# lists-screen — Design

## Context

After changes 01a/01b the app is a placeholder: `App(container)` renders static
text, while the data layer is fully functional — Room over a `shopping_lists`
table, exposed as a reactive `Flow<List<ShoppingListEntity>>` via
`ShoppingListRepository` (observe/create/rename/delete), constructed in
`AppContainer` (ADR-0001). No navigation, ViewModel, or screen code exists yet.

This change builds the first feature on that foundation, proving the
UI → ViewModel → Repository → Room pipeline end-to-end and establishing the
architecture every later screen mirrors (ARCHITECTURE.md, Change 02 of
TASKS.md).

Constraints in force (ADR-0004): current **stable** components only — no
pre-release libraries. ADR-0001: manual DI via `AppContainer`, no DI framework.
Room 3 via KSP (ADR-0006) stays untouched — no data model changes here.

## Goals / Non-Goals

**Goals:**
- Rendering of all shopping lists in a `LazyColumn`, reactive to storage
- Create (FAB → dialog), rename (long-press menu → prefilled dialog), delete
  (long-press menu) — the ARCHITECTURE interaction pattern
- Jetpack Navigation Compose with **type-safe** (`@Serializable`) routes; lists
  as start destination; an `items/{listId}` placeholder route to prove
  navigation wiring ahead of the items screen change
- ViewModel testable in isolation (JVM unit tests) and verifiable against
  in-memory Room (instrumented integration tests)
- A design that the items screen can mirror with minimal divergence

**Non-Goals:**
- The real items screen (change 03) — only a navigable placeholder route
- `strings.xml` resource extraction — deferred to change 05 (UI text stays
  hardcoded this change)
- UI-model mapping layer — entities pass straight into UI state (decision B)
- UI automation tests — deferred per ARCHITECTURE.md
- Any Room schema/entity change, manual DI framework, or experimental libraries

## Decisions

### 1. Navigation: Navigation Compose with type-safe `@Serializable` routes

Route model is a small set of serializable route types in the navigation layer:

```
App ── NavHost (startDestination = Lists)
  ├── Lists               → ListsScreen
  └── Items(listId)       → PlaceholderItemsScreen   (replaced in change 03)
```

`Lists` is a `@Serializable object`; `Items` is `@Serializable data class`
carrying `listId`. Because `kotlinx.serialization` ships no built-in serializer
for `java.util.UUID` (only for the newer `kotlin.uuid.Uuid`), the route declares
a tiny custom `KSerializer<UUID>` (UUID ↔ canonical string) on the `listId`
property. This keeps `java.util.UUID` as the single domain type across
entity/DAO/navigation.

_Alternatives considered:_ stringly-typed routes (`route = "items/{listId}"`,
`navArgument(StringType)`) — rejected: ARCHITECTURE mandates type-safe routes,
and UUID argument handling with string routes is exactly the ceremony
type-safe navigation removes; encoding `listId` as a raw `String` in the route —
rejected: weakens compile-time safety and pushes parse errors into call sites.

### 2. ViewModel wiring via `CreationExtras` initializer (manual DI)

```
AppContainer ── repo ──┐
                       ▼
ListsViewModel = viewModel { ListsViewModel(container.shoppingListRepository) }
```

`ListsViewModel(repo)` takes only the repository. The composable obtains it with
the `viewModel(initializer = ...)` overload from `lifecycle-viewmodel-compose`,
closing over `AppContainer`. No factory class, no DI framework; coherent with
ADR-0001 (the container remains the single wiring point).

_Alternatives:_ dedicated `ViewModelProvider.Factory` — more ceremony, no
benefit at this scale; instantiating the ViewModel directly via `remember` —
loses lifecycle scoping and makes process-death handling non-idiomatic.

### 3. Reactive single-source-of-truth state

```
ListsUiState(lists: List<ShoppingListEntity> = emptyList())

init {
    repo.observeLists()
        .map { ListsUiState(lists = it) }
        .stateIn(viewModelScope, WhileSubscribed(5_000), ListsUiState())
}
```

Write operations delegate to the repository inside `viewModelScope.launch {}` and
do **not** mutate state directly — the Room `Flow` re-emits and drives the UI.
This yields one source of truth (storage), no drift between local state and the
database, and trivially testable reactions.

_Alternatives:_ hand-managed `MutableStateFlow.update {}` after each op —
rejected: duplicates state the database already owns and invites divergence.

### 4. State collection with `collectAsStateWithLifecycle`

UI collects `uiState` via `collectAsStateWithLifecycle()` (adds
`lifecycle-runtime-compose`). Lifecycle-aware collection stops upstream work when
the UI is not visible; pairs with `WhileSubscribed`.

_Alternatives:_ bare Compose `collectAsState()` — works, but ignores lifecycle
and would keep a `stateIn`-less Flow alive unnecessarily under the
`WhileSubscribed` strategy.

### 5. Version alignment: single lifecycle line at 2.11.0

`lifecycle-runtime-ktx` 2.6.1 → 2.11.0 together with new
`lifecycle-runtime-compose` 2.11.0 and `lifecycle-viewmodel-compose` 2.11.0.
`navigation-compose` 2.10.0 (latest stable; pulls a newer lifecycle anyway) and
`kotlinx-serialization-json` 1.11.0 with the serialization Gradle plugin pinned
to the Kotlin version (2.2.10). All are current stable → satisfies ADR-0004.

### 6. UI interaction pattern

- Row: `combinedClickable(onClick = { navigate to items }, onLongClick = {
  menuTarget = list.id })` inside `LazyColumn` with `key(list.id)`
- **One anchored `DropdownMenu`** rendered for whichever row is the current
  `menuTarget` (held in `rememberSaveable`), offering **Rename** and **Delete**
- **Create** and **Rename**: `AlertDialog` with a text field and OK/Cancel;
  rename prefilled with the current name; OK disabled while trimmed input is
  empty; create also ignores empty/whitespace names in the ViewModel
- Local dialog/menu visibility is screen-local composable state; repository
  calls stay in the ViewModel (`App` never touches Room directly)

### 7. UI state holder skips entity→UiModel mapping

`ListsUiState` exposes `ShoppingListEntity` directly. The entity is an immutable
3-field value type with a stable `id`; a dedicated UI model adds a mapping layer
with zero behavioural gain and violates "no speculative abstraction".

### 8. Strings hardcoded this change

UI text is inline literals now; change 05 extracts everything to `strings.xml`
(`stringsResource`/resource refs). Accepted retrofit cost is deliberate to keep
this change focused.

### 9. Test strategy

- **JVM unit tests** (`src/test`): `ListsViewModel` against a fake
  `ShoppingListRepository` (in-memory MutableStateFlow implementation);
  `runTest` + `Dispatchers.setMain(turboDispatcher)`; assertions on state
  transitions (create appends in order, rename updates, delete removes, trimmed
  names, empty create ignored). Requires `kotlinx-coroutines-test` on
  `testImplementation` (currently androidTest-only).
- **Instrumented integration tests** (`src/androidTest`): ViewModel wired to a
  real repository over an **in-memory Room** database — proves
  ViewModel → Repository → Room without UI automation (UI tests deferred).

## Risks / Trade-offs

- [Navigation 2.10.0 interacts with Compose BOM 2026.02.01 and lifecycle 2.11]
  → Constrain Compose versions via the BOM (which takes precedence for compose
  artifacts); verify the app builds early in the change before writing UI.
- [`@Serializable` nav routes pull in the serialization plugin + runtime] →
  New plugin/deps are well-scoped to the navigation layer; UUID arg tamed by the
  custom serializer (verified pattern, not experimental).
- [Deferred `strings.xml` means a later retrofit in change 05]
  → Accepted, tracked in TASKS.md change 05; keeps this change's surface small.
- [Long-press `DropdownMenu` inside `LazyColumn` can misfire on scroll/item
  reuse] → Single `menuTarget` slot + `key(list.id)`; menu dismissed on
  navigation/selection.
- [Entity-in-UI couples UI to storage shape] → Accepted for a 3-field value
  type; revisit with an ADR if the entity grows opinionated fields.

## Migration Plan

Greenfield feature; no existing screen to migrate and no data schema change.
Rollback is reverting the change (data lives in Room untouched). "Migration" is
limited to the dependency adds/ bumps in the version catalog, applied via the
existing build files.

## Open Questions

- None blocking. Verify empirically at the start of implementation:
  - `navigation-compose` 2.10.0 ↔ compose BOM 2026.02.01 ↔ lifecycle 2.11.0
    resolve and build together on the target device.
  - `combinedClickable` / material3 `DropdownMenu` / `AlertDialog` APIs used are
    stable in this BOM (no experimental `@OptIn` needed beyond what the docs
    expect).
  - No in-force ADR is revisited by this design; no supersession anticipated.