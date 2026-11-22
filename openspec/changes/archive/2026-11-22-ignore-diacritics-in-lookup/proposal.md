## Why

The new-item lookup from Change 10 matches names case-insensitively but not accent-insensitively, so a user who types a Polish name without diacritics ("nic", "losos") does not see the existing item ("nić dentystyczna", "łosoś"). This makes the lookup miss exactly the everyday case it was built to catch, and lets a diacritic-only variant slip through duplicate protection.

## What Changes

- Fold both the typed query and each current-list item name before comparing them, treating the Polish diacritic pairs as equivalent: `a/ą`, `c/ć`, `e/ę`, `l/ł`, `n/ń`, `o/ó`, `s/ś`, `z/ź`, `z/ż` (including uppercase forms).
- Keep the existing two rules but apply the fold to both: substring matching for the displayed bought-item results, and trimmed exact matching for duplicate protection ("Add" disable + "Already on this list" hint).
- Keep the fold symmetric: a plain query finds a diacritic name and a diacritic query finds a plain name.
- Apply the fold for matching only. Stored item names, the visible item labels, and all other screens stay unchanged; no data migration.
- Leave the dialog layout, focus behavior, cancel/blank behavior, and all other dialogs unchanged.

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- `items-screen`: The create-item lookup's result filtering and exact-name duplicate check become diacritic-insensitive for the nine Polish letters, in both directions, without changing stored names.

## Impact

- Code: `app/src/main/java/org/mateuszmidor/shoplist/ui/items/ItemLookup.kt` (shared fold helper used by `matchingBoughtItems`, `isExactNameMatch`, and `canAddItem`) and its JVM test `app/src/test/java/org/mateuszmidor/shoplist/ui/items/ItemLookupTest.kt`.
- Uses only JDK/Android `java.text.Normalizer` plus a small explicit `ł→l` mapping; no new dependency, ViewModel, repository, DAO, Room schema, navigation, or data migration.
- Verification: JVM unit tests covering all nine letters (lower and upper case), plus manual device verification on the Samsung Galaxy A52 using plain-text queries such as "nic" and "losos".
