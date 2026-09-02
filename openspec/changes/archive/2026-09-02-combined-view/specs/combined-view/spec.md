## ADDED Requirements

### Requirement: Users SHALL be able to a start a combined view from a selection of lists

The lists screen SHALL allow the user to select multiple lists and start a
transient combined view containing all items from the selected lists. The
combined view is single-use and holds no state of its own.

Feature: combined-view
Rule: A combined view merges items from selected lists into one transient screen

#### Scenario: Start a combined view from two selected lists

- **GIVEN** the lists screen with lists "Pizza products" and "Cesar salad products"
- **WHEN** the user selects both lists and confirms the combine action
- **THEN** a combined view screen is shown containing all items from both lists

#### Scenario: Combined view is discarded on exit

- **GIVEN** a combined view is shown for two lists
- **WHEN** the user exits the combined view (system back)
- **THEN** the user returns to the lists screen
- **AND** the combined view holds no persisted state of its own

### Requirement: Combined view SHALL show items from selected lists with a source-list caption

Each item in the combined view SHALL be displayed with a small caption identifying
the list it comes from, and items SHALL be concatenated one-to-one — a name that
appears in multiple lists SHALL appear once per owning list, ungrouped (flat list).

Feature: combined-view
Rule: Items are flat with a source caption; duplicates are kept

#### Scenario: Items carry their source list caption

- **GIVEN** a combined view over "Pizza products" and "Cesar salad products"
- **WHEN** the combined view is shown
- **THEN** each item displays the name of the list it belongs to

#### Scenario: Duplicate names appear once per source list

- **GIVEN** both "Pizza products" and "Cesar salad products" contain an item named "cheese"
- **WHEN** the combined view over both lists is shown
- **THEN** two rows named "cheese" are shown — one captioned "Pizza products", one captioned "Cesar salad products"

### Requirement: Combined view SHALL order items unchecked-then-bought, then by name

The combined view SHALL display unchecked items above bought items; within each
section, items SHALL be ordered by name, with equal-named items ordered by
creation time. This mirrors the app's standard unchecked/checked split but sorts
by name (rather than creation time) within each section.

Feature: combined-view
Rule: Unchecked-first split; name ordering within each section; created-at tiebreak

#### Scenario: Unchecked items appear before bought items

- **GIVEN** a combined view containing one bought and one unchecked item
- **WHEN** the combined view is shown
- **THEN** the unchecked item appears above the bought item

#### Scenario: Items within a section are ordered by name

- **GIVEN** a combined view containing unchecked items "lettuce", "chips", and "eggs"
- **WHEN** the combined view is shown
- **THEN** the unchecked items are displayed in name order: chips, eggs, lettuce

#### Scenario: Equal-named items are ordered by creation time

- **GIVEN** a combined view with two unchecked items both named "cheese", where one was created before the other
- **WHEN** the combined view is shown
- **THEN** the earlier-created "cheese" appears before the later-created "cheese"

### Requirement: Combined view SHALL support checking/unchecking only

The combined view SHALL let the user toggle an item between bought and unbought.
It SHALL NOT offer add, rename, or delete for items.

Feature: combined-view
Rule: Combined view allows check/uncheck only

#### Scenario: Check an item in the combined view

- **GIVEN** an unchecked item in the combined view
- **WHEN** the user taps it
- **THEN** the item becomes checked (bought)

#### Scenario: Uncheck an item in the combined view

- **GIVEN** a checked item in the combined view
- **WHEN** the user taps it
- **THEN** the item becomes unchecked

#### Scenario: No add/rename/delete actions are available

- **GIVEN** the combined view is shown
- **WHEN** the user inspects the available actions
- **THEN** only check/uncheck is available
- **AND** no add, rename, or delete action is offered

### Requirement: Check/uncheck writes through to the owning list

Toggling an item in the combined view SHALL update the item in its owning list,
so the original list stays authoritative and reflects the change immediately.
The combined view does not store its own copy of the checked state.

Feature: combined-view
Rule: Toggling in the combined view updates the owning list

#### Scenario: Checking in the combined view reflects in the owning list

- **GIVEN** a combined view over "Pizza products" and "Cesar salad products" showing an unchecked "mozzarella" from "Pizza products"
- **WHEN** the user checks "mozzarella" in the combined view
- **THEN** "mozzarella" is marked bought in the "Pizza products" list

#### Scenario: Checking in the owning list reflects in the combined view

- **GIVEN** a combined view over "Pizza products" and "Cesar salad products"
- **WHEN** an item is marked bought in one owning list while the combined view is open
- **THEN** the combined view reflects the item as bought