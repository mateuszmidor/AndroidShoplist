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

- [X] Implement item tap to toggle `bought` field
- [X] Add checkmark/bought visual indicator
- [X] Implement ordering: unchecked items (by createdAt) at top, checked at bottom
- [X] Write tests for ordering logic
- [X] Verify bought/unbought transitions work correctly

---

## Change 05: Polish + Auto-Backup

**Goal**: Nice-to-haves per feature prioritization.

- [X] Ask user to confirm when deleting a list, deleting an item needs no confirmation
- [X] Add per-list summary on lists screen: total item count + bought count
- [X] Configure Android auto-backup manifest
- [X] Final pass on UI consistency between lists and items screens

---

## Change 06: Import items from Listonic app

**Goal**: Let the user append items to an existing list by pasting plain-text items
exported from Listonic. From the Items screen the user opens an import dialog and pastes
text; each pasted line becomes one item (slashes preserved, leading bullet stripped,
blanks skipped, duplicates kept). All parsed items are added atomically (all-or-nothing).

Example import text (bullets are optional):
```
• mleko
• jajka
• chleb ciemny/bułki
```

- [x] Add parser that turns pasted lines into item names (strip bullets, skip blanks, keep duplicates)
- [x] Add atomic data-layer operation to append multiple items to an existing list (single transaction)
- [x] Add FAB menu on Items screen: "Add item" and "Import from Listonic"
- [x] Add import dialog with multi-line paste field (no list name — target is the open list)
- [x] Block import when pasted text yields no items
- [x] Wire import action from Items screen to ItemsViewModel
- [x] Write unit tests for the parser
- [x] Write unit tests for the ViewModel import action (success + blocked cases)
- [x] Write integration tests for the atomic import in the data layer

---

## Change 07: Export items from Lists screen

**Goal**: Let the user export all items of a list to the phone clipboard as plain text in Listonic-compatible format. From the Lists screen the user long-presses a list and taps "Export items"; all items (bought and unbought) are formatted as bullet-prefixed lines and copied to the clipboard. A Toast confirms the action.

Example exported text:
```
• mleko
• jajka
• chleb ciemny/bułki
```

- [x] Add snapshot query to fetch all items for a list (non-reactive)
- [x] Add export action to ListsViewModel: format items as bullet-prefixed lines, copy to clipboard, show Toast
- [x] Add "Export items" option to long-press context menu on Lists screen
- [x] Write unit tests for the export formatting logic
- [x] Write unit tests for the ViewModel export action
- [x] Write integration tests for the snapshot query

---
