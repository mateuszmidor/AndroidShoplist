# lists-screen Spec

## Purpose

Present the lists screen of the ShopList app: the navigation start destination
that displays all shopping lists ordered by creation time, supports creating,
renaming, and deleting a list, and navigates to a list's items screen.

## Requirements

### Requirement: Lists SHALL be displayed in creation order

The lists screen SHALL display all shopping lists from the repository as a
reactive list ordered by creation time (earliest first), and SHALL update
automatically when the underlying data changes.

Feature: lists-screen
Rule: The lists screen shows all lists from the repository, earliest created first

#### Scenario: Observe lists ordered by creation time

- **GIVEN** a repository containing three lists created in a known sequence
- **WHEN** the lists screen is shown
- **THEN** all three lists are displayed ordered by creation time, earliest first

#### Scenario: Newly created list appears on screen

- **GIVEN** the lists screen is visible
- **WHEN** a new list is created
- **THEN** the new list appears as the last item in the display

### Requirement: Lists screen SHALL be the navigation start destination

The application SHALL launch with the lists screen as the first (start)
destination; no other screen is shown before it.

Feature: lists-screen
Rule: The application opens on the lists screen

#### Scenario: App launch shows lists screen

- **GIVEN** the application is launched
- **THEN** the lists screen is displayed as the start destination

### Requirement: Users SHALL be able to create a new list via the FAB

The lists screen SHALL provide a FAB (+) that opens a dialog with a name field.
Confirming with a non-empty trimmed name SHALL create a list with that name.
The dialog SHALL close without creating when cancelled, and SHALL NOT submit a
blank or whitespace-only name.

Feature: lists-screen
Rule: A FAB opens a create dialog that persists a new list

#### Scenario: Create a list from the FAB

- **GIVEN** the lists screen is visible
- **WHEN** the user taps the FAB, enters "Weekly groceries", and confirms
- **THEN** a list named "Weekly groceries" is persisted and shown on the lists screen
- **AND** the create dialog is closed

#### Scenario: Cancel the create dialog

- **GIVEN** the create dialog is open
- **WHEN** the user cancels
- **THEN** the dialog closes
- **AND** no list is created

#### Scenario: Blank name is not submitted

- **GIVEN** the create dialog is open
- **WHEN** the user enters only whitespace and confirms
- **THEN** no list is created
- **AND** the dialog remains open (or the confirm action is disabled)

### Requirement: Users SHALL be able to rename a list via the long-press context menu

Long-pressing a list row SHALL open a context menu offering rename and delete.
The rename action SHALL open a dialog prefilled with the current name.
Confirming with a non-empty trimmed name SHALL update the list name; cancelling
SHALL leave the list unchanged.

Feature: lists-screen
Rule: Long-pressing a list opens a context menu with a rename action

#### Scenario: Rename a list from the context menu

- **GIVEN** a list named "Groceries" is displayed
- **WHEN** the user long-presses it, selects rename, enters "Weekly groceries", and confirms
- **THEN** the lists screen shows the list with the updated name "Weekly groceries"

#### Scenario: Cancel rename leaves the name unchanged

- **GIVEN** the rename dialog is open prefilled with "Groceries"
- **WHEN** the user cancels
- **THEN** the list is still named "Groceries"

### Requirement: Users SHALL be able to delete a list via the long-press context menu

Selecting delete from a list's context menu SHALL remove that list from the
repository; the lists screen SHALL no longer display it.

Feature: lists-screen
Rule: The context menu delete action removes a list

#### Scenario: Delete a list from the context menu

- **GIVEN** a repository containing lists "Groceries" and "Books"
- **WHEN** the user long-presses "Groceries" and selects delete
- **THEN** the lists screen no longer shows "Groceries"
- **AND** "Books" is still displayed

### Requirement: Users SHALL be able to navigate to the items screen for a list

Tapping a list row SHALL navigate to the items screen for that list,
passing the list's UUID identifier. The items screen SHALL offer back
navigation to the lists screen.

Feature: lists-screen
Rule: Tapping a list navigates to its items screen with the list identifier

#### Scenario: Tap a list to open its items screen

- **GIVEN** a list is displayed
- **WHEN** the user taps the list row
- **THEN** the items screen for that list is shown with the list's identifier

#### Scenario: Back returns to the lists screen

- **GIVEN** the items screen for a list is shown
- **WHEN** the user navigates back
- **THEN** the lists screen is shown again with the previous state preserved