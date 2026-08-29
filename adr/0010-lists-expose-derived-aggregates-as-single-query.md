# ADR-0010: List observation exposes derived aggregates as a single data-layer query

- Status: accepted
- Date: 2026-08-29

## Context and Problem Statement

The lists screen needs each list's total and bought item counts (change 05,
per-list summary). The counts are derived data: they depend on rows in
`shopping_items`, after ADR-0009 fixed the parent/child relation. The data
layer must decide where derived aggregates are computed — inside Room as a
single reactive query, or in the ViewModel by combining two flows
(lists + an aggregate stream) — and that decision sets the pattern for every
future aggregate the UI needs.

## Decision

Derived aggregates over the relational model are computed in the data layer as
a single reactive query and exposed as one `Flow` whose elements carry both the
row fields and the derived values (`ListSummary`: list fields + `totalCount` /
`boughtCount`). ViewModels consume such flows as-is and do not combine raw
entity streams to derive aggregates.

### Consequences

- Good, because names and aggregates are emitted atomically from one query (no
  transient mismatch while combining separate flows), Room invalidates the
  query on every table it references — including subqueries over
  `shopping_items` — so counts stay live automatically, the subqueries are
  cheap on the `list_id` index, and the ViewModel stays a thin mapping layer
  (ADR-0008).
- Bad, because the repository's `observeLists()` return type changes from
  raw entities to summary rows (interface and test doubles must follow), and
  derived columns must be mapped per query (column aliases into a dedicated
  row type).
- Trade-off accepted: computing aggregates in SQL is preferred over
  client-side aggregation because the data layer remains the single
  source of truth and the ViewModel never holds derived state of its own.