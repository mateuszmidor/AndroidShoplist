# ADR-0001: Manual dependency injection via AppContainer

- Status: accepted
- Date: 2026-08-28

## Context and Problem Statement

The app needs a single place where dependencies (Room database, repositories,
and later ViewModels) are constructed and shared. The ARCHITECTURE.md explicitly
mandates manual DI with no framework (no Hilt/Dagger/Koin) in initial scope, and
the LEARNING goal favors code that is easy to reason about for a Kotlin/Android
beginner.

## Considered Options

- Manual DI via an `AppContainer` owned by an `Application` class.
- Hilt (Jetpack's DI framework).
- Koin (runtime/service-locator DI).

## Decision Outcome

Chosen option: "Manual DI via `AppContainer`", because the object graph is tiny
(single user, local data), it needs no annotation processing or extra framework,
it keeps every dependency in one visible place, and it is the best fit for
learning the layering without framework magic.

### Consequences

- Good, because the whole dependency graph is explicit and testable without DI
  framework tooling.
- Bad, because wiring code grows by hand as more repositories/ViewModels are
  added; acceptable at this app's scale.
