# ADR-0008: Source ViewModels via CreationExtras initializer from AppContainer

- Status: accepted
- Date: 2026-08-29

## Context and Problem Statement

The app uses manual DI via an `AppContainer` owned by the `Application` class
(ADR-0001) in a single `:app` module (ADR-0002). Every screen needs a
per-screen ViewModel that receives container-provided repositories, is
lifecycle-scoped, and survives configuration changes. ARCHITECTURE.md demands
stateless ViewModels that coordinate state flowing from storage as reactive
streams. The options are a hand-rolled `ViewModelProvider.Factory`, the
`viewModel(initializer)` CreationExtras overload from
`lifecycle-viewmodel-compose`, or instantiating the ViewModel directly via
`remember`.

## Decision Outcome

Source every screen's ViewModel with the `viewModel { ... }` CreationExtras
initializer overload, closing over the repository obtained from
`AppContainer`. ViewModels expose `StateFlow<UiState>` derived from the
repository's reactive `Flow` via `stateIn(viewModelScope, WhileSubscribed(...))`;
event functions delegate writes to the repository inside `viewModelScope` and
never mutate UI state directly, keeping storage as the single source of truth.

### Consequences

- Good, because ViewModels are lifecycle-scoped and configuration-change safe
  with idiomatic platform handling, the wiring point stays in one place
  (`AppContainer`, per ADR-0001) without a DI framework, and the
  writes-via-repository / reads-via-Flow split makes ViewModels trivially
  testable against a fake repository.
- Bad, because the initializer captures the container at the composition site
  and must be kept thin (no logic beyond constructor wiring); and because a
  future multi-module split would need to revisit how repositories reach the
  initializer.