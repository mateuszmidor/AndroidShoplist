## Why

Marking items as bought while shopping is the core daily workflow of a shopping list app, and it is the one missing piece between today's plain CRUD (Change 03) and a usable shopping tool. The `bought` field exists on the entity but nothing reads it yet — this change makes bought state visible and reorganises the display so checked items drop to the bottom instead of cluttering the active shopping list.

## What Changes

- Tapping an item's row or checkbox on the items screen toggles its `bought` flag.
- Bought items are shown with a checked visual indicator and a struck-through, dimmed label.
- Item display reorders from "by creation time" to two sections: unchecked items first, then checked items; within each section items are sorted by creation time (earliest first). Unchecking returns an item to its creation-time position in the unchecked section.
- The data layer gains a toggle-bought operation.

## Capabilities

### New Capabilities

None — the change is captured by modifying the two existing item capabilities.

### Modified Capabilities

- `items-data`: Item streams are now emitted in two-section order — unchecked items first (by creation time), then checked items (by creation time) — and a new toggle-bought operation is added alongside create/rename/delete.
- `items-screen`: The items list is displayed as two visual sections, bought rows carry a checkbox/strikethrough indicator, and tapping an item toggles its bought state; the existing "ordered by creation time" scenarios are updated to the new two-section ordering.

## Impact

- **Data layer**: `ShoppingItemDao` (add `toggleBought` query; update `observeByList` ordering to `bought ASC, created_at ASC, id ASC`), `ShoppingItemRepository` interface and `RoomShoppingItemRepository` (add `toggleBought`), and the `FakeShoppingItemRepository` test double (mirror toggle + ordering).
- **ViewModel**: `ItemsViewModel` gains `toggleItemBought(id)` following the existing write-path pattern.
- **UI**: `ItemsScreen`/`ItemRow` gain a checkbox + strikethrough visual and tap-to-toggle; `App.kt` wires the new action.
- **Tests**: DAO integration tests for toggle + two-section ordering (incl. uncheck-restores-position), ViewModel unit tests for the toggle and grouped ordering via the fake, and a ViewModel integration test covering the bought/unbought round-trip over real Room.
- **No schema migration**: the `bought` column already exists (Change 03). **No new dependencies**: `Checkbox` is part of the material3 library already in use.