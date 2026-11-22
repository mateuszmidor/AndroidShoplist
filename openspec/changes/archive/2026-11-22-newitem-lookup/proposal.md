## Why

When adding a shopping-list item, the name may already exist on the same list but be marked as bought and sorted off-screen. Not noticing it leads to a duplicate entry. The "Add item" flow should reveal such items while the name is being typed, so the user can un-mark the existing item instead of creating a duplicate.

## What Changes

- Replace the create-item `NameDialog` on the items screen with a dedicated full-screen `AddItemDialog` containing a focused name field and a live-filtered list of matching, bought items from the current list.
- Filter the list by the typed name (case-insensitive substring, trimmed) and show **all bought items** when the field is empty.
- Tapping a match un-marks that item immediately and keeps the dialog open.
- Disable the "Add" action when the trimmed, case-insensitive name exactly matches any item in the current list (bought or not); near-matches such as "eggplant" for "egg" are displayed but do not block creating a new item.
- Preserve the existing behavior of cancelling, blank/whitespace-only names, and the auto-focused name field from Change 09.

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- `items-screen`: The create-item dialog gains a live lookup of bought items in the current list, with exact-match duplicate protection and in-dialog un-marking.

## Impact

- Code: `app/src/main/java/org/mateuszmidor/shoplist/ui/items/ItemsScreen.kt` (new `AddItemDialog` composable, query state, match filtering), `app/src/main/java/org/mateuszmidor/shoplist/ui/common/NameDialog.kt` remains unchanged.
- Reuses `ItemsUiState.items` and the existing `onToggleBought` callback; no ViewModel, repository, DAO, Room schema, navigation, or dependency changes.
- Verification: manual on the Samsung Galaxy A52, plus the existing JVM and instrumented test suites.
