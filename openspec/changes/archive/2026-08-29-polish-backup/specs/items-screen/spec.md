## ADDED Requirements

### Requirement: Items screen SHALL display the opened list's name in the top bar

The items screen SHALL show the name of the list it was opened for in its top
app bar, rather than a generic label.

Feature: items-screen
Rule: The top bar shows the opened list's name

#### Scenario: Top bar shows the list name

- **GIVEN** the items screen is opened for a list named "Groceries"
- **THEN** the top bar displays "Groceries"

#### Scenario: Top bar reflects a renamed list

- **GIVEN** the items screen is open for a list
- **WHEN** the list is renamed, for example to "Weekly groceries"
- **THEN** the top bar updates to display "Weekly groceries"