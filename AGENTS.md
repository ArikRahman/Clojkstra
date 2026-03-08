# Clojkstra — Agent Orientation

This file is for AI agents working in this repository.
Read it fully before touching any code.

---

## What this repo is

Clojkstra is a **ClojureScript + re-frame starter template**.
It is not a one-off app — it is a reusable scaffold designed to be cloned and
customised into new projects. Every file is labelled either `[FRAMEWORK]` (keep
it, modify it carefully) or `[DEMO]` (example content, safe to delete).

---

## Language and runtime

- **ClojureScript** — all source files are `.cljs` under `src/clojkstra/app/`
- **shadow-cljs** — the build tool; config is `shadow-cljs.edn`
- **Bun** — the JS runtime and package manager. Never use `npm` or `node`.
  Always use `bun` or `bunx`.
- **Clojure CLI** (`clojure` / `clj`) — used by shadow-cljs for classpath
  resolution from `deps.edn`
- **re-frame** — all state lives in a single `app-db` map; mutations happen
  only through registered event handlers; views read only through subscriptions

---

## Repo layout

```
src/clojkstra/app/
  core.cljs           entry point — init, hot-reload, mount       [FRAMEWORK]
  db.cljs             app-db schema and default state             [FRAMEWORK]
  events.cljs         all re-frame event handlers                 [FRAMEWORK]
  subs.cljs           all re-frame subscriptions                  [FRAMEWORK]
  routes.cljs         bidi route table + pushy hash-routing       [FRAMEWORK]
  effects.cljs        custom re-frame effect handlers             [FRAMEWORK]
  utils.cljs          pure utility functions                      [FRAMEWORK]
  views.cljs          app shell, layout, page dispatch            [FRAMEWORK]
  components/
    ui.cljs           reusable Reagent UI component library       [FRAMEWORK]
  pages/
    home.cljs         home page — counter + notification demo     [DEMO]
    about.cljs        about page — stack info, file map           [DEMO]
    example.cljs      example page template for new features      [DEMO]

docs/
  index.html          GitHub Pages entry point
  404.html            SPA redirect safety net
  cljs-out/           compiled JS output (git-ignored, generated)

shadow-cljs.edn       build config
deps.edn              Clojure/CLJS dependency manifest
package.json          bun scripts + JS devDependencies
justfile              all developer commands (use just <recipe>)
flake.nix             Nix devShell
.clj-kondo/config.edn linter config
.cljfmt.edn           formatter config
```

---

## Commands

Always use `just`. Never construct raw `bun`, `clj-kondo`, or `cljfmt` invocations
unless `just` has no recipe for what you need.

| Recipe | What it does |
|---|---|
| `just dev` | Start shadow-cljs watch + dev server on http://localhost:8080 |
| `just build` | Production release build → `docs/cljs-out/` |
| `just clean` | Remove `docs/cljs-out/` and `.shadow-cljs/` cache |
| `just rebuild` | Clean then build |
| `just lint` | `clj-kondo --lint src/` |
| `just fmt-check` | `cljfmt check src/` |
| `just fmt` | `cljfmt fix src/` |
| `just check` | lint + fmt-check |
| `just ci` | check + build |
| `just report` | Build size report → `report.html` |
| `just loc` | Count lines of ClojureScript source |
| `just files` | List all `.cljs` source files |
| `just st` | `jj status` |
| `just log` | `jj log --limit 10` |
| `just diff` | `jj diff` |
| `just fdiff <file>` | Diff a specific file |
| `just describe "msg"` | Set working copy description |
| `just commit "msg"` | Commit + advance `main` bookmark |
| `just snap "msg"` | Commit + advance bookmark + push |
| `just ship "msg"` | ci + commit + advance bookmark + push |
| `just deploy "msg"` | build + commit + advance bookmark + push |
| `just abandon` | Abandon current empty working copy change |

---

## Version control — jujutsu (jj)

This repo uses **jujutsu** (`jj`), not plain git. Key differences:

- The **working copy is always a change**. You never need `git add`. Every file
  edit is automatically part of `@` (the current change).
- Use `jj describe -m "msg"` to set the commit message of the current change
  without finalising it.
- Use `jj commit -m "msg"` to finalise `@` and open a new empty child change.
- Use `jj status` / `jj diff` instead of `git status` / `git diff`.
- Never run `git commit`, `git add`, or `git checkout` directly.
- The primary bookmark is `main`. After committing, advance it with:
  `jj bookmark set main --revision @-`
- All of this is wrapped in `just` recipes — prefer `just commit`, `just snap`,
  `just ship` over raw `jj` calls.

