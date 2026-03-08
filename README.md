# ⚡ Clojkstra

> **Repo:** https://github.com/ArikRahman/Clojkstra

A ClojureScript + re-frame starter template designed for cloning.  
Build new SPAs by cloning this repo, renaming the namespace, and deleting the demo pages.

[![ClojureScript](https://img.shields.io/badge/ClojureScript-1.11-blue)](https://clojurescript.org)
[![re-frame](https://img.shields.io/badge/re-frame-1.4-purple)](https://github.com/day8/re-frame)
[![shadow-cljs](https://img.shields.io/badge/shadow--cljs-2.28-orange)](https://shadow-cljs.org)
[![Bun](https://img.shields.io/badge/bun-1.1-pink)](https://bun.sh)
[![GitHub Pages](https://img.shields.io/badge/GitHub%20Pages-live-brightgreen)](https://arikrahman.github.io/Clojkstra)

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Dev Commands](#dev-commands)
- [Architecture](#architecture)
  - [Data Flow](#data-flow)
  - [File Map](#file-map)
  - [Framework vs. Demo Files](#framework-vs-demo-files)
- [Routing](#routing)
- [Feature Flags](#feature-flags)
- [GitHub Pages Deployment](#github-pages-deployment)
- [How to Turn Clojkstra into a New App](#how-to-turn-clojkstra-into-a-new-app)
- [Extension Points](#extension-points)
- [Linting and Formatting](#linting-and-formatting)
- [Nix Dev Shell](#nix-dev-shell)

---

## Overview

Clojkstra is not a one-off app. It is a **reusable starter template, engine, and vehicle for future projects**. Every architectural decision is documented and every file is clearly labelled as either a _framework_ file (keep it) or a _demo_ file (safe to delete and replace).

The included SPA demonstrates:

- Hash-based client-side routing with bidi + pushy (works on GitHub Pages with no server config)
- re-frame event dispatch and subscriptions wired end-to-end
- A reusable Reagent UI component library
- Global notification toasts, loading overlays, and error banners driven by app-db
- Feature flags, config tokens, and a runtime-updatable theme surface
- A `reg-event-fx` effect handler library (navigate, set-title, localStorage, log, setTimeout)
- Local ratom state patterns alongside app-db patterns

---

## Tech Stack

| Package | Role | Version |
|---|---|---|
| ClojureScript | Language | 1.11 |
| re-frame | State management + event bus | 1.4 |
| Reagent | React wrapper | 1.2 |
| shadow-cljs | Build tool | 2.28 |
| bidi | Bidirectional route matching | 2.1 |
| pushy | HTML5 history listener | 0.3 |
| re-frisk | Dev inspector (dev only) | 1.6 |
| Bun | JS runtime + package manager | 1.1 |
| Tailwind CSS | Utility CSS (CDN in dev) | 3.x |

All dependencies are FOSS-licensed. `npm` and `node` are never used — everything goes through `bun` / `bunx`.

---

## Project Structure

```
Clojkstra/
├── src/
│   └── clojkstra/
│       └── app/
│           ├── core.cljs            # Entry point — init, hot-reload, mount
│           ├── db.cljs              # App-db schema and default state
│           ├── events.cljs          # All re-frame event handlers
│           ├── subs.cljs            # All re-frame subscriptions
│           ├── views.cljs           # App shell, layout, page dispatch
│           ├── routes.cljs          # bidi route table + pushy wiring
│           ├── effects.cljs         # Custom re-frame effect handlers
│           ├── utils.cljs           # Pure utility functions
│           ├── components/
│           │   └── ui.cljs          # Reusable Reagent UI component library
│           └── pages/
│               ├── home.cljs        # Home page (demo)
│               ├── about.cljs       # About page (demo)
│               └── example.cljs     # Example page template (demo)
├── docs/
│   ├── index.html                   # GitHub Pages entry point
│   ├── 404.html                     # SPA redirect for deep links
│   └── cljs-out/                    # Compiled JS (git-ignored, generated)
├── shadow-cljs.edn                  # shadow-cljs build config
├── deps.edn                         # Clojure/ClojureScript dependency manifest
├── package.json                     # Bun scripts + JS devDependencies
├── flake.nix                        # Nix devShell
├── .envrc                           # direnv: use flake
├── .clj-kondo/config.edn            # Linter config
└── .cljfmt.edn                      # Formatter config
```

---

## Getting Started

### Prerequisites

**Option A — Nix (recommended)**

```sh
nix develop     # or: direnv allow  (if you have direnv installed)
```

This drops you into a shell with Java, Clojure CLI, Bun, clj-kondo, and cljfmt pre-installed.  No global installs needed.

**Option B — manual**

Install the following globally:

- [JDK 21+](https://adoptium.net)
- [Clojure CLI tools](https://clojure.org/guides/install_clojure)
- [Bun](https://bun.sh) (`curl -fsSL https://bun.sh/install | bash`)

Then install JS dependencies:

```sh
bun install
```

---

### Dev Commands

| Command | What it does |
|---|---|
| `bun run dev` | Start shadow-cljs watch + dev HTTP server on [http://localhost:8080](http://localhost:8080) |
| `bun run release` | Production build → `docs/cljs-out/` (dead-code eliminated, minified) |
| `bun run clean` | Remove `docs/cljs-out/` and `.shadow-cljs/` cache |
| `bun run report` | Generate a build size report → `report.html` |
| `clj-kondo --lint src/` | Lint all ClojureScript sources |
| `cljfmt check src/` | Check formatting without modifying files |
| `cljfmt fix src/` | Auto-fix formatting in place |
| `nix fmt` | Format `flake.nix` with nixpkgs-fmt |

The dev server serves from `docs/` and hot-reloads on every save via shadow-cljs.  
The re-frisk panel opens automatically in the browser for app-db inspection.

---

## Architecture

### Data Flow

```
User interaction
     │
     ▼
(rf/dispatch [::events/some-event payload])
     │
     ▼
reg-event-db / reg-event-fx   ← reads :db, returns new :db + optional :effects
     │
     ▼
app-db  (single immutable map — the only source of truth)
     │
     ▼
reg-sub  (layer 2: extract raw slice → layer 3: derive/transform)
     │
     ▼
@(rf/subscribe [::subs/some-value])
     │
     ▼
Reagent component re-renders  →  DOM update
```

Effects (HTTP, routing, localStorage, etc.) are handled by registered `reg-fx` handlers in `effects.cljs` — they are never called directly from view components.

### File Map

| File | Kind | Purpose |
|---|---|---|
| `core.cljs` | framework | Entry point — init, hot-reload, mount |
| `db.cljs` | framework | App-db schema and default state |
| `events.cljs` | framework | All re-frame event handlers |
| `subs.cljs` | framework | All re-frame subscriptions |
| `routes.cljs` | framework | bidi route table + pushy wiring |
| `effects.cljs` | framework | Custom re-frame effect handlers |
| `utils.cljs` | framework | Pure utility functions |
| `views.cljs` | framework | App shell, layout, and page dispatch |
| `components/ui.cljs` | framework | Reusable Reagent UI component library |
| `pages/home.cljs` | **demo** | Home page — counter + notification demo |
| `pages/about.cljs` | **demo** | About page — stack info, file map, data flow |
| `pages/example.cljs` | **demo** | Example page template for new features |

### Framework vs. Demo Files

**Framework files** form the reusable base architecture. They contain extension points and should be kept (and modified) when building a new app.

**Demo files** are example content that demonstrate how the framework is used. They are safe to delete entirely — removing them will not break the base architecture.

---

## Routing

Clojkstra uses **hash-based routing** (`/#/about`, `/#/example`) so the app works on GitHub Pages without any server-side URL rewriting.

Routes are defined as a pure data structure in `routes.cljs`:

```clojure
(def app-routes
  ["/" {""        :home
        "about"   :about
        "example" :example
        true      :not-found}])
```

To add a new route:

1. Add an entry to `app-routes` in `routes.cljs`
2. Create `src/clojkstra/app/pages/my_page.cljs` with a `(defn page [] ...)` component
3. Require it in `views.cljs` and add a `case` branch in `page-for-route`
4. Add a `{:handler :my-page :label "My Page"}` entry to `nav-links` in `views.cljs`

Navigate programmatically from anywhere:

```clojure
(routes/navigate! :about)
;; or from an event handler via the :navigate effect:
{:navigate {:handler :about}}
```

---

## Feature Flags

Feature flags live in `db.cljs` under `:config :features`:

```clojure
:features
{:example-feature true
 :debug-panel     true}
```

Check a flag in a view via subscription:

```clojure
@(rf/subscribe [::subs/feature-enabled? :my-flag])
```

Toggle a flag at runtime from an event:

```clojure
(rf/dispatch [::events/toggle-feature :my-flag])
```

Set a flag programmatically:

```clojure
(rf/dispatch [::events/set-config [:features :my-flag] false])
```

---

## GitHub Pages Deployment

The app is built directly into `docs/` so GitHub Pages can serve it from that directory.

**One-time setup:**

1. The repo is already at https://github.com/ArikRahman/Clojkstra
2. Go to **Settings → Pages** on that repo
3. Set **Source** to `Deploy from a branch`, branch `main`, folder `/docs`
4. Click **Save**

**Deploy a new release:**

```sh
bun run release
git add docs/
git commit -m "release: <version>"
git push
```

GitHub Pages will serve the updated build within a minute.

The `docs/404.html` handles the rare case where someone hits a non-root URL directly — it redirects them to `index.html` so the SPA router can take over.

---

## How to Turn Clojkstra into a New App

This is the core workflow the template is designed for.

### Step 1 — Clone and rename

```sh
git clone https://github.com/ArikRahman/Clojkstra my-new-app
cd my-new-app
```

### Step 2 — Replace the namespace root

Rename `src/clojkstra/` to `src/my_app/` (underscores in paths, hyphens in ns names):

```sh
mv src/clojkstra src/my_app
```

Do a project-wide find-and-replace:

```
clojkstra.app  →  my-app.app
clojkstra/app  →  my_app/app
```

Most editors support this with a single multi-file search-and-replace.

### Step 3 — Update config

In `db.cljs`, set your app's name and version:

```clojure
:config
{:app-name "My New App"
 :version  "0.1.0"
 ...}
```

In `shadow-cljs.edn`, update `:init-fn`:

```clojure
:init-fn my-app.app.core/init
:after-load my-app.app.core/on-reload
```

In `package.json`, update the `"name"` field.

### Step 4 — Delete demo files

Remove the three demo pages:

```sh
rm src/my_app/app/pages/home.cljs
rm src/my_app/app/pages/about.cljs
rm src/my_app/app/pages/example.cljs
```

In `views.cljs`, remove the requires for those pages and add your own.  
In `events.cljs` and `subs.cljs`, delete all sections marked `[DEMO]`.  
In `db.cljs`, remove the `:counter` and `:notifications` keys.

### Step 5 — Seed your domain state

Add your app's initial data structure to `db.cljs`:

```clojure
(def default-db
  {:current-route {:handler :home :route-params {}}
   :loading?       false
   :error          nil
   :config         { ... }
   ;; Your domain state:
   :current-user   nil
   :my-feature     {:items [] :loading? false}})
```

### Step 6 — Add your first real page

Copy `pages/example.cljs` as a starting scaffold:

```sh
cp src/my_app/app/pages/example.cljs src/my_app/app/pages/my_feature.cljs
```

Update the namespace, follow the checklist at the top of the file, and add the route.

### Step 7 — Wire API clients and auth (optional)

Add custom effect handlers in `effects.cljs` for HTTP, WebSocket, or analytics.  
Add `:auth` and `:api` keys to `db.cljs`.  
Gate in-progress work behind feature flags.

---

## Extension Points

Each file has a clearly marked `Extension point` comment block.  
Here is a quick reference:

| Where | What to add |
|---|---|
| `db.cljs` | New top-level keys for domain state; new feature flags; theme tokens |
| `events.cljs` | Domain event handlers; `reg-event-fx` for side-effectful events |
| `subs.cljs` | Domain subscriptions; derived/computed layer-3 subs |
| `routes.cljs` | New route entries in `app-routes` |
| `effects.cljs` | HTTP, WebSocket, analytics, native API effect handlers |
| `utils.cljs` | Pure helper functions with no re-frame dependencies |
| `components/ui.cljs` | New reusable Reagent components; or split into sub-namespaces |
| `views.cljs` | New page requires + `case` in `page-for-route`; new nav links |
| `pages/` | One file per top-level page/feature |

---

## Linting and Formatting

**Lint:**

```sh
clj-kondo --lint src/
```

Configuration is in `.clj-kondo/config.edn`. re-frame registration macros are taught to clj-kondo so it understands `reg-event-db` / `reg-sub` / `reg-fx` without false positives.

**Format (check only):**

```sh
cljfmt check src/
```

**Format (auto-fix):**

```sh
cljfmt fix src/
```

Configuration is in `.cljfmt.edn`. Namespace `:require` blocks are intentionally not auto-sorted — manual grouping with framework/local comments is preferred.

---

## Nix Dev Shell

`flake.nix` provides a fully reproducible development environment:

```sh
nix develop
```

Or with [direnv](https://direnv.net):

```sh
echo "use flake" > .envrc
direnv allow
```

The shell includes: `jdk21`, `clojure`, `bun`, `git`, `curl`, `jq`, `clj-kondo`, `cljfmt`.

On entry, `bun install` runs automatically if `node_modules/` is missing.

Format `flake.nix` itself with:

```sh
nix fmt
```

---

## License

MIT — do whatever you want with it.  
The whole point is that you clone it and make it yours.