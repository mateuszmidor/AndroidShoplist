# Implementation Tasks

Decomposed changes for building the shoplist app, based on ARCHITECTURE.md.
Each change is self-contained and delivers something testable.

---

## Change 01a: Project Scaffolding

**Goal**: Proves the build toolchain, dependencies, and runtime environment work.

- [X] Create Android project structure (Gradle Kotlin DSL)
- [X] Configure dependencies: Compose, Room, Navigation Compose, Coroutines
- [X] Set up `AppContainer` for manual DI (empty shell)
- [X] Create empty Compose `App()` that launches with a placeholder
- [X] Create `Application` class
- [X] Configure project (minSdk, targetSdk, compileSdk, etc.)
- [X] Verify app compiles and launches on target device

---

## Change 01b: Data Foundation — Lists

**Goal**: Proves Room persistence works end-to-end.

- [X] Create `ShoppingListEntity` (`id: UUID`, `name: String`, `createdAt: Long`)
- [X] Create `ShoppingListDao` with `Flow` queries and CRUD operations
- [X] Create `ShoppingListRepository` interface
- [X] Create `RoomShoppingListRepository` implementation
- [X] Create `ShoppingDatabase` (Room `@Database`)
- [X] Wire `AppContainer` to provide repository
- [X] Write in-memory Room integration tests for DAO
- [X] Write integration tests for repository

---

## Change 02: Lists Screen (Full Stack)

**Goal**: First real feature — proves UI -> ViewModel -> Repository -> Room pipeline.

- [X] Create `ListsViewModel` with `StateFlow<ListsUiState>`
- [X] Create Lists screen (LazyColumn) showing all lists
- [X] Implement FAB (+) to create a new list
- [X] Implement long-press context menu: rename
- [X] Implement long-press context menu: delete
- [X] Set up Navigation Compose with lists screen as start destination
- [X] Add placeholder navigation to items screen
- [X] Write unit tests for ViewModel
- [X] Write integration tests for lists feature

---

## Change 03: Items Screen (Full Stack)

**Goal**: Second screen, mirroring the lists pattern. Completes the two-screen navigation model.

- [X] Create `ShoppingItemEntity` (`id: UUID`, `listId: UUID`, `name`, `bought`, `createdAt`)
- [X] Create `ShoppingItemDao` with CRUD + Flow queries by list
- [X] Create `ShoppingItemRepository` interface + Room implementation
- [X] Wire item repository into `AppContainer`
- [X] Create `ItemsViewModel` with per-list state
- [X] Create Items screen (LazyColumn) showing items in a list
- [X] Implement FAB (+) to add item
- [X] Implement long-press context menu: rename/delete
- [X] Wire navigation: lists -> items (with list UUID argument)
- [X] Implement back navigation from items -> lists
- [X] Write unit tests for ViewModel
- [X] Write integration tests for item data layer

---

## Change 04: Bought Checkmarks + Ordering

**Goal**: Core shopping functionality — marking items as bought and visual split.

- [ ] Implement item tap to toggle `bought` field
- [ ] Add checkmark/bought visual indicator
- [ ] Implement ordering: unchecked items (by createdAt) at top, checked at bottom
- [ ] Write tests for ordering logic
- [ ] Verify bought/unbought transitions work correctly

---

## Change 05: Polish + Auto-Backup

**Goal**: Nice-to-haves per feature prioritization.

- [ ] Add per-list summary on lists screen: total item count + bought count
- [ ] Configure Android auto-backup manifest
- [ ] Move all UI strings to `strings.xml` resources
- [ ] Final pass on UI consistency between lists and items screens

---

## Change 06: Import list from Listonic app

**Goal**: Let the user create a new list by pasting plain-text items exported from
Listonic. Each pasted line becomes one item (slashes preserved, leading bullet stripped,
blanks skipped, duplicates kept); the list and its items are created atomically (all-or-nothing).

Example import text (bullets are optional):
```
• mleko
• jajka
• chleb ciemny/bułki
```

- [ ] Add parser that turns pasted lines into item names (strip bullets, skip blanks, keep duplicates)
- [ ] Add atomic data-layer operation to create a list together with all its items
- [ ] Add FAB menu on Lists screen: "New list" and "Import from Listonic"
- [ ] Add import dialog with list name field + multi-line paste field
- [ ] Block import when name is blank or pasted text yields no items
- [ ] Wire import action from Lists screen to ViewModel
- [ ] Move new UI strings to strings.xml resources
- [ ] Write unit tests for the parser
- [ ] Write unit tests for the ViewModel import action (success + blocked cases)
- [ ] Write integration tests for the atomic import in the data layer

---

## Dependency Graph

```
01a → 01b → 02 → 03 → 04 → 05
```

Each change builds on the previous one. No change can be started until its predecessor is complete.
