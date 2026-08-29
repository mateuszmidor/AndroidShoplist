## ADDED Requirements

### Requirement: Item bought state can be toggled

The data layer SHALL flip the `bought` flag of an existing item identified by
its UUID. Toggling SHALL NOT change the item's name, identifier, owning list, or
creation time.

Feature: items-data
Rule: An item's bought flag can be toggled by identifier

#### Scenario: Toggle an item from unchecked to checked

- **GIVEN** an unchecked item in a list
- **WHEN** the item's bought state is toggled
- **THEN** the persisted item has a `bought` flag of true
- **AND** the observed stream emits it within the checked section

#### Scenario: Toggle an item from checked back to unchecked

- **GIVEN** a checked item in a list
- **WHEN** the item's bought state is toggled
- **THEN** the persisted item has a `bought` flag of false
- **AND** the observed stream emits it within the unchecked section, ordered by
  its creation time

#### Scenario: Toggling preserves identity and other fields

- **GIVEN** an item in a list
- **WHEN** its bought state is toggled
- **THEN** the item keeps its identifier, name, owning list, and creation time

## MODIFIED Requirements

### Requirement: Items are observable per list in creation order

The data layer SHALL expose all items of a given shopping list as a reactive
stream ordered in two sections: unchecked items first, then checked items;
within each section items are ordered by creation time (earliest first).

Feature: items-data
Rule: Items of a list are emitted with unchecked items first, then checked, each by creation time

#### Scenario: Observe items of a list in two-section order

- **GIVEN** a list with items created in a known sequence, some of which are marked bought
- **WHEN** the items stream for that list is observed
- **THEN** the unchecked items are emitted first, ordered by creation time, earliest first
- **AND** the checked items are emitted after them, ordered by creation time, earliest first

#### Scenario: Newly created item appears as the last unchecked item

- **GIVEN** an observed items stream for a list
- **WHEN** a new item is created in that list
- **THEN** the stream emits the new item as the last item of the unchecked
  section, before any checked items

## REMOVED Requirements