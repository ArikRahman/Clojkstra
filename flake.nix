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

        # Tauri v2 on Linux requires a set of system libraries for WebKit / GTK.
        tauriLinuxDeps = with pkgs; [
          pkg-config
          gobject-introspection
          gtk3
          glib
          gdk-pixbuf
          pango
          cairo
          atk
          webkitgtk_4_1   # WebKit2GTK 4.1 — required by Tauri v2
          libsoup_3
          openssl
          libayatana-appindicator
          librsvg
          xdotool            # optional but handy for window automation in tests
          xorg.libX11
          xorg.libXcursor
          xorg.libXrandr
          xorg.libXi
        ];

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

            # --- Rust toolchain (needed by cargo-tauri / src-tauri) ---
            pkgs.rustc
            pkgs.cargo
            pkgs.rustfmt
            pkgs.clippy

            # --- Tauri CLI ---
            pkgs.cargo-tauri

            # --- Tauri system dependencies (Linux only) ---
          ] ++ (pkgs.lib.optionals pkgs.stdenv.isLinux tauriLinuxDeps) ++ [

            # --- Utilities ---
            pkgs.git
            pkgs.curl          # handy for quick API checks during development
            pkgs.jq            # JSON pretty-printing / querying
            pkgs.clj-kondo     # ClojureScript linter
            pkgs.cljfmt        # ClojureScript formatter
            pkgs.just          # task runner used by justfile
          ];

          # Ensure the JVM used by shadow-cljs is the pinned one.
          JAVA_HOME = jdk;

          # pkg-config needs to find the Tauri system libs.
          PKG_CONFIG_PATH = pkgs.lib.optionalString pkgs.stdenv.isLinux (
            pkgs.lib.makeSearchPathOutput "dev" "lib/pkgconfig" tauriLinuxDeps
          );

          # GIO_MODULE_DIR is required by WebKit on some NixOS setups.
          GIO_MODULE_DIR = pkgs.lib.optionalString pkgs.stdenv.isLinux
            "${pkgs.glib-networking}/lib/gio/modules";

          # Point Rust to OpenSSL headers so the openssl-sys crate compiles.
          OPENSSL_DIR = pkgs.lib.optionalString pkgs.stdenv.isLinux
            "${pkgs.openssl.dev}";
          OPENSSL_LIB_DIR = pkgs.lib.optionalString pkgs.stdenv.isLinux
            "${pkgs.openssl.out}/lib";

          # Set a clear prompt so developers know they are in the Nix shell.
          shellHook = ''
            echo ""
            echo "  ⚡  Clojkstra dev shell ready"
            echo ""
            echo "  Runtime versions:"
            echo "    java    $(java -version 2>&1 | head -1)"
            echo "    bun     $(bun --version)"
            echo "    rustc   $(rustc --version)"
            echo "    cargo   $(cargo --version)"
            echo ""
            echo "  Available commands:"
            echo "    just dev          — start shadow-cljs watch + dev server on :8080"
            echo "    just build        — production ClojureScript build → docs/cljs-out/"
            echo "    just tauri-dev    — start Tauri desktop app with hot reload"
            echo "    just tauri-build  — build Tauri desktop app + installers"
            echo "    just tauri-info   — show Tauri environment info"
            echo "    just lint         — clj-kondo lint"
            echo "    just fmt          — auto-fix ClojureScript formatting"
            echo "    just ci           — lint + fmt-check + release build"
            echo ""

            # Install JS dependencies via bun if node_modules is absent.
            # This is a no-op if bun.lock + node_modules are already up to date.
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
