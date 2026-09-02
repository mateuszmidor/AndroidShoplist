## ADDED Requirements

### Requirement: Users SHALL be able to select multiple lists via long-press

Long-pressing a list row SHALL enter a selection mode on the lists screen and
select that list. While in selection mode, tapping a row SHALL toggle it in/out
of the selection. Tapping a row outside selection mode SHALL navigate to that
list's items screen as before. Selection mode SHALL be exited (clearing the
selection) via system back or the combine action.

Feature: lists-screen
Rule: Long-press enters multi-select mode; taps toggle selection

#### Scenario: Long-press a list to enter selection mode

- **GIVEN** the lists screen showing "Groceries"
- **WHEN** the user long-presses "Groceries"
- **THEN** "Groceries" becomes selected and selection mode is active

#### Scenario: Tap a list in selection mode to toggle its selection

- **GIVEN** selection mode is active with "Groceries" selected
- **WHEN** the user taps "Groceries" again
- **THEN** "Groceries" is deselected

#### Scenario: Back exits selection mode and clears the selection

- **GIVEN** selection mode is active with lists selected
- **WHEN** the user presses system back
- **THEN** selection mode is exited
- **AND** the selection is cleared
- **AND** the user stays on the lists screen

### Requirement: Users SHALL be able to start a combined view via a Combine action

The lists screen SHALL provide a "Combine" action (top bar) that opens a
combined view over the currently selected lists. The action SHALL be enabled
when at least one list is selected.

Feature: lists-screen
Rule: A Combine action opens the combined view over the selected lists

#### Scenario: Combine action enabled with one selected list

- **GIVEN** selection mode is active with one list selected
- **WHEN** the user activates the Combine action
- **THEN** a combined view over that single list is shown

#### Scenario: Combine action enabled with multiple selected lists

- **GIVEN** selection mode is active with two lists selected
- **WHEN** the user activates the Combine action
- **THEN** a combined view over both selected lists is shown

## MODIFIED Requirements

### Requirement: Users SHALL be able to rename a list via the row overflow menu

Long-pressing a list row SHALL NOT open the rename/delete menu (it enters
selection mode instead). Each list row SHALL provide an overflow menu button
(three vertical dots) that, when tapped, opens a dropdown with actions including
rename. The rename action SHALL open a dialog prefilled with the current name.
Confirming with a non-empty trimmed name SHALL update the list name; cancelling
SHALL leave the list unchanged.

Feature: lists-screen
Rule: The row overflow menu offers a rename action

#### Scenario: Rename a list from the overflow menu

- **GIVEN** a list named "Groceries" is displayed with an overflow menu button
- **WHEN** the user taps the overflow button, selects rename, enters "Weekly groceries", and confirms
- **THEN** the lists screen shows the list with the updated name "Weekly groceries"

#### Scenario: Cancel rename leaves the name unchanged

- **GIVEN** the rename dialog is open prefilled with "Groceries"
- **WHEN** the user cancels
- **THEN** the list is still named "Groceries"

### Requirement: Users SHALL be able to delete a list via the row overflow menu

Each list row SHALL provide an overflow menu button (three vertical dots) that,
when tapped, opens a dropdown with actions including delete. Tapping delete SHALL
ask the user to confirm the deletion before any list is removed; confirming SHALL
remove that list from the repository and the lists screen SHALL no longer display
it, while cancelling SHALL leave it unchanged.

Feature: lists-screen
Rule: The row overflow menu offers a delete action that confirms, then removes a list

#### Scenario: Delete a list after confirmation

- **GIVEN** a repository containing lists "Groceries" and "Books"
- **WHEN** the user taps the overflow button on "Groceries", selects delete, and confirms
- **THEN** the lists screen no longer shows "Groceries"
- **AND** "Books" is still displayed

#### Scenario: Confirm dialog names the list and its item count

- **GIVEN** the user selects delete on a list named "Groceries" containing 12 items
- **WHEN** the deletion confirmation is shown
- **THEN** the confirmation names the list "Groceries" and its 12 items

#### Scenario: Cancelling deletion leaves the list intact

- **GIVEN** a repository containing lists "Groceries" and "Books"
- **WHEN** the user taps the overflow button on "Groceries", selects delete, and cancels
- **THEN** "Groceries" is still displayed on the lists screen

### Requirement: Users SHALL be able to export items via the row overflow menu

Each list row SHALL provide an overflow menu button (three vertical dots) that,
when tapped, opens a dropdown with actions including export. The export action
SHALL copy the list's items to the clipboard. Android 12+ shows a system-level
clipboard confirmation automatically; the app SHALL NOT show its own Toast.

Feature: lists-screen
Rule: The row overflow menu offers an export action

#### Scenario: Export items from the overflow menu

- **GIVEN** a list row on the Lists screen
- **WHEN** the user taps the overflow button and selects export
- **THEN** the clipboard SHALL contain the bullet-prefixed text of all the list's items