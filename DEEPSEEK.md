# Clojkstra – DeepSeek Project Overview

> ClojureScript + re-frame starter template for web and desktop apps, with Tauri integration.

## Introduction

Clojkstra is a **reusable scaffold** designed to be cloned and customised into new projects. It provides:

- A production‑ready re‑frame architecture (single `app-db`, events, subscriptions, effects).
- Client‑side hash routing (no external deps).
- A library of stateless Reagent UI components.
- Desktop app packaging with Tauri v2 (Rust backend).
- Build tooling with `shadow-cljs`, dev server on `http://localhost:8080`.
- Developer experience: `just` recipes for all common tasks, `clj‑kondo` linting, `cljfmt` formatting.
- Version control with **Jujutsu (`jj`)** – working copy is always a change.
- OpenSpec workflow for spec‑driven feature development.

---

## Core Technologies

| Technology               | Version / Role                                 |
|--------------------------|------------------------------------------------|
| **ClojureScript**        | 1.10.x (via `deps.edn`)                        |
| **re‑frame**             | 1.4.3 – state management                      |
| **Reagent**              | 2.0.1 – React wrapper                         |
| **shadow‑cljs**          | 3.3.7 – build tool                            |
| **Bun**                  | JS runtime & package manager (never `npm`)    |
| **Tauri**                | v2 – desktop host process (Rust)              |
| **DataScript**           | 1.5.4 – optional in‑memory Datalog DB         |
| **Tailwind CSS**         | via CDN (CSP configured)                      |

---

## Project Layout

```text
src/clojkstra/app/
  core.cljs           – entry, init, hot‑reload       [FRAMEWORK]
  db.cljs             – app‑db schema & default state [FRAMEWORK]
  events.cljs         – all re‑frame event handlers   [FRAMEWORK]
  subs.cljs           – all re‑frame subscriptions    [FRAMEWORK]
  routes.cljs         – hash router                   [FRAMEWORK]
  effects.cljs        – custom effect handlers        [FRAMEWORK]
  theme.cljs          – CSS class constants           [FRAMEWORK]
  utils.cljs          – pure utility functions        [FRAMEWORK]
  views.cljs          – app shell, page dispatch      [FRAMEWORK]
  components/
    ui.cljs           – reusable Reagent components   [FRAMEWORK]
  pages/
    home.cljs         – demo: counter + notify        [DEMO]
    about.cljs        – demo: stack info, file map    [DEMO]
    example.cljs      – template for new features     [DEMO]

docs/
  index.html           – GitHub Pages entry
  404.html             – SPA redirect
  cljs-out/            – compiled JS (git‑ignored)

src-tauri/              – Tauri v2 host process       [FRAMEWORK]
  src/lib.rs            – Tauri app setup & commands
  src/main.rs           – thin wrapper over lib.rs
  capabilities/         – permission capability files
  icons/                – app icon assets
  tauri.conf.json       – window, CSP, bundle config
  Cargo.toml            – Rust manifest

openspec/               – spec‑driven change management
  specs/                – source of truth (system behaviour)
  changes/              – active proposals

shadow-cljs.edn
deps.edn
package.json
justfile
flake.nix               – Nix devShell (Rust + Tauri deps)
```

---

## Development Commands (via `just`)

Always use `just` – never invoke `bun`, `shadow‑cljs`, `clj‑kondo`, or `cargo‑tauri` directly unless no recipe exists.

| Recipe                | Purpose                                                                 |
|-----------------------|-------------------------------------------------------------------------|
| `just dev`            | Start shadow‑cljs watch + dev server @ `http://localhost:8080`          |
| `just build`          | Production release build → `docs/cljs-out/main.js`                      |
| `just clean`          | Remove `docs/cljs-out/` and `.shadow-cljs/` cache                       |
| `just rebuild`        | `clean` then `build`                                                    |
| `just lint`           | `clj‑kondo --lint src/`                                                 |
| `just fmt-check`      | `cljfmt check src/`                                                     |
| `just fmt`            | `cljfmt fix src/`                                                       |
| `just check`          | `lint` + `fmt-check`                                                    |
| `just ci`             | `check` + `build`                                                       |
| `just report`         | Build size report → `report.html`                                       |
| `just tauri-dev`      | Launch Tauri desktop app in dev mode (launches shadow‑cljs automatically) |
| `just tauri-build`    | Build Tauri release + installers                                        |
| `just tauri-info`     | Show Tauri/Rust/OS diagnostics                                          |
| `just version x.y.z`  | Update version across `package.json`, `tauri.conf.json`, `Cargo.toml`, `db.cljs` |

