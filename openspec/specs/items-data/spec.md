# items-data Spec

## Purpose

Provide the data layer for persisting shopping list items on-device. Items are
created within a shopping list with a generated UUID, a `bought` flag, and a
creation timestamp, persisted durably in the on-device database, exposed as a
reactive stream filtered by owning list and ordered by creation time, and
support rename, deletion, and cascade deletion together with their owning list.

## Requirements

### Requirement: Item creation persists with generated identifier, scoped to a list

The data layer SHALL create an item within a given shopping list, with a name, a
generated UUID identifier, a `bought` flag defaulting to false, and the creation
timestamp, and SHALL persist it durably in the on-device database.

Feature: items-data
Rule: Item creation returns a unique generated identifier scoped to a list

#### Scenario: Create an item in a list

- **GIVEN** a shopping list exists
- **WHEN** an item named "Milk" is created in that list
- **THEN** an item with that name is persisted
- **AND** the item has a non-null UUID identifier, a recorded creation time, and
  a `bought` flag of false
- **AND** the item is associated with that shopping list

#### Scenario: Created item survives database re-open

- **GIVEN** a database containing a created item
- **WHEN** the database is closed and re-opened
- **THEN** the previously created item is still present with the same name,
  identifier, list, creation time, and bought flag

### Requirement: Items are observable per list in creation order

The data layer SHALL expose all items of a given shopping list as a reactive
stream ordered by creation time (earliest first).

Feature: items-data
Rule: Items of a list are emitted in creation order

#### Scenario: Observe items of a list ordered by creation time

- **GIVEN** three items created in that list in a known sequence
- **WHEN** the items stream for that list is observed
- **THEN** the items are emitted ordered by creation time, earliest first

#### Scenario: Newly created item appears in its list's stream

- **GIVEN** an observed items stream for a list
- **WHEN** a new item is created in that list
- **THEN** the stream emits the new item as its last element

### Requirement: Items are scoped to their owning list

The data layer SHALL return only the items belonging to the requested list; items
of other lists SHALL NOT appear in that list's stream.

Feature: items-data
Rule: Observation is filtered by owning list

#### Scenario: Items of another list do not appear

- **GIVEN** two shopping lists "A" and "B", each containing items
- **WHEN** the items stream for list "A" is observed
- **THEN** only the items of list "A" are emitted
- **AND** none of list "B"'s items are included

### Requirement: Item rename

The data layer SHALL update the name of an existing item identified by its UUID.

Feature: items-data
Rule: An item's name can be changed by identifier

#### Scenario: Rename an existing item

- **GIVEN** an item named "Milk" in a list
- **WHEN** that item is renamed to "Soy milk"
- **THEN** the persisted item keeps its identifier, list, creation time, and
  bought flag
- **AND** the observed stream emits the item with the updated name

### Requirement: Item deletion

The data layer SHALL delete an item identified by its UUID from its list.

Feature: items-data
Rule: An item can be removed by identifier

#### Scenario: Delete an existing item

- **GIVEN** a list containing an item
- **WHEN** that item is deleted
- **THEN** the observed stream for the list no longer contains the item

#### Scenario: Deleting an unknown identifier has no effect

- **GIVEN** a list whose items do not include a given UUID
- **WHEN** deletion is attempted with that UUID
- **THEN** the database state is unchanged

### Requirement: Deleting a list deletes its items

When a shopping list is deleted, the data layer SHALL also delete every item
belonging to that list; the items SHALL NOT be retained as orphans.

Feature: items-data
Rule: Removing a list cascades to its items

#### Scenario: Deleting a list removes its items

- **GIVEN** a shopping list containing items
- **WHEN** that shopping list is deleted
- **THEN** the items that belonged to that list are removed from the database