## 1. Add the lookup dialog

- [x] 1.1 Add a full-screen `AddItemDialog` composable in `app/src/main/java/org/mateuszmidor/shoplist/ui/items/ItemsScreen.kt` with an auto-focused name field, a weighted `LazyColumn` for results, and Cancel/Add actions
- [x] 1.2 Add private helpers in `ui/items` that filter the current list's bought items by trimmed, case-insensitive substring and detect a trimmed, case-insensitive exact-name duplicate across all items in the current list

## 2. Wire the dialog into the items screen

- [x] 2.1 Replace the "New item" `NameDialog` call site with `AddItemDialog`, holding the query state in `ItemsScreen` and deriving results from `uiState.items`
- [x] 2.2 Wire tapping a result to the existing `onToggleBought` callback without closing the dialog, and leave Cancel, blank-name blocking, and the create action otherwise unchanged
- [x] 2.3 Disable "Add" on an exact match and show the "Already on this list" hint

## 3. Verification

- [x] 3.1 Build the app (`./gradlew assembleDebug`) and confirm it compiles
- [x] 3.2 Install and launch the debug build on the Samsung Galaxy A52
- [x] 3.3 Verify the dialog opens with the name field focused, keyboard shown, all and only bought items of the current list displayed, and Add/Cancel reachable
- [x] 3.4 Verify typing filters bought items case-insensitively by substring, and a near-match such as "Eggplant" for "egg" is shown while Add stays enabled
- [x] 3.5 Verify an exact match (bought or unbought, ignoring case and surrounding spaces) disables Add, shows the hint, and creates no item
- [x] 3.6 Verify tapping a shown match un-marks it, keeps the dialog open, and removes it from the results
- [x] 3.7 Verify cancel and blank/whitespace-only names still create nothing, and the rename, new-list, and import dialogs are unchanged
- [x] 3.8 Run the existing JVM unit tests (`./gradlew testDebugUnitTest`) and instrumented tests (`./gradlew connectedDebugAndroidTest`)
- [x] 3.9 Run `openspec validate newitem-lookup --type change --strict` before archive

## 4. Bookkeeping

- [x] 4.1 Add Change 10 to `TASKS.md` and mark it complete after verification
