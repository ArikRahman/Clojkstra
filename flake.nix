{
  description = "Clojkstra — ClojureScript + re-frame starter template";

  inputs = {
    nixpkgs.url     = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };

        # ---------------------------------------------------------------------------
        # JDK — shadow-cljs requires a JVM to compile ClojureScript.
        # JDK 21 (LTS) is used here; swap for pkgs.jdk17 if preferred.
        # ---------------------------------------------------------------------------
        jdk = pkgs.jdk21;

        # ---------------------------------------------------------------------------
        # Bun — the JS runtime used for all package management and script running.
        # NEVER use npm or node directly; always use bun / bunx.
        # ---------------------------------------------------------------------------
        bun = pkgs.bun;

      in
      {
        # ---------------------------------------------------------------------------
        # devShell
        #
        # Enter with:  nix develop
        # Or, with direnv:  echo "use flake" > .envrc && direnv allow
        #
        # Everything needed to build, run, test, and lint the project is
        # available in this shell.  No global installs required.
        # ---------------------------------------------------------------------------
        devShells.default = pkgs.mkShell {
          name = "clojkstra-dev";

          buildInputs = [
            # --- Language runtimes ---
              jdk
              bun

            # --- Utilities ---
            pkgs.git
            pkgs.curl          # handy for quick API checks during development
            pkgs.jq            # JSON pretty-printing / querying
            pkgs.clj-kondo     # ClojureScript linter
            pkgs.cljfmt        # ClojureScript formatter
          ];

          # Ensure the JVM used by shadow-cljs is the pinned one.
          JAVA_HOME = jdk;

          # Set a clear prompt so developers know they are in the Nix shell.
          shellHook = ''
            echo ""
            echo "  ⚡  Clojkstra dev shell ready"
            echo ""
            echo "  Runtime versions:"
            echo "    java    $(java -version 2>&1 | head -1)"
            echo "    bun     $(bun --version)"
            echo ""
            echo "  Available commands:"
            echo "    bun run dev     — start shadow-cljs watch + dev server on :8080"
            echo "    bun run release — production build  →  docs/cljs-out/"
            echo "    bun run clean   — remove build artefacts"
            echo "    bun run report  — generate build size report"
            echo "    clj-kondo --lint src/  — lint"
            echo "    cljfmt check src/      — check formatting"
            echo "    cljfmt fix src/        — auto-fix formatting"
            echo ""

            # Install JS dependencies via bun if node_modules is absent.
            # This is a no-op if bun.lockb + node_modules are already up to date.
            if [ ! -d node_modules ]; then
              echo "  → Running 'bun install' to hydrate node_modules…"
              bun install
              echo ""
            fi
          '';
        };

        # ---------------------------------------------------------------------------
        # formatter — run with: nix fmt
        # Uses nixpkgs-fmt to keep flake.nix itself consistently formatted.
        # ---------------------------------------------------------------------------
        formatter = pkgs.nixpkgs-fmt;
      });
}
