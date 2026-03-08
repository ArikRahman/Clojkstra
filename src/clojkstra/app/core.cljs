(ns clojkstra.app.core
  (:require
   [reagent.dom          :as rdom]
   [re-frame.core        :as rf]
   [clojkstra.app.events :as events]
   [clojkstra.app.subs   :as subs]
   [clojkstra.app.routes :as routes]
   [clojkstra.app.views  :as views]))

(defn- mount-root []
  (rf/clear-subscription-cache!)
  (rdom/render [views/app-root] (.getElementById js/document "app")))

(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (routes/start!)
  (mount-root))

(defn on-reload []
  (mount-root))
