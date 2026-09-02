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
as a plain-text label "ShopList". The export SHALL not modify any stored data.
Android 12+ shows a system-level clipboard confirmation automatically; the app
SHALL NOT show its own Toast to avoid duplicate confirmations.

Feature: items-export
Rule: Format all items and copy to clipboard

#### Scenario: Exporting a list copies its formatted items to the clipboard

- **GIVEN** a list with items and a user on the Lists screen
- **WHEN** the user chooses "Export items" from the list's overflow menu
- **THEN** the clipboard SHALL contain the bullet-prefixed text of all the
  list's items

#### Scenario: Exporting does not alter the list or its items

- **GIVEN** a list with a known set of items
- **WHEN** the user exports the list
- **THEN** the list and its items remain unchanged in storage

### Requirement: Export is available from the Lists screen row overflow menu

The Lists screen SHALL expose an "Export items" action in the overflow menu
(three vertical dots) of each list row, alongside the existing Rename and Delete
actions.

Feature: items-export
Rule: The export action is reachable from each list's overflow menu

#### Scenario: A list row offers an Export items action

- **GIVEN** a list row on the Lists screen
- **WHEN** the user taps the overflow menu button on the row
- **THEN** the dropdown SHALL include an "Export items" item
