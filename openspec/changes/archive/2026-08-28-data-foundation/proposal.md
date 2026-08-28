# Proposal: data-foundation

## Why

Change 01a proved the build toolchain and app entry wiring, but there is no
persistence yet: `AppContainer` is an empty shell and the app holds no data.
Change 01b (TASKS.md) establishes the Room data layer for shopping lists, so the
lists screen (02) and items screen (03) build on a storage foundation that is
proven end-to-end and covered by automated tests before any feature UI exists.
Doing it now also locks the Room 3.0 + KSP choice (current stable per ADR-0004)
and schema-versioning discipline while the schema is still trivially version 1.

## What Changes

- Add the data-layer toolchain to the version catalog and build scripts:
  - KSP plugin (`com.google.devtools.ksp` 2.2.10-2.0.2, pinned to Kotlin 2.2.10)
  - Room 3.0 Gradle plugin (`androidx.room3` 3.0.2) with
    `room3 { schemaDirectory("$projectDir/schemas") }` for committed,
    reproducible schema export
  - Dependencies `androidx.room3:room3-runtime` (implementation) and
    `androidx.room3:room3-compiler` (ksp)
  - `kotlinx-coroutines-test` (androidTestImplementation) for Flow/`runTest`-based
    integration tests
- Introduce the lists data layer in `org.mateuszmidor.shoplist.data`:
  - `ShoppingListEntity` (table `shopping_lists`; `id: UUID` `@PrimaryKey`,
    `name: String`, `createdAt: Long`)
  - `Converters` (UUID <-> String `@TypeConverter`) so the UUID primary key maps
    to a TEXT column
  - `ShoppingListDao`: reactive `observeAll(): Flow<List<...>>` ordered by
    creation time, plus suspend `insert` / `rename` / `deleteById`
  - `ShoppingDatabase` (`@Database`, `version = 1`, `@TypeConverters`)
- Introduce `ShoppingListRepository` interface and
  `RoomShoppingListRepository(dao)` implementation; `create(name)` generates the
  entity id/timestamp and returns the new `UUID`, `rename`/`delete` map to DAO
  operations, `observeLists()` streams the flow.
- Wire the container: `AppContainer` gains a `Context` constructor parameter,
  builds the database + repository; `ShopListApp.onCreate` passes
  `applicationContext`.
- Add tests: in-memory Room integration tests for the DAO and the repository
  (androidTest, run on the A52 via `connectedDebugAndroidTest`), plus a JVM unit
  test for the UUID converter round-trip.

## Capabilities

### New Capabilities

- `list-persistence`: shopping lists can be created, renamed, deleted, and
  observed in creation order, persisted durably in an on-device Room database
  that survives app restarts.

### Modified Capabilities

- None (no existing specs beyond the scaffolding placeholder).

## Impact

- **Build config**: `gradle/libs.versions.toml` (KSP, Room 3), root
  `build.gradle.kts` and `app/build.gradle.kts` (plugins, `room3` block,
  dependencies).
- **Code**: new `data/` package files; `di/AppContainer.kt` changes signature
  (gains `Context`); `ShopListApp.kt` updated call site. UI layer untouched.
- **Tests**: new `test/` (JVM) and `androidTest/` (instrumented) sources. The
  instrumented tests require a connected device/emulator (A52 per README).
- **Schema artifacts**: Room schema JSON exported to `app/schemas/` and checked
  in (foundation for future migrations, cf. ARCHITECTURE risks).
- **Out of scope**: items entity/DAO/repository (03), any UI/ViewModel work (02),
  database migrations (version 1 remains current).