### Version control (`jj`) shortcuts

| Recipe               | Equivalent `jj` action                                  |
|----------------------|---------------------------------------------------------|
| `just st`            | `jj status`                                             |
| `just log`           | `jj log --limit 10`                                     |
| `just diff`          | `jj diff`                                               |
| `just fdiff <file>`  | `jj diff -- <file>`                                     |
| `just describe "msg"`| `jj describe -m "msg"`                                  |
| `just commit "msg"`  | `jj commit -m "msg"` + advance `main` bookmark          |
| `just snap "msg"`    | `jj git fetch`, rebase, commit, advance, push           |
| `just ship "msg"`    | `ci` + `snap`                                           |
| `just deploy "msg"`  | `build` + `snap` (pushes `docs/` for GitHub Pages)      |

---

## Architecture

### re‑frame State Management

- **Single atom** `app-db` defined in `db.cljs`.
- **Events** (`events.cljs`) – pure transformations or effects.
  - Every event must use `standard-interceptors` (see `events.cljs`).
  - Use `::events/...` (namespaced keywords).
- **Subscriptions** (`subs.cljs`) – layer‑2 (raw slice) and layer‑3 (derived).
- **Effects** (`effects.cljs`) – side effects registered via `rf/reg-fx`.  
  Built‑in effects: `:navigate`, `:set-title`, `:local-storage`, `:log`, `:set-timeout`.  
  *Tauri effect* pattern described (not yet implemented):
  ```clojure
  (rf/reg-fx ::invoke-tauri (fn [{:keys [command args on-success on-error]}] ...))
  ```

### Routing (hash‑based)

- `routes.cljs` maps path strings (e.g. `"/about"`) to handler keywords (`:about`).
- `navigate!` changes the URL hash and triggers a re‑frame dispatch.
- `start!` attaches `hashchange` listener.

### UI Components

- `components/ui.cljs` – pure, stateless Reagent components.
- Each component accepts an optional `:class` prop and follows the same styling API.
- Theme classes are centralised in `theme.cljs` (Tailwind utility classes).

### Tauri Integration

- Desktop app lives in `src-tauri/` (Rust).
- Frontend communicates **only via Tauri commands**.
- **Rule:** Never call `js/window.__TAURI__` directly. Use a registered effect handler in `effects.cljs`.
- The CSP is set in `tauri.conf.json` – add external resources there, never disable CSP.

### DataScript (optional)

- `db.cljs` holds `:datascript/db` (empty DB).
- `events.cljs` provides `::transact` to update the DB.
- `subs.cljs` provides `::query` (takes Datalog query & args).

### Feature Flags

- Located in `db.cljs` under `:config :features`.
- Subscribe with `@(rf/subscribe [::subs/feature-enabled? :my-flag])`.
- Toggle with `(rf/dispatch [::events/toggle-feature :my-flag])`.

---

## OpenSpec Workflow

For non‑trivial features, **use OpenSpec** to propose, design, and implement changes.

```bash
# Create a new change
just os-new feature-name

# Write artifacts (follow instructions output)
openspec instructions proposal --change feature-name
openspec instructions specs   --change feature-name
openspec instructions design  --change feature-name   # optional
openspec instructions tasks   --change feature-name

# Implement tasks (update - [x] in tasks.md)
# Validate & archive
openspec validate feature-name
echo y | openspec archive feature-name
```

Use `just os-status feature-name` to check artifact completion.

---

## Adding a New Page

1. **Create** `src/clojkstra/app/pages/my_page.cljs` with `(defn page [] …)`.
2. **Add route** in `routes.cljs` → `routes` map: `"/my-page" :my-page`.
3. **Require the page** in `views.cljs` and add a `case` branch in `page-for-route`.
4. **Add nav link** (optional) in `views.cljs` → `nav-links`.
5. **Seed state** – add a top‑level key to `default-db` in `db.cljs` with a comment.
6. **Add events** in `events.cljs`, **subscriptions** in `subs.cljs`.

---

## Adding a New UI Component

- Place in `components/ui.cljs` (or a new file like `components/forms.cljs` and re‑export).
- Must be a **pure function** of props – no `rf/subscribe`.
- Accept an optional `:class` prop.
- Document the prop signature in the docstring.

---

## Adding a New Effect Handler

1. Open `effects.cljs`.
2. Register with `(rf/reg-fx :my-effect (fn [data] …))`.
3. Document the expected shape of `data`.
4. Use it from a `reg-event-fx` by returning `{:my-effect …}`.

---

## Coding Conventions (Must Follow)

