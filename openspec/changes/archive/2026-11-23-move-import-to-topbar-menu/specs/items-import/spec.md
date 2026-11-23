## MODIFIED Requirements

### Requirement: The items screen offers a top-bar import action and a paste-based import dialog

The items screen SHALL present a `MoreVert` overflow action in the top app bar.
The overflow menu SHALL offer "Import from Listonic", which opens the import
dialog. The FAB SHALL NOT present an import action; adding items remains the
FAB's direct action as defined by the items-screen capability.

The import dialog SHALL provide a multi-line paste field and SHALL target the
currently open list without any list selector. The dialog's import action SHALL
be disabled whenever the pasted text yields no items, be enabled otherwise, and
SHALL, when confirmed, append the parsed items to the open list and close the
dialog. Cancelling the dialog SHALL close it without importing anything.

Feature: items-import
Rule: The items screen serves paste-import from a top-bar overflow menu

#### Scenario: The MoreVert menu offers import from Listonic

- **GIVEN** the items screen for a list is visible
- **WHEN** the user taps the `MoreVert` overflow action in the top app bar
- **THEN** a menu appears offering "Import from Listonic"

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
