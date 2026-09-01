# export-items

## Why

Users currently have no way to get their list contents out of the app. The app
already supports importing pasted Listonic-format text (Change 06/items-import),
but the reverse direction is missing. Exporting a list's items to the clipboard
as Listonic-compatible text lets users back up a list, share it, or move it into
another tool, completing the import/export symmetry.

## What Changes

- Add an "Export items" action to the long-press context menu on a list row in
  the Lists screen.
- Export all items of the selected list (bought and unbought) as one
  bullet-prefixed line per item, matching the format that the existing import
  parser accepts (so exported text round-trips back through import).
- Copy the formatted text to the device clipboard.
- Show a Toast confirmation after copying.

## Capabilities

### New Capabilities
- `items-export`: Export all items of a shopping list to the clipboard as
  bullet-prefixed text, one item per line, format-compatible with the app's
  existing Listonic import.

### Modified Capabilities
<!-- No existing capability's behaviour changes -- export is additive. -->

## Impact

- `ShoppingItemDao`/`ShoppingItemRepository`: adds a one-shot (non-reactive)
  query to fetch all items of a list.
- `ListsViewModel`: gains an export action; constructor widens to also depend
  on the shopping-item repository and the clipboard.
- `ListsScreen`: adds "Export items" to the existing long-press context menu.
- New format helper mirroring the import parser's conventions.
- Feature is additive; no breaking changes.
