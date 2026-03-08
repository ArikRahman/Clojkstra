(ns clojkstra.app.routes
  "Client-side routing for Clojkstra.
   [FRAMEWORK FILE] — defines the route table and wires pushy to re-frame.

   Uses:
     bidi  — data-driven bidirectional route matching
     pushy — HTML5 history / hash-fragment listener

   Hash-based routing is enabled so the app works on GitHub Pages without
   a server-side rewrite rule.  pushy is configured with a custom identity
   function that strips the leading '#' before matching.

   Extension point:
     Add new routes to `app-routes` and a corresponding page component in
     pages/.  The router dispatches ::events/set-route on every navigation
     and views/app-root selects the right page via the ::subs/current-handler
     subscription."
  (:require
   [bidi.bidi      :as bidi]
   [pushy.core     :as pushy]
   [re-frame.core  :as rf]
   [clojkstra.app.events :as events]))

;; ---------------------------------------------------------------------------
;; Route table
;; [FRAMEWORK] The structure is pure data — easy to extend, test, or print.
;;
;; Format: [prefix [[path handler] ...]]
;; Add new top-level pages here.  Nested routes can use bidi's full syntax.
;; ---------------------------------------------------------------------------

(def app-routes
  ["/" {""        :home
        "about"   :about
        "example" :example
        true      :not-found}])

;; ---------------------------------------------------------------------------
;; Helpers
;; ---------------------------------------------------------------------------

(defn path-for
  "Generate a URL path string for a given route handler and optional params.
   Example: (path-for :about)  => \"/about\"
            (path-for :home)   => \"/\""
  ([handler]
   (bidi/path-for app-routes handler))
  ([handler params]
   (apply bidi/path-for app-routes handler (mapcat identity params))))

(defn- match-path
  "Match a raw path string against the route table.
   Returns a map of {:handler kw :route-params map} or nil on no match."
  [path]
  (bidi/match-route app-routes path))

;; ---------------------------------------------------------------------------
;; Hash routing support for GitHub Pages
;; ---------------------------------------------------------------------------

(defn- hash-identity
  "Transforms the browser's current token for pushy.
   Strips a leading '#' so bidi can match clean path strings like '/about'
   even though the real URL is '/#/about'."
  [path]
  ;; pushy exposes the full hash fragment including '#'; drop it.
  (if (clojure.string/starts-with? path "#")
    (subs path 1)
    path))

(defn- dispatch-route
  "Called by pushy whenever the URL changes.
   Dispatches ::events/set-route with the matched bidi route map,
   falling back to :not-found if no route matches."
  [matched]
  (let [route (or matched {:handler :not-found :route-params {}})]
    (rf/dispatch [::events/set-route route])))

;; ---------------------------------------------------------------------------
;; Router instance
;; [FRAMEWORK] Defined at module load; started explicitly in core/init.
;; ---------------------------------------------------------------------------

(defonce ^:private router
  (pushy/pushy dispatch-route
               (fn [path]
                 (match-path (hash-identity path)))))

;; ---------------------------------------------------------------------------
;; Public API
;; ---------------------------------------------------------------------------

(defn start!
  "Initialise the pushy listener.  Call once from core/init.
   pushy will immediately dispatch the route for the current URL."
  []
  (pushy/start! router))

(defn stop!
  "Tear down the pushy listener.  Useful in tests or when hot-reloading
   requires a clean slate — core/on-reload does NOT call this."
  []
  (pushy/stop! router))

(defn navigate!
  "Programmatically navigate to a route handler.
   Pushes a new history entry; pushy fires dispatch-route automatically.
   Example: (navigate! :about)"
  ([handler]
   (pushy/set-token! router (str "#" (path-for handler))))
  ([handler params]
   (pushy/set-token! router (str "#" (path-for handler params)))))
