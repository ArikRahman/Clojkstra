# Clojkstra — justfile
# Install just: https://github.com/casey/just
# Run from Clojkstra/ with: just <recipe>
#
# Tauri recipes require Rust/Cargo in PATH.
# In the Nix devShell (nix develop) these are provided automatically.

# ── Settings ──────────────────────────────────────────────────────────────────

set shell := ["sh", "-c"]

# Default: list available recipes
default:
    @just --list

# ── Dependency management ──────────────────────────────────────────────────────

# Install all bun dependencies
install:
    bun install

# ── Development ────────────────────────────────────────────────────────────────

# Start the shadow-cljs dev server with hot reload (http://localhost:8080)
# Kills any stale watcher first so you never hit "already started".
dev:
    @pkill -f "shadow.cljs.devtools.cli" 2>/dev/null && echo "Stopped stale shadow-cljs process." || true
    bun run dev

# Open the local dev site in the default browser
open:
    @echo "Opening http://localhost:8080 …"
    xdg-open http://localhost:8080 2>/dev/null || open http://localhost:8080 2>/dev/null || echo "Could not open browser — visit http://localhost:8080 manually"

# Kill any lingering shadow-cljs JVM process
kill:
    @echo "Killing shadow-cljs server processes …"
    pkill -f "shadow.cljs.devtools.cli" && echo "Done." || echo "No shadow-cljs process found."

# Alias for kill — cleanly stop the shadow-cljs watcher/server
stop: kill

# ── Build ──────────────────────────────────────────────────────────────────────

# Production release build → docs/cljs-out/main.js
build:
    bun run release

# Remove the compiled output and shadow-cljs cache
clean:
    bun run clean
    @echo "Cache and output cleared."

# Clean then do a full production build
rebuild: clean build

# Generate a build size report → report.html
report:
    bun run report

# ── Checks ────────────────────────────────────────────────────────────────────

# Lint all ClojureScript sources with clj-kondo
lint:
    clj-kondo --lint src/

# Check formatting without modifying files
fmt-check:
    cljfmt check src/

# Auto-fix formatting in place
fmt:
    cljfmt fix src/

# Format flake.nix with nixpkgs-fmt
fmt-nix:
    nix fmt

# Run lint + fmt-check
check: lint fmt-check

# ── CI ────────────────────────────────────────────────────────────────────────

# Run all checks then a production build — use before committing
ci: check build

# ── Tauri ─────────────────────────────────────────────────────────────────────

# Start the Tauri desktop app in development mode with hot reload.
# shadow-cljs watch is launched automatically via beforeDevCommand in tauri.conf.json.
# Runs inside nix develop so cargo, pkg-config, and GTK libs are all on PATH.
tauri-dev:
    nix develop --command cargo-tauri dev

# Build the Tauri desktop app in release mode and generate installers.
# The ClojureScript release build is run automatically via beforeBuildCommand.
# Runs inside nix develop so cargo, pkg-config, and GTK libs are all on PATH.
tauri-build:
    nix develop --command cargo-tauri build

# Show Tauri environment info (Rust, OS, relevant config)
tauri-info:
    nix develop --command cargo-tauri info

# ── Deploy ────────────────────────────────────────────────────────────────────

# Build, commit all changes (including docs/), and push to GitHub Pages.
# GitHub Pages serves docs/ from the main branch.
# Fetches + rebases onto remote main first so the bookmark never goes sideways.
# Skips the commit step if the working copy has no changes.
# Usage: just deploy           (uses default message "deploy")
#        just deploy "message"
deploy msg="deploy": build
    jj git fetch
    jj rebase -d main@origin
    @if jj diff --summary | grep -q .; then \
        jj commit -m "{{msg}}"; \
    else \
        echo "Nothing changed — skipping commit, proceeding to push."; \
    fi
    jj bookmark set main --revision @-
    jj bookmark track main@origin 2>/dev/null || true
    jj git push --bookmark main

# ── Utilities ─────────────────────────────────────────────────────────────────

# Count lines of ClojureScript source
loc:
    @find src -name "*.cljs" | xargs wc -l | tail -1

# List all .cljs source files
files:
    @find src -name "*.cljs" | sort

# Show the current size of the production bundle
bundle-size:
    @ls -lh docs/cljs-out/main.js 2>/dev/null || echo "docs/cljs-out/main.js not found — run: just build"

# Print a quick summary of the project state
status:
    @echo "── Source files ──────────────────────────────────────────────────"
    @find src -name "*.cljs" | sort
    @echo ""
    @echo "── Bundle ────────────────────────────────────────────────────────"
    @ls -lh docs/cljs-out/main.js 2>/dev/null || echo "  (not built)"
    @echo ""
    @echo "── shadow-cljs cache ─────────────────────────────────────────────"
    @ls .shadow-cljs/ 2>/dev/null && echo "  (cache present)" || echo "  (no cache)"

# ── Version control (jujutsu) ─────────────────────────────────────────────────

# Show working copy status
st:
    jj status

# Show recent commit log (last 10 changes)
log:
    jj log --limit 10

# Show diff of all working copy changes
diff:
    jj diff

# Show diff for a specific file — usage: just fdiff src/clojkstra/app/events.cljs
fdiff file:
    jj diff -- "{{file}}"

# Set the description of the current working copy change
# Usage: just describe "what I changed"
describe message:
    jj describe -m "{{message}}"

# Finalise the current change and open a new empty child change.
# Advances the main bookmark to the committed change.
# Usage: just commit "what I changed"
commit message:
    jj commit -m "{{message}}"
    jj bookmark set main --revision @-

# Push the main bookmark to origin
push:
    jj git push --bookmark main

# Fetch latest changes from origin
fetch:
    jj git fetch
    jj log --limit 5

# One-step: commit, advance bookmark, and push to origin.
# Usage: just snap "what I changed"
snap message:
    jj git fetch
    jj rebase -d main@origin
    jj commit -m "{{message}}"
    jj bookmark set main --revision @-
    jj bookmark track main@origin 2>/dev/null || true
    jj git push --bookmark main

# One-step: run ci checks, commit, advance bookmark, push.
# Usage: just ship "what I changed"
ship message: ci
    jj git fetch
    jj rebase -d main@origin
    jj commit -m "{{message}}"
    jj bookmark set main --revision @-
    jj bookmark track main@origin 2>/dev/null || true
    jj git push --bookmark main

# Abandon the current (empty) working copy change and move @ to parent
abandon:
    jj abandon @
