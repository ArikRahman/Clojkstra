(ns clojkstra.app.subs
  "Re-frame subscriptions for Clojkstra.
   [FRAMEWORK FILE] — core subscriptions that views depend on.

   Naming convention:
     Layer 2 (extract)  — pull a raw slice of app-db.
     Layer 3 (derive)   — compute/transform from layer-2 subs.

   [FRAMEWORK] subs:
     ::current-route, ::loading?, ::error, ::config,
     ::app-name, ::theme, ::feature-enabled?

   [DEMO] subs (safe to delete):
     ::counter, ::notifications

   Extension point:
     Add your domain subscriptions below the DEMO section."
  (:require
   [re-frame.core :as rf]))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] Layer 2 — raw extractions
;; ---------------------------------------------------------------------------

(rf/reg-sub
 ::current-route
 (fn [db _]
   (:current-route db)))

(rf/reg-sub
 ::loading?
 (fn [db _]
   (:loading? db)))

(rf/reg-sub
 ::error
 (fn [db _]
   (:error db)))

(rf/reg-sub
 ::config
 (fn [db _]
   (:config db)))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] Layer 3 — derived from ::config
;; ---------------------------------------------------------------------------

(rf/reg-sub
 ::app-name
 :<- [::config]
 (fn [config _]
   (:app-name config)))

(rf/reg-sub
 ::version
 :<- [::config]
 (fn [config _]
   (:version config)))

(rf/reg-sub
 ::theme
 :<- [::config]
 (fn [config _]
   (:theme config)))

(rf/reg-sub
 ::features
 :<- [::config]
 (fn [config _]
   (:features config)))

(rf/reg-sub
 ::feature-enabled?
 :<- [::features]
 (fn [features [_ flag]]
   "Check whether a feature flag is enabled.
    Usage: @(rf/subscribe [::subs/feature-enabled? :my-flag])"
   (boolean (get features flag))))

;; ---------------------------------------------------------------------------
;; [FRAMEWORK] Layer 3 — derived from ::current-route
;; ---------------------------------------------------------------------------

(rf/reg-sub
 ::current-handler
 :<- [::current-route]
 (fn [route _]
   (:handler route)))

(rf/reg-sub
 ::route-params
 :<- [::current-route]
 (fn [route _]
   (:route-params route)))

;; ---------------------------------------------------------------------------
;; [DEMO] Counter — mirrors ::increment-counter / ::decrement-counter events
;; Safe to delete when building a real app.
;; ---------------------------------------------------------------------------

(rf/reg-sub
 ::counter
 (fn [db _]
   (:counter db)))

(rf/reg-sub
 ::counter-label
 :<- [::counter]
 (fn [counter _]
   "Derived display string for the counter value."
   (str "Count: " counter)))

;; ---------------------------------------------------------------------------
;; [DEMO] Notifications — mirrors ::add-notification / ::dismiss-notification
;; Safe to delete when building a real app.
;; ---------------------------------------------------------------------------

(rf/reg-sub
 ::notifications
 (fn [db _]
   (:notifications db)))

(rf/reg-sub
 ::has-notifications?
 :<- [::notifications]
 (fn [notifs _]
   (seq notifs)))

;; ---------------------------------------------------------------------------
;; Extension point
;; ---------------------------------------------------------------------------
;;
;; Add your domain subscriptions here, e.g.:
;;
;; (rf/reg-sub
;;  ::current-user
;;  (fn [db _]
;;    (:current-user db)))
;;
;; (rf/reg-sub
;;  ::user-display-name
;;  :<- [::current-user]
;;  (fn [user _]
;;    (or (:display-name user) (:email user) "Anonymous")))
