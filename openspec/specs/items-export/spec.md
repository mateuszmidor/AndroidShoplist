# items-export Spec

## Purpose

Export items from shopping lists as bullet-prefixed text and copy to the clipboard.

## Requirements

### Requirement: Items are exported as bullet-prefixed text

The export formatter SHALL convert a list of item names into a single string
with one item per line, each line consisting of the bullet `•` followed by a
single space and the item name. Every item SHALL be exported regardless of its
bought status. The order of lines SHALL follow the list's display order
(unbought first, then bought, each by creation time). An item name that is blank
SHALL still be represented by its bullet line (blank lines are not dropped).
A list with no items SHALL export to an empty string.

Feature: items-export
Rule: One item per bullet-prefixed line, all items included, display order preserved

#### Scenario: Items export to bullet-prefixed lines in the exact order given

- **GIVEN** a list whose items in display order are "mleko", "jajka", and
  "chleb ciemny/bułki"
- **WHEN** the items are formatted for export
- **THEN** the result is exactly "• mleko\n• jajka\n• chleb ciemny/bułki\n",
  with the slash preserved

#### Scenario: Bought items are included in the export

- **GIVEN** a list containing both unbought items and bought items
- **WHEN** the items are formatted for export
- **THEN** the result contains every item, bought or not, each on its own line

#### Scenario: Display order is preserved with unbought items first

- **GIVEN** a list whose items, in display order, are an unbought item "mleko"
  followed by a bought item "jajka"
- **WHEN** the items are formatted for export
- **THEN** "mleko" appears on the line before "jajka"

#### Scenario: A list with no items exports to an empty string

- **GIVEN** a list with no items
- **WHEN** the items are formatted for export
- **THEN** the result is an empty string

### Requirement: Exported items are copied to the clipboard

The action SHALL fetch all items of the selected list, format them via the
export formatter, and copy the resulting text to the device clipboard. The
System ClipboardManager SHALL be the destination, with the exported text tagged
as a plain-text label "ShopList". After copying, the action SHALL notify the
user with a Toast confirmation. The export SHALL not modify any stored data.

Feature: items-export
Rule: Format all items, copy to clipboard, and confirm with a Toast

#### Scenario: Exporting a list copies its formatted items to the clipboard

- **GIVEN** a list with items and a user on the Lists screen
- **WHEN** the user chooses "Export items" from the list's context menu
- **THEN** the clipboard SHALL contain the bullet-prefixed text of all the
  list's items

#### Scenario: A confirmation Toast is shown after export

- **GIVEN** a user has just exported a list's items
- **WHEN** the copy to the clipboard completes
- **THEN** a Toast confirms that the items were copied

#### Scenario: Exporting does not alter the list or its items

- **GIVEN** a list with a known set of items
- **WHEN** the user exports the list
- **THEN** the list and its items remain unchanged in storage

### Requirement: Export is available from the Lists screen context menu

The Lists screen SHALL expose an "Export items" action in the long-press context
menu of each list row, alongside the existing Rename and Delete actions.

Feature: items-export
Rule: The export action is reachable from each list's context menu

#### Scenario: A list row offers an Export items action

- **GIVEN** a list row on the Lists screen
- **WHEN** the user long-presses the row
- **THEN** the context menu SHALL include an "Export items" item
