## 1. Build toolchain: Room 3.0 + KSP

- [x] 1.1 Add `ksp` (2.2.10-2.0.2), `room3` (3.0.2) and `kotlinxCoroutinesTest` (1.9.0, matching the compose-BOM-pinned coroutines-core) version refs to `gradle/libs.versions.toml`
- [x] 1.2 Add `room3-runtime`, `room3-compiler`, `kotlinx-coroutines-test` library entries and `ksp` / `androidx-room3` plugin entries to `gradle/libs.versions.toml`
- [x] 1.3 Register `com.google.devtools.ksp` and `androidx.room3` plugins (`apply false`) in the root `build.gradle.kts`
- [x] 1.4 Apply `ksp` and `androidx.room3` plugins in `app/build.gradle.kts` and add `room3 { schemaDirectory("debug", "$projectDir/schemas/debug") }` (+ release mirror so `build` works)
- [x] 1.5 Add dependencies: `androidx-room3-runtime` (implementation), `androidx-room3-compiler` (ksp), `kotlinx-coroutines-test` (androidTestImplementation)
- [x] 1.6 Build smoke: `./gradlew assembleDebug` compiles with the new Room/KSP toolchain

## 2. Lists data layer

- [x] 2.1 Create `data/ShoppingListEntity.kt` (`@Entity` table `shopping_lists`: `id: UUID` `@PrimaryKey`, `name: String`, `createdAt: Long`)
- [x] 2.2 Create `data/Converters.kt` with `UUID` <-> `String` `@ColumnTypeConverter`
- [x] 2.3 Create `data/ShoppingListDao.kt`: `observeAll()` `Flow<List<Entity>>` ordered by `created_at`; `suspend` `insert`, `renameById(id, name)`, `deleteById(id)`
- [x] 2.4 Create `data/ShoppingDatabase.kt` (`@Database` version 1, `@ColumnTypeConverters(Converters::class)`, entities enabled)
- [x] 2.5 Create `data/ShoppingListRepository.kt` interface: `observeLists(): Flow<List<ShoppingListEntity>>`, `create(name): UUID`, `rename(id, name)`, `delete(id)`
- [x] 2.6 Create `data/RoomShoppingListRepository.kt` (per ADR-0006): `create()` builds `ShoppingListEntity(UUID.randomUUID(), name, System.currentTimeMillis())` and returns the id

## 3. DI wiring

- [x] 3.1 Change `di/AppContainer.kt` to `AppContainer(context: Context)` building the `ShoppingDatabase` (`Room.databaseBuilder("shoplist.db")`), DAO, and repository; expose the repository
- [x] 3.2 Update `ShopListApp.kt`: pass `applicationContext` into `AppContainer`

## 4. Tests

- [x] 4.1 Add JVM unit test for the UUID converter round-trip (`test/`)
- [x] 4.2 Add `ShoppingListDaoTest` (androidTest): in-memory database, covers insert/order-by-createdAt/rename/delete and Flow emission (spec scenarios)
- [x] 4.3 Add `RoomShoppingListRepositoryTest` (androidTest): `create()` returns a stored UUID, rename preserves id/timestamp, delete removes, observe ordering, survives database re-open
- [x] 4.4 Run JVM tests: `./gradlew test` passes

## 5. Verification

- [x] 5.1 Full build: `./gradlew build` compiles and JVM tests pass
- [x] 5.2 Instrumented integration tests pass on the A52: `./gradlew connectedDebugAndroidTest`
- [x] 5.3 Verify the exported schema JSON `app/schemas/debug/.../ShoppingDatabase/1.json` exists and is committed
- [x] 5.4 Run `openspec validate data-foundation --type change --strict` with no errors