---

## re-frame conventions

Follow these rules strictly — they are the architecture of this project:

1. **No side effects in views.** Views call `rf/dispatch` and `@(rf/subscribe …)`
   only. They never call `js/fetch`, mutate atoms outside Reagent, or touch
   `js/localStorage` directly.

2. **No direct db reads in views.** Views read app-db exclusively through
   named subscriptions in `subs.cljs`. Never pass `db` into a component.

3. **Events mutate db; effects reach outside.** `reg-event-db` for pure db
   updates. `reg-event-fx` when a side effect is also needed. Side effects go
   through registered `reg-fx` handlers in `effects.cljs`.

4. **Layer-2 subscriptions extract; layer-3 subscriptions derive.**
   A layer-2 sub reads a raw slice of `db`. A layer-3 sub uses `:<-` to depend
   on layer-2 subs and computes a derived value. Never skip layers.

5. **All new state goes in `db.cljs` first.** Add every new top-level key to
   `default-db` with a comment explaining its purpose before writing events
   or subs that reference it.

6. **Keyword namespacing.** Event and subscription keywords use `::` in their
   own namespace (`::initialize-db` in `events.cljs`) and alias-qualified
   `::events/initialize-db` from other namespaces. Never use plain unnamespaced
   keywords for re-frame registrations.

7. **`standard-interceptors`** must be attached to every `reg-event-db` /
   `reg-event-fx` call. See `events.cljs`.

---

## Namespace conventions

- Root: `clojkstra.app`
- File path mirrors namespace: `src/clojkstra/app/pages/home.cljs`
  → `(ns clojkstra.app.pages.home …)`
- Hyphens in namespace names, underscores in file/directory names.
- Aliases used project-wide:

  | Namespace | Alias |
  |---|---|
  | `re-frame.core` | `rf` |
  | `reagent.core` | `r` |
  | `reagent.dom` | `rdom` |
  | `clojkstra.app.events` | `events` |
  | `clojkstra.app.subs` | `subs` |
  | `clojkstra.app.routes` | `routes` |
  | `clojkstra.app.components.ui` | `ui` |

---

## Adding a new page

1. Create `src/clojkstra/app/pages/my_page.cljs` with `(defn page [] …)`
2. Add a route in `routes.cljs` → `app-routes`: `"my-page" :my-page`
3. Require the page in `views.cljs`; add a `case` branch in `page-for-route`
4. Add `{:handler :my-page :label "My Page"}` to `nav-links` in `views.cljs`
5. Seed any new state in `db.cljs` under a clearly named top-level key
6. Add events in `events.cljs`, subscriptions in `subs.cljs`

---

## Adding a new UI component

Add it to `src/clojkstra/app/components/ui.cljs`.

Rules:
- Pure function of props — no `rf/subscribe` calls inside `ui.cljs`
- Accept an optional `:class` prop on every component for caller overrides
- Document the prop signature in the docstring
- If the file grows large, split into `components/forms.cljs`,
  `components/data_display.cljs`, etc. and re-export from `ui.cljs`

---

## Adding a new effect handler

Add it to `src/clojkstra/app/effects.cljs` using `rf/reg-fx`.

Rules:
- The handler body must be a pure side-effect function — no `db` reads
- Document the usage shape in the docstring
- Call it from `reg-event-fx` via the effects map, never directly from a view

---

## Feature flags

Feature flags live in `db.cljs` under `:config :features`.
Gate unfinished work behind a flag. Check in a view with:

```clojure
@(rf/subscribe [::subs/feature-enabled? :my-flag])
```

Toggle at runtime:

```clojure
(rf/dispatch [::events/toggle-feature :my-flag])
```

---

## What NOT to do

- Do not run `npm`, `node`, `yarn`, or `npx`. Use `bun` / `bunx` only.
- Do not run `git commit`, `git add`, `git checkout`. Use `jj` / `just`.
- Do not edit files in `docs/cljs-out/` — they are generated.
- Do not put business logic in `views.cljs` or any `pages/` file. Views
  dispatch and subscribe; logic lives in events, subs, and effects.
- Do not add new top-level deps to `deps.edn` without checking that they are
  FOSS-licensed and have a ClojureScript-compatible build.
- Do not break the `[FRAMEWORK]` files to fix a `[DEMO]` problem. Delete the
  demo, don't patch around it.

---

## Before you commit

Run:

```sh
just ci
```

This runs `clj-kondo --lint src/`, `cljfmt check src/`, and a full release
build. All three must pass cleanly. Fix any lint or format errors before
committing. Use `just fmt` to auto-fix formatting.