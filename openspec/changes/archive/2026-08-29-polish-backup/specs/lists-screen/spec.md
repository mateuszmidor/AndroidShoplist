## ADDED Requirements

### Requirement: Lists screen SHALL show a per-list item summary

The lists screen SHALL display, for each list, its total item count and its
bought item count, and SHALL update the summary when the underlying items
change.

Feature: lists-screen
Rule: Each list row shows its total and bought item counts

#### Scenario: List without items shows zero counts

- **GIVEN** the lists screen with a list that has no items
- **THEN** the list displays a summary of 0 items and 0 bought

#### Scenario: Summary reflects bought items

- **GIVEN** a list containing 3 items of which 1 is bought
- **WHEN** the lists screen is shown
- **THEN** the list displays a summary of 3 items with 1 bought

#### Scenario: Summary updates when items change

- **GIVEN** the lists screen showing a list with a summary
- **WHEN** an item in that list is marked bought
- **THEN** the list's summary updates to reflect the new bought count

## MODIFIED Requirements

### Requirement: Users SHALL be able to delete a list via the long-press context menu

Selecting delete from a list's context menu SHALL ask the user to confirm the
deletion before any list is removed; confirming SHALL remove that list from
the repository and the lists screen SHALL no longer display it, while
cancelling SHALL leave it unchanged.

Feature: lists-screen
Rule: The context menu delete action confirms, then removes a list

#### Scenario: Delete a list after confirmation

- **GIVEN** a repository containing lists "Groceries" and "Books"
- **WHEN** the user long-presses "Groceries", selects delete, and confirms
- **THEN** the lists screen no longer shows "Groceries"
- **AND** "Books" is still displayed

#### Scenario: Confirm dialog names the list and its item count

- **GIVEN** the user selects delete on a list named "Groceries" containing 12 items
- **WHEN** the deletion confirmation is shown
- **THEN** the confirmation names the list "Groceries" and its 12 items

#### Scenario: Cancelling deletion leaves the list intact

- **GIVEN** a repository containing lists "Groceries" and "Books"
- **WHEN** the user long-presses "Groceries", selects delete, and cancels
- **THEN** "Groceries" is still displayed on the lists screen