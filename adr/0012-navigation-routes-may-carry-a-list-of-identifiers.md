# ADR-0012: Navigation routes may carry a list of identifiers

- Status: accepted
- Date: 2026-09-01

## Context and Problem Statement

ADR-0007 commits the app to Navigation Compose with type-safe
`@Serializable` routes and a single single-valued `UUID` route argument
mechanism (`ListId` + `ListIdNavType`). The combined view (change 08) is the
first screen that needs to carry *several* list identifiers from the lists
screen to a destination: the user selects a group of lists and the combined
view must know which lists it merges. Navigation Compose has no natural
`NavType` for `List<UUID>` through the existing single-UUID mechanism, so the
route layer must decide how multi-identifier routes are encoded.

## Considered Options

- Comma-join the canonical UUID strings into a single route argument and
  reconstruct the list in the destination, reusing the existing `ListId`
  serializer per element. Implemented as a single custom `ListIdListNavType`
  that encodes the list as comma-joined strings (so the route argument remains
  one joined value, not a native collection argument).
- Add a custom `NavType<List<UUID>>` that serializes as a native collection
  argument (repeated/multi-value encoding) rather than a joined string. This is
  the path Navigation itself uses for primitive lists; rejected here because the
  joined-string form keeps the route a single opaque argument, matching how the
  existing `ListIdNavType` encodes.
- Carry a single opaque session token that a transient session store resolves
  to the selected list ids.

## Decision Outcome

Chosen option: "comma-join the canonical UUID strings into a single route argument",
because it keeps routes type-safe and compile-checked under ADR-0007, reuses the
existing `ListId` serializer (route arguments remain lists of the same domain
`UUID` type), adds only a small per-argument NavType that doubles as the
string-join helper, and avoids a transient session store that would be a second
source of truth. Content of the route stays fully inline, so the transient
combined view is reconstructed from the route alone and needs no extra state.

### Consequences

- Good, because multi-identifier routes stay type-safe with minimal new
  machinery (a single `ListIdListNavType` alongside the existing
  `ListIdNavType`), the domain identifier type remains `UUID` everywhere, and
  configuration changes are safe since the full selection lives in the route.
- Bad, because a route argument now carries the list as an encoded (comma-joined)
  string rather than a native typed collection argument: the `Combined` route's
  `List<ListId>` field depends on `ListIdListNavType` being registered in the
  destination `typeMap`, and UUIDs-as-strings are mandated to be canonical
  (never contain the separator).
- Follow-up: the combined view route (`Combined(listIds)`) is the first
  consumer; if the app later gains other multi-id routes, this convention is
  the baseline for those too.