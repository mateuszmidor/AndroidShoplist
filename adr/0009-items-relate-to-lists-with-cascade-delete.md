# ADR-0009: Items relate to lists with cascade delete

- Status: accepted
- Date: 2026-08-29

## Context and Problem Statement

Change 03 introduces the `shopping_items` table. Each item belongs to exactly one
shopping list (`list_id`). The lists screen (change 02) already supports deleting
a list, and ARCHITECTURE.md scopes the data model to lists and items as a
parent/child relation. The data layer must decide how item rows relate to their
owning list and what happens to them when that list is deleted. ADR-0006 commits
the project to Room 3 for local persistence but does not prescribe the schema's
relational shape.

## Decision

Give `shopping_items` a foreign key on `list_id` referencing
`shopping_lists.id` with `ON DELETE CASCADE`, and index `list_id`. Deleting a
shopping list deletes its items in the same transaction; orphaned item rows can
never exist. Each item is scoped to exactly one owning list, and item
observation is always filtered by list.

## Consequences

- Good, because the database enforces referential integrity rather than relying
  on application code to clean up items when a list is deleted; no orphaned item
  rows can accumulate; and the parent/child schema matches the domain model
  (lists contain items), giving future changes (e.g. per-list item counts in
  change 05) a reliable basis.
- Bad, because cascade deletion is unconditional: deleting a list irreversibly
  removes its items with no confirmation at the data layer (mitigation lives in
  the UI, which already asks before the destructive action); and because schema
  changes involving the relationship must keep the foreign key and cascade
  semantics in mind.
- Follow-up: item observation and writing remain scoped by `list_id`; later
  changes that add behaviour to items (bought toggling and ordering) extend this
  table without altering the ownership relationship.
