# ADR-0011: Pure business logic lives in a `domain` package

- Status: accepted
- Date: 2026-08-29

## Context and Problem Statement

Change 06 (import-items) introduces the app's first piece of standalone
business logic: parsing pasted text into item names. ARCHITECTURE.md places
business logic in ViewModels and dedicated domain logic, with a three-layer
split — UI / business logic / data — where Room and UI never call each other
directly. Until now every line of business logic has lived inside the
ViewModels and repositories, so there is no established home for a pure,
stateless unit like the parser. Where that logic lives is a choice every future
such unit (formatting, validation, other import/export parsing) will follow.

## Considered Options

- New `org.mateuszmidor.shoplist.domain` package for pure logic.
- Keep it in `data/` beside the repositories.
- Place it in `ui/items` next to the ViewModel.

## Decision Outcome

Chosen option: "new `domain` package", because it gives pure business logic a
dedicated seam matching ARCHITECTURE's layering, keeps deterministic logic
separate from side-effecting storage code, makes the logic trivially JVM-
testable with no Android dependencies, and gives future pure units a canonical
home. The package root stays `org.mateuszmidor.shoplist` per ADR-0005.

### Consequences

- Good, because pure logic becomes platform-independent and unit-testable at
  the fastest (JVM) test tier without an Android environment; the three-layer
  split (ADR-0002's package-by-layer discipline) becomes explicit; and future
  pure rules have an obvious home instead of drifting into repositories or
  ViewModels.
- Bad, because it introduces a new top-level package that currently contains a
  single object — a small structural first; contributors must judge what
  belongs in `domain` versus a repository or DAO.
- Follow-up: keep the parser and future domain units free of Android types and
  I/O (rule of thumb: deterministic and side-effect-free means `domain`);
  revisit the package only if it grows without earning its place.

## Follow-up

The import parser (`ListonicImportParser`) is the first occupant of the
`domain` package and establishes the seam that later pure units build on.