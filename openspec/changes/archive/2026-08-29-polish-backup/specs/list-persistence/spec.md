## ADDED Requirements

### Requirement: Lists are observable individually by identifier

The data layer SHALL expose a single shopping list as a reactive stream
identified by its UUID.

#### Scenario: Observe a list by its identifier

- **GIVEN** a database containing a list with a known UUID
- **WHEN** the list stream for that UUID is observed
- **THEN** the stream emits the list with that UUID

#### Scenario: Observing an unknown identifier emits no list

- **GIVEN** a database without a list for a given UUID
- **WHEN** the list stream for that UUID is observed
- **THEN** the stream does not emit a list

## MODIFIED Requirements

### Requirement: Lists are observable in creation order

The data layer SHALL expose all shopping lists as a reactive stream ordered by
creation time (earliest first), where each element carries the list's fields
plus its per-list summary: the total item count and the bought item count. The
stream SHALL re-emit whenever the underlying lists or their items change.

#### Scenario: Observe lists ordered by creation time

- **GIVEN** three lists created in a known sequence
- **WHEN** the lists stream is observed
- **THEN** the lists are emitted ordered by creation time, earliest first

#### Scenario: Newly created list appears in the stream

- **GIVEN** an observed lists stream
- **WHEN** a new list is created
- **THEN** the stream emits the new list as its last element

#### Scenario: Each list carries its total and bought item counts

- **GIVEN** a database containing a list with 3 items, 1 of which is bought
- **WHEN** the lists stream is observed
- **THEN** that list is emitted with a total item count of 3
- **AND** the list is emitted with a bought count of 1

#### Scenario: Lists without items carry zero counts

- **GIVEN** a database containing a list with no items
- **WHEN** the lists stream is observed
- **THEN** that list is emitted with a total count of 0 and a bought count of 0

#### Scenario: Stream re-emits when an item's bought state changes

- **GIVEN** an observed lists stream for a database with one list and one unchecked item
- **WHEN** that item is marked bought
- **THEN** the stream re-emits the list with a bought count of 1