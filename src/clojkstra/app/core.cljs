(ns clojkstra.app.core
  "Core entry point for Clojkstra.
   [FRAMEWORK FILE] — wires together startup, routing, and rendering.
   This namespace is intentionally thin; behaviour lives in events/subs/views."
  (:require
   [reagent.dom          :as rdom]
   [re-frame.core        :as rf]
   [clojkstra.app.db     :as db]
   [clojkstra.app.events :as events]
   [clojkstra.app.subs   :as subs]
   [clojkstra.app.routes :as routes]
   [clojkstra.app.views  :as views]))

;; ---------------------------------------------------------------------------
;; Registration side-effects
;;
;; Requiring db/events/subs/routes causes their top-level `reg-*` calls to
;; run.  We name them explicitly here so the startup path is easy to trace —
;; no magic, no deferred loading surprises.
;; ---------------------------------------------------------------------------

(def ^:private mount-point
  "The DOM node that Reagent renders into.
   Change this if your HTML uses a different id."
  "app")

(defn- mount-root []
  (rf/clear-subscription-cache!)
  (let [root (.getElementById js/document mount-point)]
    (rdom/render [views/app-root] root)))

;; ---------------------------------------------------------------------------
;; Public API — called by shadow-cljs
;; ---------------------------------------------------------------------------

(defn init []
  "Called once when the page first loads.
   Order matters: db defaults → routes → render."
  (rf/dispatch-sync [::events/initialize-db])
  (routes/start!)
  (mount-root))

(defn on-reload []
  "Called by shadow-cljs after every hot-reload.
   Re-renders without reinitialising the app-db or router."
  (mount-root))
