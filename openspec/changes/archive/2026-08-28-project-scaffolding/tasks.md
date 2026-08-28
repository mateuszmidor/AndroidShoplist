## 1. Rename package to org.mateuszmidor.shoplist

- [x] 1.1 Update `namespace` and `applicationId` in `app/build.gradle.kts` from `com.example.shoplist` to `org.mateuszmidor.shoplist`
- [x] 1.2 Move `app/src/main/java/com/example/shoplist/` sources into `app/src/main/java/org/mateuszmidor/shoplist/`
- [x] 1.3 Move `app/src/test/java/com/example/shoplist/` and `app/src/androidTest/java/com/example/shoplist/` sources into the new package directories
- [x] 1.4 Update `package` declarations and imports in moved Kotlin files (MainActivity.kt, ui/theme/*)
- [x] 1.5 Update `AndroidManifest.xml` references to the new package (`.MainActivity`, theme, and any namespace-dependent attrs)
- [x] 1.6 Verify no `com.example.shoplist` references remain across the repo (grep should return zero hits)

## 2. Composition root: Application + AppContainer

- [x] 2.1 Create `ShopListApp : Application` that owns a single `AppContainer` (in package `org.mateuszmidor.shoplist`)
- [x] 2.2 Create `AppContainer` (empty shell) in `org.mateuszmidor.shoplist.di` ready to hold the Room database/repositories (01b)
- [x] 2.3 Register `ShopListApp` in the manifest via `android:name=".ShopListApp"` on `<application>`

## 3. App entry: empty App() composable

- [x] 3.1 Create `App()` composable as the app entry point, replacing the template `Greeting("Android")` placeholder
- [x] 3.2 Wire `MainActivity` to reach `AppContainer` via `(application as ShopListApp).container` and render `App()` inside `ShopListTheme`
- [x] 3.3 Keep the app's placeholder body visible so the app is verifiable on device

## 4. Repair template smoke tests

- [x] 4.1 Update `ExampleUnitTest.kt` (JVM test) to the new package/imports
- [x] 4.2 Update `ExampleInstrumentedTest.kt` (androidTest) to the new package/imports

## 5. Build, test and launch verification

- [x] 5.1 Run a full build (`./gradlew build`) and confirm it compiles without errors
- [x] 5.2 Run JVM unit tests (`./gradlew test`) and confirm the smoke test passes
- [x] 5.3 Launch the app on the emulator and confirm the placeholder screen renders without crashing
- [x] 5.4 Run `openspec validate project-scaffolding --type change --strict` and confirm no validation errors
