## ADDED Requirements

### Requirement: Users SHALL be able to mark items as bought by tapping them

Tapping an item's row or its checkbox SHALL toggle the item's bought state.
Bought items SHALL display a checked checkbox and a struck-through, dimmed
label, and SHALL be shown in the checked section at the bottom of the list.
Tapping a bought item again SHALL uncheck it and return it to the unchecked
section in creation-time order. Long-press behavior (context menu) SHALL remain
unchanged.

Feature: items-screen
Rule: Tapping an item toggles its bought state and moves it between sections

#### Scenario: Mark an item as bought

- **GIVEN** an unchecked item displayed on the items screen
- **WHEN** the user taps the item's row
- **THEN** the item is marked bought, shown with a checked checkbox and a
  struck-through, dimmed label
- **AND** the item is displayed below the remaining unchecked items

#### Scenario: Uncheck a bought item by tapping again

- **GIVEN** a bought item displayed in the checked section
- **WHEN** the user taps the item's row again
- **THEN** the item is unchecked again, shown without the bought styling
- **AND** the item returns to the unchecked section in creation-time order

#### Scenario: Tapping the checkbox toggles bought state

- **GIVEN** an item row displayed on the items screen
- **WHEN** the user taps the item's checkbox
- **THEN** the item's bought state toggles
- **AND** the row moves to the corresponding section

## MODIFIED Requirements

### Requirement: Items screen SHALL display the items of the selected list

The items screen SHALL display all items of the passed list (by its UUID) from
the repository as a reactive list in two sections — unchecked items first, then
checked items, each section ordered by creation time (earliest first) — and
SHALL update automatically when the underlying data changes. Bought items SHALL
be visually distinguished with a checked indicator and a struck-through, dimmed
label.

Feature: items-screen
Rule: The items screen shows the selected list's items in two sections, unchecked first

#### Scenario: Observe items of a selected list in two sections

- **GIVEN** a shopping list containing items created in a known sequence, some of which are marked bought
- **WHEN** the user opens that list
- **THEN** the list's unchecked items are displayed first, ordered by creation time, earliest first
- **AND** the list's checked items are displayed after them, ordered by creation time, earliest first

#### Scenario: Newly added item appears at the end of the unchecked section

- **GIVEN** the items screen for a list is visible
- **WHEN** a new item is added
- **THEN** the new item appears as the last item of the unchecked section,
  before any checked items

## REMOVED Requirements