| Rule | Explanation |
|------|-------------|
| **No side effects in views** | Views call `rf/dispatch` and `@(rf/subscribe …)` only. |
| **No direct `db` reads in views** | Always go through named subscriptions (`subs.cljs`). |
| **Events mutate `db`; effects reach outside** | Use `reg-event-db` for pure updates; `reg-event-fx` for side effects. |
| **Layer‑2 / layer‑3 subscriptions** | Layer‑2 reads raw slice; layer‑3 uses `:<-` to depend on layer‑2. |
| **All new state in `db.cljs` first** | Add keys with comments before writing events or subs. |
| **Keyword namespacing** | Use `::` in its own namespace; alias‑qualified (`::events/...`) elsewhere. |
| **`standard-interceptors`** | Must be attached to every `reg-event-db` / `reg-event-fx`. |
| **No direct Tauri calls** | Always go through an effect handler – never `js/window.__TAURI__` inline. |

### Namespace Aliases (Project‑wide)

```clojure
(:require [re-frame.core :as rf]
          [reagent.core :as r]
          [reagent.dom :as rdom]
          [clojkstra.app.events :as events]
          [clojkstra.app.subs :as subs]
          [clojkstra.app.routes :as routes]
          [clojkstra.app.components.ui :as ui])
```

---

## Linting & Formatting

- `clj-kondo` – static analysis (linting).
- `cljfmt` – whitespace and indentation rules.
- Run `just ci` before committing – this runs both checks + production build.
- Auto‑format with `just fmt`.

---

## Tauri Specifics

### Configuration

- `src-tauri/tauri.conf.json` controls window size, CSP, build hooks.
- `beforeDevCommand` and `beforeBuildCommand` already set to `bun run dev` / `bun run release`.

### Capabilities

- Permissions for Tauri commands are defined in `src-tauri/capabilities/*.json`.
- When adding a new Rust command, add a corresponding capability entry.

### Adding a New Tauri Command

1. Define Rust function in `src-tauri/src/lib.rs` with `#[tauri::command]`.
2. Add it to `.invoke_handler(tauri::generate_handler![...])` in the same file.
3. Add a capability entry for the command.
4. Create an event‑effect pair in `effects.cljs` and `events.cljs` (following the `::invoke-tauri` pattern).
5. Never call `js/window.__TAURI__.core.invoke` inline – always go through the effect handler.

### Content Security Policy (CSP)

- Defined in `tauri.conf.json` under `app.security.csp`.
- If you add an external resource (CDN, font host, etc.), add its origin to the relevant CSP directive.
- **Never set `csp` to `null`** – that disables all CSP protection.

---

## Version Control (Jujutsu)

- This repo uses **Jujutsu (`jj`)** – not plain Git.
- The working copy is always a change – no `git add` needed.
- Important `jj` concepts (wrapped in `just` recipes):
  - `@` – the current change.
  - `jj commit` – finalises `@` and opens a new empty child change.
  - `jj bookmark set main --revision @-` – advance the `main` bookmark after commit.
- **Never run `git commit`, `git add`, or `git checkout` directly.**
- Use `just commit`, `just snap`, `just ship`, `just abandon`.

---

## Continuous Integration & Deployment

- `just ci` runs `lint`, `fmt-check`, and `build`.
- `just deploy "message"` builds the app, commits the result (including `docs/`), and pushes `main` to origin – used for GitHub Pages.
- The `docs/` folder is served directly from the `main` branch.

---

## What NOT to Do

| Avoid | Because |
|-------|---------|
| Using `npm` / `node` / `yarn` | Project uses `bun` exclusively. |
| Direct `git` commands | Use `jj` or `just` recipes. |
| Editing `docs/cljs-out/` | Generated output. |
| Business logic in `views.cljs` or `pages/` | Views dispatch only; logic belongs in events/subs/effects. |
| Adding top‑level deps without checking license & CLJS compatibility | Keeps the template portable. |
| Fixing `[DEMO]` problems by breaking `[FRAMEWORK]` files | Delete the demo instead. |

---

## Getting Started

```bash
# Clone the repo (using jj)
jj git clone https://github.com/ArikRahman/Clojkstra
cd Clojkstra

# Install dependencies
bun install

# Start development server
just dev

# Open http://localhost:8080
just open

# Run checks before committing
just ci
```

For desktop development:

```bash
# Install Tauri dependencies (or use nix develop)
just tauri-dev
```

---

## License & Origin

Clojkstra is a starter template created by Arik Rahman.  
Use it freely as a scaffold for your own ClojureScript + re‑frame projects.
