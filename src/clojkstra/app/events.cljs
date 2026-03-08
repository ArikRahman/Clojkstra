(ns clojkstra.app.events
  "Re-frame event handlers for Clojkstra.
   [FRAMEWORK FILE] — core lifecycle and routing events.

   Naming convention:
     ::initialize-db       — app boot
     ::set-route           — router writes current location
     ::set-loading         — async guard
     ::set-error           — global error signal
     ::clear-error         — dismiss error

   [DEMO] events are clearly marked and safe to delete:
     ::increment-counter
     ::decrement-counter
     ::reset-counter
     ::add-notification
     ::dismiss-notification

   Extension point:
     Add your domain events below the DEMO section.
     For side-effectful events (HTTP, localStorage, etc.) see effects.cljs."
  (:require
   [re-frame.core  :as rf]
   [clojkstra.app.db :as db]))

;; ---------------------------------------------------------------------------
;; Interceptors
;; ---------------------------------------------------------------------------

(def check-spec-interceptor
  "Optionally validate app-db shape after every event in dev builds.
   Swap the no-op below for a cljs.spec assertion when you add a spec."
  (rf/->interceptor
   :id     :check-spec
   :after  (fn [ctx] ctx)))   ; <- replace body with spec check if desired

(def standard-interceptors
  "Attach to every event handler that mutates app-db."
  [check-spec-interceptor])

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] Lifecycle
;; ---------------------------------------------------------------------------

(rf/reg-event-db
 ::initialize-db
 standard-interceptors
 (fn [_ _]
   "Reset app-db to the canonical default state on first load.
    Called synchronously in core/init before anything renders."
   db/default-db))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] Routing
;; ---------------------------------------------------------------------------

(rf/reg-event-db
 ::set-route
 standard-interceptors
 (fn [db [_ matched]]
   "Written by the pushy router whenever the URL changes.
    `matched` is the map returned by bidi/match-route."
   (assoc db :current-route matched)))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] Async / loading state
;; ---------------------------------------------------------------------------

(rf/reg-event-db
 ::set-loading
 standard-interceptors
 (fn [db [_ loading?]]
   (assoc db :loading? loading?)))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] Error handling
;; ---------------------------------------------------------------------------

(rf/reg-event-db
 ::set-error
 standard-interceptors
 (fn [db [_ error]]
   (assoc db :error error)))

(rf/reg-event-db
 ::clear-error
 standard-interceptors
 (fn [db _]
   (assoc db :error nil)))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] Config / feature flags
;; ---------------------------------------------------------------------------

(rf/reg-event-db
 ::set-config
 standard-interceptors
 (fn [db [_ path value]]
   "Update a path inside :config at runtime.
    Example: (rf/dispatch [::events/set-config [:theme :color-primary] \"#FF0000\"])"
   (assoc-in db (into [:config] path) value)))

(rf/reg-event-db
 ::toggle-feature
 standard-interceptors
 (fn [db [_ flag]]
   "Toggle a boolean feature flag by key.
    Example: (rf/dispatch [::events/toggle-feature :debug-panel])"
   (update-in db [:config :features flag] not)))

;; ---------------------------------------------------------------------------
;; [DEMO] Counter — demonstrates simple event dispatch
;; Safe to delete when building a real app.
;; ---------------------------------------------------------------------------

(rf/reg-event-db
 ::increment-counter
 standard-interceptors
 (fn [db [_ amount]]
   (update db :counter + (or amount 1))))

(rf/reg-event-db
 ::decrement-counter
 standard-interceptors
 (fn [db [_ amount]]
   (update db :counter - (or amount 1))))

(rf/reg-event-db
 ::reset-counter
 standard-interceptors
 (fn [db _]
   (assoc db :counter 0)))

;; ---------------------------------------------------------------------------
;; [DEMO] Notifications — demonstrates transient UI state in app-db
;; Safe to delete when building a real app.
;; ---------------------------------------------------------------------------

(rf/reg-event-db
 ::add-notification
 standard-interceptors
 (fn [db [_ message]]
   (let [id (str (random-uuid))]
     (update db :notifications conj {:id id :message message}))))

(rf/reg-event-db
 ::dismiss-notification
 standard-interceptors
 (fn [db [_ id]]
   (update db :notifications (fn [ns] (filterv #(not= (:id %) id) ns)))))

;; ---------------------------------------------------------------------------
;; Extension point
;; ---------------------------------------------------------------------------
;;
;; Add your app's domain events here, e.g.:
;;
;; (rf/reg-event-fx
;;  ::fetch-user
;;  standard-interceptors
;;  (fn [{:keys [db]} [_ user-id]]
;;    {:db       (assoc db :loading? true)
;;     :http-xhrio { ... }}))  ; see effects.cljs for custom effect handlers
