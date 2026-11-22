## 1. Fold logic and tests

- [x] 1.1 Add failing JVM tests in `ItemLookupTest` for plain-to-diacritic and diacritic-to-plain substring matching covering `ą ć ę ł ń ó ś ź ż` (lower and upper case) and decomposed input
- [x] 1.2 Add failing JVM tests proving a diacritic-only exact variant (`nic` vs `nić`) disables Add via `isExactNameMatch` and `canAddItem`
- [x] 1.3 Implement a shared fold helper in `ItemLookup.kt` (`lowercase(Locale.ROOT)`, NFD, strip combining marks, explicit `ł→l`) and use it in `matchingBoughtItems`, `isExactNameMatch`, and `canAddItem`

## 2. Verification

- [x] 2.1 Build the app (`./gradlew assembleDebug`) and confirm it compiles
- [x] 2.2 Verify on the Samsung Galaxy A52: `nic` finds `nić dentystyczna`, `losos` finds `łosoś`, `łosoś` finds a plain `losos`, an exact `nić` disables Add with the hint, and stored/displayed names keep their diacritics
- [x] 2.3 Run the existing JVM unit tests (`./gradlew testDebugUnitTest`) and instrumented tests (`./gradlew connectedDebugAndroidTest`)
- [x] 2.4 Run `openspec validate ignore-diacritics-in-lookup --type change --strict` before archive

## 3. Bookkeeping

- [x] 3.1 Add Change 11 to `TASKS.md` and mark it complete after verification
