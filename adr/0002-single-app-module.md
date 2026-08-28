# ADR-0002: Single :app module

- Status: accepted
- Date: 2026-08-28

## Context and Problem Statement

The project must choose between a single-module Android app and a multi-module
structure (feature/data/design-system modules). The app is small, single-user,
and ARCHITECTURE.md marks extensibility as MEDIUM with an explicit principle of
no speculative abstraction for unplanned features.

## Considered Options

- Single `:app` module.
- Multi-module split from day one (e.g., `:app`, `:data`, `:core`).

## Decision Outcome

Chosen option: "Single `:app` module", because the codebase is small and there is
no team or planned feature set that would benefit from separate modules now.

### Consequences

- Good, because build and navigation are simple and there is no module-boundary
  overhead to learn at the start.
- Bad, because a future split would be a one-time refactor; the layers (UI /
  business / data) are still kept clean within the module via packages.
