## Context

We are adding DataScript to a re-frame application. The `app-db` is currently a plain Clojure map. We will embed a DataScript database connection or value inside `app-db` or keep it alongside it. Since DataScript databases are immutable values, we can store the DB value inside `app-db`.

## Goals / Non-Goals

**Goals:**
- Include DataScript.
- Demonstrate adding facts.
- Demonstrate querying facts.

**Non-Goals:**
- Replacing `app-db` entirely.
- Complex migrations or schemas.

## Decisions

- **Store DataScript DB as a value in `app-db`**: We will put it under the `:datascript/conn` key in `app-db` but wait, DataScript uses connections for atoms. Let's just store the immutable DB value in `app-db` at `:datascript/db` and use `datascript.core/empty-db` to initialize it. Or we can just use a real connection `d/create-conn` and interact with it, but that breaks re-frame purity. Actually, re-frame apps typically store the `d/empty-db` in `app-db` and update it via `d/db-with` to remain pure. We will use `d/db-with`.

## Risks / Trade-offs

- Performance overhead of `db-with` on large datasets might be noticeable, but it's pure and fits re-frame perfectly.