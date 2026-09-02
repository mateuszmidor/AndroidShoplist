# combined-view

## Why

While shopping, the user often needs to buy from several lists at once (e.g. two
recipe lists) and currently has to switch back and forth between them. This
change lets the user merge a group of lists into a single transient view so
everything needed for one trip is visible in one place.

## What Changes

- Add a multi-select mode on the Lists screen: long-pressing a list enters
  selection mode, where tapping lists toggles them in/out of the selection.
- Move the existing long-press rename/delete context menu off the lists screen:
  rename and delete become per-row icon buttons, freeing long-press for
  selection mode. (The items screen long-press menu is unchanged.)
- Add a "Combine" action (top bar) that opens a combined view from the selected
  lists (enabled with one or more selected). System back exits selection mode
  without combining.
- Add a combined view screen that merges all items of the selected lists into a
  single list. It is transient and single-use: it supports checking/unchecking
  only, holds no state of its own, and is discarded on exit via system back.
- In the combined view, each item shows its source list as a small caption.
  Items concatenate 1:1 (a name present in several lists shows as separate
  rows). Ordering follows the standard split — unchecked at top, checked at
  bottom; within each section items sort by name, with equal names ordered by
  creation time.
- Toggling an item in the combined view writes through to the owning list, so
  the original lists stay authoritative and the combined view reflects the same
  underlying rows reactively.
- No Room schema change; the combined view is a derived, reactive composition
  of existing item flows, not a new stored entity.

## Capabilities

### New Capabilities

- `combined-view`: Merging a selected group of shopping lists into a single
  transient view — selecting lists on the lists screen, the combined view
  screen that merges and orders their items, and the check/uncheck write-through
  to the owning lists.

### Modified Capabilities

- `lists-screen`: The long-press interaction changes so that it enters
  multi-select mode instead of opening the rename/delete context menu;
  rename/delete move to per-row icon buttons; a "Combine" action starts the
  combined view from the selected lists.

## Impact

- **New code**: combined view screen + ViewModel; the merge/sort logic that
  combines per-list item flows with a source-list caption; the lists-screen
  selection-mode state and "Combine" action.
- **Modified code**: `ui/lists/ListsViewModel.kt` + `ListsScreen.kt` (selection
  mode, row icons for rename/delete, Combine action), navigation routes
  (`navigation/Routes.kt` — new combined route carrying the selected list ids);
  test doubles for the lists/items repositories.
- **Removed code**: the long-press context menu on the lists screen.
- **New dependencies**: none. **Schema change**: none (Room version stays 1).
- **Tests**: JVM unit tests for the merge/sort/ordering logic and the combined
  view ViewModel; JVM tests for the lists-screen selection mode; instrumented
  integration tests for the combined view feature and the write-through toggle.
  No UI automation tests (deferred).
- **Not in scope**: saving/reusing a combined group (transient by design);
  deduplicating or grouping identical item names (concatenated and captioned
  instead); add/rename/delete inside the combined view; combining a single list
  (allowed but identical to opening it); changing the items screen long-press.
