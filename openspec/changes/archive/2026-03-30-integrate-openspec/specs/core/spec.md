# Specification: OpenSpec Integration

## ADDED Requirements

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