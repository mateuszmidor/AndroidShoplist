# Proposal: project-scaffolding

## Why

The repository currently contains only the default Android Studio empty-activity
template. Before any feature work (01b Data Foundation, 02 Lists screen, ...), the
project must prove that the build/toolchain works end-to-end and that the
application entry wiring (Application -> AppContainer -> MainActivity -> Compose)
is in place. Doing this now also locks cheap-now/expensive-later decisions
(package naming, module structure, dependency policy) before they are hard to
change.

## What Changes

- Rename the base package/namespace/applicationId from the template placeholder
  `com.example.shoplist` to `org.mateuszmidor.shoplist`, including moving source
  directories, updating imports, and the AndroidManifest.
- Introduce a manual-DI composition root:
  - `ShopListApp : Application` (registered via `android:name` in the manifest).
  - `AppContainer` (empty shell) that will later provide Room-backed repositories.
- Introduce an empty `App()` composable as the app entry point, replacing the
  template `Greeting("Android")` placeholder; wire `ShopListApp`/`AppContainer`
  into `MainActivity`.
- Keep and repair the two template smoke tests (`ExampleUnitTest`,
  `ExampleInstrumentedTest`) so they move to the new package and prove the JVM
  and instrumented test toolchains run.
- Record durable architecture decisions as ADRs (manual DI, single module,
  Gradle Kotlin DSL + version catalog, dependency/version policy, package
  naming).

## Capabilities

### New Capabilities

- `app-launch`: the app launches on a compatible device (Android 11+/API 30+),
  renders the placeholder screen without crashing, and carries the real
  `org.mateuszmidor.shoplist` application identity.

### Modified Capabilities

- None (no existing specs).

## Impact

- **Code**: `app/src/main/java` and test source sets move package
  (`com.example.shoplist` -> `org.mateuszmidor.shoplist`); `MainActivity.kt` is
  edited; new files `ShopListApp.kt`, `di/AppContainer.kt`, and the empty `App()`
  composable.
- **Build config**: `namespace` and `applicationId` in `app/build.gradle.kts`.
  No dependency changes in this change (Room/KSP are deferred to 01b).
- **Manifest**: add `android:name=".ShopListApp"` on `<application>`.
- **Out of scope**: any Room/data-layer work, the `compose`-featured UI beyond the
  placeholder, KSP/Room/Navigation dependencies. These are handled by later
  changes (01b+).
