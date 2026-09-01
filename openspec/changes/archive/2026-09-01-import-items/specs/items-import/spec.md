# items-import Spec

## ADDED Requirements

### Requirement: Pasted text is parsed into one item name per line

The import parser SHALL convert pasted text into a list of item names,
interpreting each non-blank line as one item: at most one leading bullet
(any of `•`, `-`, `*`) together with any following whitespace SHALL be
stripped, surrounding whitespace SHALL be trimmed, and a line that yields an
empty name after stripping SHALL be skipped. Line endings SHALL be tolerated in
both LF and CRLF form. Equal names SHALL NOT be deduplicated — every parsed
line SHALL be preserved as a separate item, thus duplicates are kept. Any `/`
characters within a name SHALL be preserved verbatim. A paste that yields no
names SHALL produce an empty result.

Feature: items-import
Rule: One pasted line becomes one item name, verbatim except for bullets and whitespace

#### Scenario: Typical Listonic export parses to item names with slashes preserved

- **GIVEN** pasted text consisting of the lines "• mleko", "• jajka", and
  "• chleb ciemny/bułki"
- **WHEN** the text is parsed
- **THEN** the result contains exactly the names "mleko", "jajka", and
  "chleb ciemny/bułki" in that order, with the slash preserved

#### Scenario: Dash and asterisk bullets are stripped along with surrounding whitespace

- **GIVEN** pasted text consisting of the lines "-mleko", " * bułki ", and
  "jajka"
- **WHEN** the text is parsed
- **THEN** the result contains exactly the names "mleko", "bułki", and "jajka",
  each free of its bullet and surrounding whitespace

#### Scenario: Blank lines and bare bullets are skipped

- **GIVEN** pasted text "mleko", "", "   ", "•", and "jajka"
- **WHEN** the text is parsed
- **THEN** the result contains exactly the names "mleko" and "jajka"

#### Scenario: Duplicates are kept

- **GIVEN** pasted text consisting of the lines "mleko" and "mleko"
- **WHEN** the text is parsed
- **THEN** the result contains the name "mleko" twice, in pasted order

#### Scenario: CRLF line endings are tolerated

- **GIVEN** pasted text "\u2022 mleko\r\n\u2022 jajka\r\n"
- **WHEN** the text is parsed
- **THEN** the result contains exactly the names "mleko" and "jajka"

#### Scenario: A paste with no parseable item yields an empty result

- **GIVEN** pasted text that is blank, whitespace-only, or consists only of
  bullet characters such as "•"
- **WHEN** the text is parsed
- **THEN** the result is empty

### Requirement: Imported items are appended atomically to the target list in pasted order

The data layer SHALL append all parsed item names to an existing shopping list
in a single all-or-nothing operation: if inserting the batch fails for any
reason, none of its items SHALL be persisted and the list SHALL be unchanged.
Each imported item SHALL be created within the target list with a generated
UUID identifier, a `bought` flag of false, and a creation timestamp chosen so
the batch is emitted under the standard ordering (unchecked items first by
creation time, then checked) with the items of one import in the pasted order,
appearing after the list's pre-existing unchecked items and before any checked
items.

Feature: items-import
Rule: A batch append is atomic and preserves the pasted order within the unchecked section

#### Scenario: Appending a batch places all imported items after existing unchecked items

- **GIVEN** a shopping list containing an unchecked item "Bread"
- **WHEN** the names "Milk", "Eggs", and "Mleko" are appended to that list in
  that order
- **THEN** the list's item stream emits "Bread", then "Milk", then "Eggs",
  then "Mleko"
- **AND** each imported item has a generated UUID, a `bought` flag of false,
  and is associated with the same list

#### Scenario: Failure to insert the batch persists none of its items

- **GIVEN** a shopping list and a batch of item names of which at least one
  cannot be inserted (for example the batch references a list that does not
  satisfy the list ownership constraint)
- **WHEN** the batch append is attempted
- **THEN** the append fails
- **AND** none of the batch's items are persisted
- **AND** the list's item stream is unchanged

### Requirement: The items screen offers FAB actions and a paste-based import dialog

The items screen SHALL present its FAB as a small menu with two actions: "Add
item", which opens the existing single-item create dialog, and "Import from
Listonic", which opens the import dialog. The import dialog SHALL provide a
multi-line paste field and SHALL target the currently open list without any
list selector. The dialog's import action SHALL be disabled whenever the
pasted text yields no items, be enabled otherwise, and SHALL, when confirmed,
append the parsed items to the open list and close the dialog. Cancelling the
dialog SHALL close it without importing anything.

Feature: items-import
Rule: The items screen serves single-item add and paste-import from one FAB menu

#### Scenario: The FAB menu offers both add and import

- **GIVEN** the items screen for a list is visible
- **WHEN** the user taps the FAB
- **THEN** a menu appears offering "Add item" and "Import from Listonic"

#### Scenario: The import dialog targets the open list with no list selector

- **GIVEN** the items screen is open for a list named "Groceries"
- **WHEN** the user selects "Import from Listonic"
- **THEN** a dialog with a multi-line paste field is shown
- **AND** the dialog asks for no list name or selection — the target is the
  open list "Groceries"

#### Scenario: Import is blocked while the paste yields no items

- **GIVEN** the import dialog is open
- **WHEN** the pasted text yields no items (blank, whitespace-only, or bare
  bullets such as "•")
- **THEN** the import action is disabled

#### Scenario: Import is enabled once the paste yields items

- **GIVEN** the import dialog is open
- **WHEN** the user pastes the lines "• mleko" and "• jajka"
- **THEN** the import action is enabled

#### Scenario: Confirming the dialog imports into the open list and closes it

- **GIVEN** the import dialog is open for list "Groceries" with pasted text
  that yields the names "mleko" and "jajka"
- **WHEN** the user confirms the import
- **THEN** both "mleko" and "jajka" appear in the open list's items
- **AND** the import dialog closes

#### Scenario: Cancelling the dialog imports nothing

- **GIVEN** the import dialog is open with pasted text
- **WHEN** the user cancels
- **THEN** the dialog closes
- **AND** the open list's items are unchanged

### Requirement: The ViewModel import action is a no-op for empty parse results

The items ViewModel SHALL expose an import action that takes raw pasted text,
parses it with the import parser, and — when the parse result is empty — SHALL
perform no data-layer write. When the parse result is non-empty, the ViewModel
SHALL delegate the parsed names to the data layer's batch append for the open
list, so the imported items appear in the UI state.

Feature: items-import
Rule: The ViewModel blocks empty parses from reaching the data layer and imports otherwise

#### Scenario: Importing text with items appends them to the open list

- **GIVEN** an items ViewModel for a list with one existing unchecked item
  "Bread"
- **WHEN** the import action is invoked with text that parses to "Milk" and
  "Eggs"
- **THEN** the ViewModel requests the data layer to append "Milk" and "Eggs"
  to that list
- **AND** the UI state emits "Bread", "Milk", "Eggs" in that order

#### Scenario: Importing text with no items performs no data-layer write

- **GIVEN** an items ViewModel for a list
- **WHEN** the import action is invoked with text that parses to no items
  (blank, whitespace-only, or bare bullets such as "•")
- **THEN** the data layer is not called
- **AND** the list's items and UI state are unchanged