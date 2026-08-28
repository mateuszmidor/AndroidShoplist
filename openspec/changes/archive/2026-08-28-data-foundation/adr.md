# ADR Review Manifest

- Status: completed
- Review date: 2026-08-28

## Review Summary

ADR review completed for this change.

## In-Force ADRs Reviewed

- `adr/0001-manual-di-via-appcontainer.md` - manual DI via `AppContainer`
  (constrains the container wiring in this change).
- `adr/0002-single-app-module.md` - single `:app` module, clean layers via
  packages (constrains the `data` package placement).
- `adr/0003-gradle-kotlin-dsl-version-catalog.md` - Kotlin DSL + central
  version catalog (all new Room/KSP/coroutines-test versions flow through
  `gradle/libs.versions.toml`).
- `adr/0004-dependency-version-policy.md` - current stable, no experimental
  libraries (underpins the Room 3.0 + KSP choice).
- `adr/0005-package-naming-org-mateuszmidor-shoplist.md` - package/
  applicationId `org.mateuszmidor.shoplist` (data layer lives under this
  namespace).

No ADR is superseded by this change; the design is coherent with all five.

## New Durable ADRs Created

- `adr/0006-use-room-3-for-local-data-layer.md` - adopt Room 3.0.2 on the
  `androidx.room3` artifacts with KSP (Kotlin 2.2.10) for the local data
  layer, recording the 3.0-over-2.8 decision and its consequences.

The full Context, Decision, and Consequences live in the repository-level ADR
file above and are not duplicated here. Remaining design decisions (UUID-as-TEXT
primary key, repository exposing the entity, `data` package layout, schema
export via the `androidx.room3` Gradle plugin) are implementation-level choices
documented in `design.md`, not durable architectural commitments.