## 1. Dependency

- [x] 1.1 Add `datascript/datascript` to `deps.edn` under `:deps`.

## 2. Re-frame Integration

- [x] 2.1 In `db.cljs`, add `:datascript/db` initialized with `(datascript.core/empty-db)` to `default-db`.
- [x] 2.2 In `events.cljs`, add a `::transact` event that updates `:datascript/db` via `d/db-with`.
- [x] 2.3 In `subs.cljs`, add a `::datascript-db` layer-2 subscription, and a `::query` layer-3 subscription that accepts a query and variables and returns the result using `d/q`.

## 3. UI and Demo

- [x] 3.1 Create `src/clojkstra/app/pages/datascript.cljs` with a basic UI component for adding facts and showing results.
- [x] 3.2 Update `routes.cljs` to map `"datascript-demo"` to `:datascript-demo`.
- [x] 3.3 Update `views.cljs` to handle `:datascript-demo` route and add it to `nav-links`.
