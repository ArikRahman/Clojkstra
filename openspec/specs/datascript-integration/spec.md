# datascript-integration Specification

## Purpose
TBD - created by archiving change datascript-impl. Update Purpose after archive.
## Requirements
### Requirement: Application incorporates DataScript dependency
The project MUST include `datascript` in `deps.edn` to allow usage of its APIs.

#### Scenario: Build success
- **WHEN** the project builds
- **THEN** it resolves the `datascript/datascript` library without error

### Requirement: Database is stored in re-frame state
The application MUST initialize a DataScript database and store it at `:datascript/db` in `app-db`.

#### Scenario: Initial state check
- **WHEN** the app starts
- **THEN** `app-db` has an empty DataScript DB at `:datascript/db`

### Requirement: Users can transact data
The application MUST provide a re-frame event `::transact` that applies transaction data using `d/db-with` and updates `:datascript/db`.

#### Scenario: Transact entity
- **WHEN** `::transact` is dispatched with `[{:db/id -1 :name "Test"}]`
- **THEN** the `:datascript/db` is updated to include the new entity

### Requirement: Users can query data
The application MUST provide a re-frame subscription `::query` that runs Datalog queries against `:datascript/db`.

#### Scenario: Query entities
- **WHEN** `::query` is subscribed with `[:find ?e ?n :where [?e :name ?n]]`
- **THEN** it returns a set of tuples matching the query from the current DataScript DB

### Requirement: Datascript demo page
The application MUST provide a `/datascript-demo` page that demonstrates inserting a name and querying all names.

#### Scenario: Insert and view
- **WHEN** the user adds a name on the demo page
- **THEN** it appears in the list of queried names on the same page

