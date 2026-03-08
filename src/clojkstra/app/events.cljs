(ns clojkstra.app.events
  (:require
   [re-frame.core    :as rf]
   [clojkstra.app.db :as db]))

(def standard-interceptors [])

(rf/reg-event-db
 ::initialize-db
 (fn [_ _] db/default-db))

(rf/reg-event-db
 ::set-route
 (fn [db [_ matched]]
   (assoc db :current-route matched)))

(rf/reg-event-db
 ::set-loading
 (fn [db [_ loading?]]
   (assoc db :loading? loading?)))

(rf/reg-event-db
 ::set-error
 (fn [db [_ error]]
   (assoc db :error error)))

(rf/reg-event-db
 ::clear-error
 (fn [db _]
   (assoc db :error nil)))

(rf/reg-event-db
 ::toggle-feature
 (fn [db [_ flag]]
   (update-in db [:config :features flag] not)))

(rf/reg-event-db
 ::add-notification
 (fn [db [_ message]]
   (update db :notifications conj {:id (str (random-uuid)) :message message})))

(rf/reg-event-db
 ::dismiss-notification
 (fn [db [_ id]]
   (update db :notifications (fn [ns] (filterv #(not= (:id %) id) ns)))))
