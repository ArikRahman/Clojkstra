## Why

The project needs an integration with DataScript, an immutable in-memory database and Datalog query engine. DataScript brings expressive query capabilities (Datalog) and relational modeling to ClojureScript frontends, which can simplify complex state management compared to plain nested maps. We will add DataScript to the project and provide a basic demo.

## What Changes

- Add `datascript/datascript` to `deps.edn`.
- Initialize a simple DataScript database in `db.cljs`.
- Add basic re-frame events to transact against DataScript.
- Add a basic re-frame subscription to query DataScript using Datalog.
- Add a new page `datascript-demo` to demonstrate querying and transacting with DataScript.

## Capabilities

### New Capabilities
- `datascript-integration`: Integration of DataScript into the application's re-frame state and provide a demonstration UI.

### Modified Capabilities
- (None)

## Impact

- `deps.edn` will have a new dependency.
- `src/clojkstra/app/db.cljs`, `events.cljs`, `subs.cljs` will be modified.
- A new page `src/clojkstra/app/pages/datascript.cljs` will be created.
- `src/clojkstra/app/routes.cljs` and `views.cljs` will be updated to include the new page.
