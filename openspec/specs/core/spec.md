# core Specification

## Purpose
This specification defines the core workflow, architecture rules, and coding conventions for the Clojkstra repository. It acts as the source-of-truth for agent and developer behavior, integrating OpenSpec workflows with strict framework guidelines.

## Requirements

### Requirement: OpenSpec Change Creation
For any non-trivial task, feature addition, behavior change, or architectural modification, developers MUST create an OpenSpec change under `openspec/changes/<change-id>/` before beginning implementation.

#### Scenario: Complex Feature Addition
When a developer adds a complex feature, they create a change proposal via OpenSpec CLI.

### Requirement: Change Contents
Every OpenSpec change MUST include a `proposal.md`, `tasks.md`, and a spec delta under `specs/<capability>/spec.md` using explicit ADDED, MODIFIED, or REMOVED requirements.

#### Scenario: Drafting a Proposal
When scaffolding a new change, the developer drafts `proposal.md` and `tasks.md` to define the scope and implementation steps.

### Requirement: Architectural Changes
A `design.md` file MUST be included if the change touches architecture, Tauri integration, routing, cross-page state flow, or spans both Rust and ClojureScript layers.

#### Scenario: Adding a Tauri Command
When a change introduces a new Tauri command, the developer includes a `design.md` detailing the Rust and ClojureScript integration.

### Requirement: Implementation and Archiving
Code implementation MUST strictly follow the approved `tasks.md` checklist in order. Completed OpenSpec changes MUST be archived using the OpenSpec CLI to merge delta specs into the living `openspec/specs/` directory.

#### Scenario: Archiving a Change
After all tasks are completed and verified, the developer runs `openspec archive <change-id>` to finalize the change and update the main specs.

### Requirement: CLI Tooling
The repository MUST include `.github/` configuration and skills to support AI-native spec-driven development using the GitHub Copilot toolset, and use the `openspec` CLI as the primary tool.

#### Scenario: Initializing OpenSpec
The developer runs `openspec init --tools github-copilot` to set up the necessary tooling in the repository.

### Requirement: Runtime and Package Management
All JavaScript dependencies and scripts MUST be managed using `bun` or `bunx`. The use of `npm`, `node`, `yarn`, or `npx` is strictly forbidden.

#### Scenario: Adding a JS Dependency
A developer needs a new JS package and installs it using `bun add <package>`, ensuring it is FOSS-licensed and compatible with ClojureScript.

### Requirement: Task Running
Developers and agents MUST use `just` for all command-line operations (e.g., `just dev`, `just build`, `just check`). Raw `bun`, `clj-kondo`, or `cargo-tauri` commands MUST NOT be constructed unless a `just` recipe does not exist.

#### Scenario: Starting the Development Server
To start the app, the developer runs `just dev` rather than invoking `shadow-cljs` directly.

### Requirement: Version Control
The repository uses `jujutsu` (`jj`) for version control. Raw `git commit`, `git add`, or `git checkout` commands MUST NOT be used. Developers MUST use `just` recipes (`just commit`, `just snap`, `just ship`) or `jj` commands to manage working copies.

#### Scenario: Committing Work
After completing a feature, the developer runs `just commit "Added new feature"` to finalize the change and advance the main bookmark.

### Requirement: re-frame Architecture
Views MUST NOT contain side effects or direct database reads. State mutations MUST occur via `reg-event-db` or `reg-event-fx`. Side effects MUST be handled by registered `reg-fx` handlers in `effects.cljs`.

#### Scenario: Fetching Data on Button Click
A view dispatches an event on click. The event handler uses `reg-event-fx` to trigger a custom effect, which performs the external call and dispatches a success event with the result.

### Requirement: Tauri Integration
The frontend MUST communicate with the Rust backend exclusively through Tauri commands and events. Direct calls to `js/window.__TAURI__` from views or event handlers are forbidden and MUST be routed through a `reg-fx` effect in `effects.cljs` (e.g., `::invoke-tauri`).

#### Scenario: Invoking a Native Command
An event handler returns `{::effects/invoke-tauri {:command "my_cmd" ...}}`. The registered effect handler safely interacts with the Tauri API.

### Requirement: Namespace Conventions
ClojureScript files MUST mirror their namespace using hyphens for namespaces and underscores for file paths (e.g., `src/clojkstra/app/pages/home.cljs` -> `clojkstra.app.pages.home`). Event and subscription keywords MUST use namespaced keywords (e.g., `::initialize-db`).

#### Scenario: Registering a New Event
In `events.cljs`, an event is registered as `(rf/reg-event-db ::my-event ...)`, ensuring it does not collide with other namespaces.