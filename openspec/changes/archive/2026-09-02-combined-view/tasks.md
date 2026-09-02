## 1. Domain layer: combined sorting logic

- [x] 1.1 Create `CombinedItemSorter` in the `domain` package: a pure function ordering combined rows by bought (unchecked first), then name, then createdAt
- [x] 1.2 Write JVM unit tests for `CombinedItemSorter` (unchecked-before-checked, name ordering, createdAt tiebreak, determinism)

## 2. Data layer: nothing

- [x] 2.1 Confirm no data-layer or schema change is needed (combined view reuses `observeItems` / `toggleBought`; Room schema stays version 1)

## 3. Navigation: Combined route

- [x] 3.1 Add a `Combined(listIds: List<ListId>)` type-safe route in `navigation/Routes.kt` per ADR-0012 (comma-joined canonical UUID argument, reusing `ListId` serializer)
- [x] 3.2 Wire the `Combined` destination into the NavHost in `ui/App.kt` with the route argument to the selected list ids

## 4. Lists screen: selection mode + row actions

- [x] 4.1 Extend `ListsUiState` with transient selection state (selection-mode flag + selected ids)
- [x] 4.2 Add long-press-selection behavior to `ListsViewModel`: entering selection mode on a list, toggling selection with taps, clearing on back
- [x] 4.3 Add per-row rename icon button on the lists screen (replaces long-press rename)
- [x] 4.4 Add per-row delete icon button on the lists screen (replaces long-press delete, keeps confirmation dialog naming the list and its item count)
- [x] 4.5 Move the export action onto the row icon cluster (replacing the long-press menu's export entry)
- [x] 4.6 Add the "Combine" top-bar action (enabled with ≥1 selected) and navigate to `Combined(listIds)` for the selected lists
- [x] 4.7 Ensure system back exits and clears selection mode
- [x] 4.8 Write JVM unit tests for `ListsViewModel` selection mode (enter/exit, toggle, combine surfaces selected ids, back clears)

## 5. Combined screen: ViewModel + UI

- [x] 5.1 Create `CombinedViewModel` composing `observeItems(listId)` flows for the selected lists into a `StateFlow<CombinedUiState>`, pairing each item with its source list name via `observeList`
- [x] 5.2 Apply `CombinedItemSorter` for the combined ordering (unchecked/checked split, name order, createdAt tiebreak) in the ViewModel state
- [x] 5.3 Add `toggleBought(itemId)` to `CombinedViewModel` delegating to `ShoppingItemRepository.toggleBought` (write-through)
- [x] 5.4 Create the combined screen composable (flat LazyColumn, source-list caption per item, unchecked top / checked bottom, name-sorted sections)
- [x] 5.5 Ensure the combined screen offers check/uncheck only (no add/rename/delete) and exits via system back
- [x] 5.6 Write JVM unit tests for `CombinedViewModel` (merge with source captions, duplicates concatenated, ordering, toggle write-through) using fake repositories
- [x] 5.7 Write instrumented integration tests for the combined feature (merged view over real lists; toggle reflects in the owning list)

## 6. Consistency and validation

- [x] 6.1 Confirm the lists-screen long-press context menu is fully removed and no dead code/labels remain
- [x] 6.2 Run the app on the target device and verify: long-press enters selection, row icons rename/delete, Combine opens merged view, toggles write through, back discards
- [x] 6.3 Run `openspec validate combined-view --type change --strict` before archive