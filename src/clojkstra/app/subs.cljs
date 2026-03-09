(ns clojkstra.app.subs
    (:require [re-frame.core :as rf]))

(rf/reg-sub ::current-route (fn [db _] (:current-route db)))
(rf/reg-sub ::loading?      (fn [db _] (:loading? db)))
(rf/reg-sub ::error         (fn [db _] (:error db)))
(rf/reg-sub ::config        (fn [db _] (:config db)))
(rf/reg-sub ::notifications (fn [db _] (:notifications db)))

(rf/reg-sub ::app-name      :<- [::config] (fn [c _] (:app-name c)))
(rf/reg-sub ::version       :<- [::config] (fn [c _] (:version c)))
(rf/reg-sub ::features      :<- [::config] (fn [c _] (:features c)))

(rf/reg-sub ::feature-enabled?
            :<- [::features]
            (fn [features [_ flag]] (boolean (get features flag))))

(rf/reg-sub ::current-handler
            :<- [::current-route]
            (fn [route _] (:handler route)))
