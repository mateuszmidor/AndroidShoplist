# items-screen Spec

## Purpose

Present the items screen of the ShopList app: reached from the lists screen via
the list identifier, displays the selected list's items from the repository as
a reactive list ordered by creation time, and supports adding an item via a FAB
plus renaming and deleting items via long-press context menus.

## Requirements

### Requirement: Items screen SHALL display the items of the selected list

The items screen SHALL display all items of the passed list (by its UUID) from
the repository as a reactive list ordered by creation time, and SHALL update
automatically when the underlying data changes.

Feature: items-screen
Rule: The items screen shows the selected list's items, earliest created first

#### Scenario: Observe items of a selected list

- **GIVEN** a shopping list containing items created in a known sequence
- **WHEN** the user opens that list
- **THEN** all of the list's items are displayed ordered by creation time,
  earliest first

#### Scenario: Newly added item appears on screen

- **GIVEN** the items screen for a list is visible
- **WHEN** a new item is added
- **THEN** the new item appears as the last item in the display

### Requirement: Items screen is reached from the lists screen via the list identifier

Tapping a shopping list on the lists screen SHALL open the items screen for that
list, carrying the list's UUID. The items screen SHALL offer back navigation to
the lists screen.

Feature: items-screen
Rule: A list opens its items screen with the list identifier

#### Scenario: Open the items screen for a list

- **GIVEN** a shopping list is displayed on the lists screen
- **WHEN** the user taps the list row
- **THEN** the items screen for that list is shown
- **AND** the items displayed belong to the tapped list

#### Scenario: Back returns to the lists screen

- **GIVEN** the items screen for a list is shown
- **WHEN** the user navigates back
- **THEN** the lists screen is shown again with its previous state preserved

### Requirement: Users SHALL be able to add an item via the FAB

The items screen SHALL provide a FAB (+) that opens a dialog with a name field.
Confirming with a non-empty trimmed name SHALL create an item in the current
list. The dialog SHALL close without creating when cancelled, and SHALL NOT
submit a blank or whitespace-only name.

Feature: items-screen
Rule: A FAB opens a create dialog that persists a new item in the current list

#### Scenario: Add an item from the FAB

- **GIVEN** the items screen for a list is visible
- **WHEN** the user taps the FAB, enters "Milk", and confirms
- **THEN** an item named "Milk" is persisted in the current list and shown on the
  items screen
- **AND** the create dialog is closed

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

### Requirement: Users SHALL be able to rename an item via the long-press context menu

Long-pressing an item row SHALL open a context menu offering rename and delete.
The rename action SHALL open a dialog prefilled with the current name.
Confirming with a non-empty trimmed name SHALL update the item name; cancelling
SHALL leave the item unchanged.

Feature: items-screen
Rule: Long-pressing an item opens a context menu with a rename action

#### Scenario: Rename an item from the context menu

- **GIVEN** an item named "Milk" is displayed in a list
- **WHEN** the user long-presses it, selects rename, enters "Soy milk", and
  confirms
- **THEN** the items screen shows the item with the updated name "Soy milk"

#### Scenario: Cancel rename leaves the name unchanged

- **GIVEN** the rename dialog is open prefilled with "Milk"
- **WHEN** the user cancels
- **THEN** the item is still named "Milk"

### Requirement: Users SHALL be able to delete an item via the long-press context menu

Selecting delete from an item's context menu SHALL remove that item from the
repository; the items screen SHALL no longer display it.

Feature: items-screen
Rule: The context menu delete action removes an item from its list

#### Scenario: Delete an item from the context menu

- **GIVEN** a list containing items "Milk" and "Bread"
- **WHEN** the user long-presses "Milk" and selects delete
- **THEN** the items screen no longer shows "Milk"
- **AND** "Bread" is still displayed