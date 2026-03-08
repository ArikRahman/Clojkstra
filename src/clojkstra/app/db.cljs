(ns clojkstra.app.db
  "App-db schema and default state for Clojkstra.
   [FRAMEWORK FILE] — defines the shape of the entire application state.

   Convention:
     - Top-level keys mirror the major concerns of the app.
     - Keep this flat where possible; nest only when the domain demands it.
     - Add a comment for every top-level key so contributors know its purpose.

   Extension point:
     When starting a new app from this template, add your domain's initial
     state here (e.g. :user, :api-response, :feature-flags) and update the
     spec if you add cljs.spec validation.")

;; ---------------------------------------------------------------------------
;; Default state
;; ---------------------------------------------------------------------------

(def default-db
  {;; [FRAMEWORK] Current route matched by bidi/pushy — set by router.
   :current-route {:handler :home :route-params {}}

   ;; [FRAMEWORK] UI-level loading / error signals.
   :loading?       false
   :error          nil

   ;; [FRAMEWORK] App-wide configuration surface.
   ;; Override these when cloning for a new project.
   :config
   {:app-name    "Clojkstra"
    :version     "0.1.0"
    ;; Theme tokens — consumed by view components.
    ;; Swap these out to restyle without touching markup.
    :theme
    {:color-primary   "#5B4FE8"
     :color-secondary "#F5A623"
     :color-bg        "#FAFAFA"
     :color-surface   "#FFFFFF"
     :color-text      "#1A1A2E"
     :color-muted     "#6B7280"
     :font-sans       "Inter, system-ui, sans-serif"
     :font-mono       "JetBrains Mono, monospace"}
    ;; Feature toggles — check via the :config/feature-enabled? subscription.
    ;; Add new flags here to gate in-progress features.
    :features
    {:example-feature true
     :debug-panel     true}}

   ;; [DEMO] Counter used by the example event-dispatch demo on the home page.
   ;; Safe to remove when building a real app.
   :counter 0

   ;; [DEMO] Example async-style notification list.
   ;; Demonstrates how transient UI state lives in app-db.
   ;; Safe to remove when building a real app.
   :notifications []})
