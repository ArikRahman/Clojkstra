(ns clojkstra.app.effects
  "Custom re-frame effect handlers for Clojkstra.
   [FRAMEWORK FILE] — registers side-effectful handlers that event handlers
   can invoke via the :effects map returned from reg-event-fx.

   Included effects:
     :navigate        — push a new route via the router
     :set-title       — update document.title
     :local-storage   — read/write browser localStorage
     :log             — structured console logging (dev convenience)
     :set-timeout     — fire a re-frame dispatch after a delay

   Extension point:
     Add new effects here when your app needs to reach outside of app-db
     (HTTP, WebSocket, native APIs, analytics, etc.).
     Keep each effect handler a pure side-effect function — no db reads."
  (:require
   [re-frame.core       :as rf]
   [clojkstra.app.routes :as routes]))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] :navigate
;;
;; Push a new route into the browser history.
;; Usage in an event handler:
;;   {:navigate {:handler :about}}
;;   {:navigate {:handler :example :params {:id "42"}}}
;; ---------------------------------------------------------------------------

(rf/reg-fx
 :navigate
 (fn [{:keys [handler params]}]
   (if params
     (routes/navigate! handler params)
     (routes/navigate! handler))))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] :set-title
;;
;; Set the browser tab / document title.
;; Usage: {:set-title "My Page – Clojkstra"}
;; ---------------------------------------------------------------------------

(rf/reg-fx
 :set-title
 (fn [title]
   (set! (.-title js/document) (str title))))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] :local-storage
;;
;; Read or write a value in browser localStorage.
;; Values are JSON-serialised automatically.
;;
;; Write usage:
;;   {:local-storage {:op :set :key "prefs" :value {:theme "dark"}}}
;;
;; Read usage (fires a dispatch with the retrieved value):
;;   {:local-storage {:op :get :key "prefs" :on-success [::events/prefs-loaded]}}
;;
;; Remove usage:
;;   {:local-storage {:op :remove :key "prefs"}}
;; ---------------------------------------------------------------------------

(rf/reg-fx
 :local-storage
 (fn [{:keys [op key value on-success]}]
   (case op
     :set
     (try
       (.setItem js/localStorage key (.stringify js/JSON (clj->js value)))
       (catch :default e
         (.warn js/console "[clojkstra] localStorage :set failed" e)))

     :get
     (when on-success
       (try
         (let [raw  (.getItem js/localStorage key)
               data (when raw (js->clj (.parse js/JSON raw) :keywordize-keys true))]
           (rf/dispatch (conj on-success data)))
         (catch :default e
           (.warn js/console "[clojkstra] localStorage :get failed" e)
           (rf/dispatch (conj on-success nil)))))

     :remove
     (.removeItem js/localStorage key)

     ;; default
     (.warn js/console "[clojkstra] :local-storage unknown op:" op))))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] :log
;;
;; Structured console logging.  Use in event handlers to surface important
;; transitions without scattering js/console calls around the codebase.
;;
;; Usage:
;;   {:log {:level :info :msg "User signed in" :data {:user-id 42}}}
;;   {:log {:level :warn :msg "Token expiring soon"}}
;;   {:log {:level :error :msg "Fetch failed" :data {:status 500}}}
;;
;; Levels: :debug | :info | :warn | :error
;; ---------------------------------------------------------------------------

(rf/reg-fx
 :log
 (fn [{:keys [level msg data]
       :or   {level :info}}]
   (let [prefix (str "[clojkstra] " msg)
         log-fn (case level
                  :debug  (.-debug  js/console)
                  :warn   (.-warn   js/console)
                  :error  (.-error  js/console)
                  (.-log js/console))]
     (if data
       (.call log-fn js/console prefix (clj->js data))
       (.call log-fn js/console prefix)))))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] :set-timeout
;;
;; Fire a re-frame dispatch after `ms` milliseconds.
;; Returns the timeout id via an optional :on-created dispatch so you can
;; cancel it later with js/clearTimeout if needed.
;;
;; Usage:
;;   {:set-timeout {:ms 3000 :dispatch [::events/dismiss-notification id]}}
;; ---------------------------------------------------------------------------

(rf/reg-fx
 :set-timeout
 (fn [{:keys [ms dispatch]}]
   (js/setTimeout
    (fn [] (rf/dispatch dispatch))
    (or ms 0))))

;; ---------------------------------------------------------------------------
;; Extension point
;; ---------------------------------------------------------------------------
;;
;; Common effects to add when building a real app:
;;
;; HTTP (via cljs-ajax or fetch):
;; (rf/reg-fx
;;  :http
;;  (fn [{:keys [method url params on-success on-failure]}]
;;    ...))
;;
;; WebSocket:
;; (rf/reg-fx
;;  :ws/send
;;  (fn [{:keys [channel payload]}]
;;    ...))
;;
;; Analytics / telemetry:
;; (rf/reg-fx
;;  :track
;;  (fn [{:keys [event props]}]
;;    ...))
