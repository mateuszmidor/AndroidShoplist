## MODIFIED Requirements

### Requirement: Users SHALL be able to add an item via the FAB

The items screen SHALL provide a FAB (+) that opens a dialog with a name field.
The create-item dialog SHALL open with the name field focused and the soft
keyboard requested, so the user can type immediately without tapping the field.
Confirming with a non-empty trimmed name SHALL create an item in the current
list. The dialog SHALL close without creating when cancelled, and SHALL NOT
submit a blank or whitespace-only name. The rename and import dialogs SHALL
retain their existing focus behavior.

Feature: items-screen
Rule: A FAB opens a create dialog that persists a new item in the current list

#### Scenario: Add an item from the FAB

- **GIVEN** the items screen for a list is visible
- **WHEN** the user taps the FAB, enters "Milk", and confirms
- **THEN** an item named "Milk" is persisted in the current list and shown on the
  items screen
- **AND** the create dialog is closed

#### Scenario: Name field is focused when the create dialog opens

- **GIVEN** the items screen for a list is visible
- **WHEN** the user taps the FAB and chooses "Add item"
- **THEN** the create-item dialog opens with the name field focused
- **AND** the soft keyboard is shown, so the user can type immediately

#### Scenario: Cancel the create dialog

- **GIVEN** the create-item dialog is open
- **WHEN** the user cancels
- **THEN** the dialog closes
- **AND** no item is created

#### Scenario: Blank name is not submitted

- **GIVEN** the create-item dialog is open
- **WHEN** the user enters only whitespace and confirms
- **THEN** no item is created
- **AND** the dialog remains open (or the confirm action is disabled)
