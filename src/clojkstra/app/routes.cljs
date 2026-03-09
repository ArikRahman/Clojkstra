(ns clojkstra.app.routes
    "Client-side routing for Clojkstra.
   [FRAMEWORK FILE] — hash-based routing with no external dependencies.

   How it works:
     - A js/window hashchange listener fires on every URL change.
     - The hash fragment is parsed into a route keyword and dispatched
       to ::events/set-route so the rest of the app is notified.
     - navigate! pushes a new hash and triggers the listener.

   Route table:
     Add entries to `routes` to register new pages.
     Each key is the hash path string, each value is the handler keyword.

   Extension point:
     1. Add an entry to `routes` below.
     2. Create pages/my_page.cljs with (defn page [] ...)
     3. Require it in views.cljs and add a case in page-for-route.
     4. Add a nav link in views.cljs nav-links."
    (:require
     [re-frame.core        :as rf]
     [clojkstra.app.events :as events]
     [clojure.string]))

;; ---------------------------------------------------------------------------
;; Route table
;; key   — hash path as it appears after #  (e.g. "/" or "/about")
;; value — handler keyword dispatched to ::events/set-route
;; ---------------------------------------------------------------------------

(def ^:private routes
     {"/"        :home
      "/about"   :about
      "/example" :example})

;; ---------------------------------------------------------------------------
;; Helpers
;; ---------------------------------------------------------------------------

(defn- current-hash []
       (let [h (.. js/window -location -hash)]
            (if (clojure.string/starts-with? h "#")
                (subs h 1)
                h)))

(defn- match [path]
       (or (get routes path)
           (get routes "/")
           :not-found))

(defn- dispatch-current! []
       (let [path (current-hash)
             path (if (clojure.string/blank? path) "/" path)]
            (rf/dispatch [::events/set-route {:handler      (match path)
                                              :route-params {}}])))

;; ---------------------------------------------------------------------------
;; Public API
;; ---------------------------------------------------------------------------

(defn path-for
      "Returns the hash path string for a given route handler keyword.
   Example: (path-for :about) => \"/about\""
      [handler]
      (or (key (first (filter #(= (val %) handler) routes))) "/"))

(defn navigate!
      "Pushes a new route by setting the window hash.
   Triggers the hashchange listener automatically.

   Arity:
     (navigate! handler)             ;; existing behaviour
     (navigate! handler params)      ;; optional params map appended as query string

   Example:
     (navigate! :about)
     (navigate! :example {:id 42 :filter \"all\"})"
      ([handler]
       (set! (.. js/window -location -hash) (path-for handler)))
      ([handler params]
       (let [base (path-for handler)
             qs (when (and params (seq params))
                      (->> params
                           (map (fn [[k v]]
                                    (str (name k) "=" (js/encodeURIComponent (str v)))))
                           (clojure.string/join "&")))]
            (set! (.. js/window -location -hash) (if qs (str base "?" qs) base)))))

(defn start!
      "Attach the hashchange listener and dispatch the current route.
   Call once from core/init."
      []
      (.addEventListener js/window "hashchange" dispatch-current!)
      (dispatch-current!))
