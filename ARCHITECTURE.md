# Architectural Drivers 

Based on https://github.com/mateuszmidor/ArchStudy/tree/master/Drivers

## Project goal

- personal, long-lived application used by a single user (me) on a daily basis - replacement for Listonic shopping list app
- not intended for publication / wide audience
- flagship quality: maintainability and clean architecture matter, because the app is used daily and is a study project meant to teach good Android/Kotlin practices
- no production-grade concerns: no CI, no release process, no scalability, no multi-user support

## Functional requirements

- shopping lists
  - create a new shopping list
  - rename an existing shopping list
  - delete a shopping list
- items within a list
  - add a new item to a list
  - rename an existing item
  - delete an item
  - mark an item as bought (checkmark) and uncheck it
  - ordering: items display in two visual sections - unchecked at top, checked (bought) at bottom; within each section, items are sorted by creation time. No manual reordering. Checking moves an item to the checked section; unchecking restores it to its original position in the unchecked section (based on creation time)
- UI layout: two screens - a "list of lists" screen and a per-list items screen, with navigation (back) between them
- interaction pattern: FAB (+) to add a list / item, tap-and-hold (long-press) context menu for rename/delete
- lists screen shows per-list summary: total item count and bought count
- NOT in scope initially
  - quantities / units per item (only item names)
  - manual reordering - not in scope; items are always ordered by creation time within their section (unchecked/checked)
- persistence
  - lists and items are stored locally in phone memory and survive app restarts
- runtime environment
  - must run on Samsung Galaxy A52

## Non-functional requirements (Quality Attributes)

- maintainability - HIGH; flagship study project, code must be clean and easy to understand for learning
- testability - HIGH; automated tests (unit for business logic, integration for storage) written from day one alongside features
- performance - responsive UI; shopping list interaction must feel instant on Samsung Galaxy A52
- availability - app must not lose data across restarts; persistence is reliable
- security - LOW; no authentication, no accounts, no app lock
- privacy - HIGH; all data stays on the phone in app-private storage, no network access, no data collection; nothing leaves the device
- scalability - not applicable; single user, local data only
- disaster recovery - MEDIUM; rely on Android automatic app-data backup (Google Drive lineage backup) - enabled via manifest/config, no manual export/import in initial scope
- internationalisation / multi-language support - dropped; not needed for a single-user personal app, English only
- accessibility - ignored; personal app, no screen-reader/font-scaling testing
- monitoring - not applicable; no server, no ops team
- management (runtime feature toggles) - not applicable
- audit - LOW; no audit log needed; version control history serves as change tracking
- flexibility - LOW; user-configurable runtime behavior is out of scope
- extensibility - MEDIUM; clean layered structure (UI / business logic / storage, decoupled interfaces) so future features slot in, but no speculative abstraction or frameworks for unplanned features
- legal, regulatory, compliance - not applicable; personal non-published app, no user data of others
- performance details - app launch and basic interactions should be snappy; no heavy data/network, so no explicit latency targets

## Constraints imposed upon us

- technology constraints
  - language: Kotlin (learning goal)
  - UI toolkit: Jetpack Compose (declarative Kotlin UI; Google-recommended modern default) - constraint chosen for learning value
  - storage: local on-device persistence via Room (Android's SQLite-based persistence library) - chosen for relational model, reactive Flow queries and being the standard Android pattern
  - dependency injection: manual DI (hand-wired object graph, e.g., an AppContainer); no DI framework (no Hilt/Dagger) in initial scope
  - navigation: Jetpack Navigation Compose (type-safe routes + back stack)
  - target platform: Samsung Galaxy A52 (Android 11, 2021 mid-range) - must build/run/test on this device
  - open source: no restrictions - exclusively standard, freely available, mature-but-verified Android tech; no experimental/unreleased libraries
  - no existing systems to interoperate with; greenfield app
- people constraints
  - team: single developer (me)
  - experience: 15 years in C/C++ (embedded) and Go/Python (backend); strong software engineering fundamentals
  - Android/Kotlin knowledge: none - complete beginner in mobile, tooling, ecosystem conventions
  - capacity for learning: primary motivation is to learn, so time spent understanding the platform is the point
- organisational constraints
  - none; solo project, no company politics or stakeholders
- budget and time constraints
  - zero budget (everything free/open source); no external deadline - elapsed time is my own commitment
- negotiable constraints
  - duration and pace are fully flexible; the only hard constraint is learning Android/Kotlin by building this app

## Principles adopted

- coding standards and conventions
  - idiomatic Kotlin; official Android/Kotlin style/formatting (ktlint or ktfmt)
  - meaningful names, small single-purpose functions/classes
  - UI strings in resource files; no hardcoded text in code
- automated testing
  - unit tests for business logic and ViewModels (JUnit + coroutines-test)
  - integration tests for the Room data layer (in-memory database)
  - UI tests deferred until the UI stabilizes (avoid early brittleness) - revisit later
- static analysis / tooling
  - Kotlin compiler warnings treated seriously; lint enabled; no dedicated third-party static analysis tooling in initial scope
- architecture principles
  - layering strategy: three layers - UI (Compose + ViewModel + StateFlow, unidirectional data flow) / business logic / data (Room)
  - MVVM-style recommended app architecture (Google)
  - placement of business logic: in ViewModels and dedicated domain logic; Room and UI never call each other directly
  - SOLID; favour small interfaces (e.g., repository/DAO boundaries) 
  - statelessness: ViewModels as per-screen coordination; state flows from storage to UI as reactive Flow streams
  - domain model: simple and pragmatic - entities are List/Item; no rich domain behaviour for this feature set

## Feature prioritization

- if forced to cut scope, the non-negotiable core is: lists CRUD + item CRUD + bought checkmarks, working reliably and durably
- nice-to-haves that can be cut first (in decreasing priority): lists-screen item counts, bought-move-to-end ordering polish (UX/animations), auto-backup config

## Architecture risks to mitigate

- the team lacks experience with the technology (solo Kotlin/Android beginner)
  - mitigation: start with the smallest MVP that teaches the whole stack; follow official Google guidance and templates; leverage senior experience in software engineering fundamentals; iterate in small testable steps
- data loss / corruption
  - mitigation: Room + reliable transactional writes; enable Android auto-backup early in the project, not as an afterthought; keep data model versioned for future migrations
- NOT risks for this project: third-party data formats, external system unavailability, scaling, single points of failure, off-the-shelf product failures, vendor lock-in - single user, no integrations, no network

## Things to think about beyond the code

- cross-cutting concerns
  - logging: keep minimal, use standard Android Logcat with sensible tags; no external logging frameworks
  - exception handling: app must not crash on unexpected states; failed operations show non-fatal errors or fail soft
- consistency of approaches: lists screen and items screen are mirror structures - implement them with the same established pattern (state, ViewModel, DAO shape) rather than two bespoke styles
- structural consistency and integrity: keep layering boundaries respected (UI never touches Room directly); enforce via code review / simplicity, not tooling
- foundations sufficiency: the storage schema and entity model must support the core features cleanly before adding nice-to-haves
- future outlook (keep in mind, NOT in initial scope): quantities/units per item, manual reordering, sharing lists, cloud/companion sync
- evaluation of foundations: before building more features, the MVP must prove the whole stack works end-to-end on the target device