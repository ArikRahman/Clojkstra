(ns clojkstra.app.pages.datascript
  (:require
   [re-frame.core :as rf]
   [reagent.core :as r]
   [clojkstra.app.events :as events]
   [clojkstra.app.subs :as subs]))

(defn page []
  (let [input-val (r/atom "")]
    (fn []
      (let [names @(rf/subscribe [::subs/query
                                  '[:find [?n ...]
                                    :where [_ :person/name ?n]]])]
        [:div.page
         [:h1 "DataScript Demo"]
         [:div
          [:input {:type "text"
                   :value @input-val
                   :placeholder "Enter a name"
                   :on-change #(reset! input-val (-> % .-target .-value))}]
          [:button {:on-click (fn []
                                (when-not (empty? @input-val)
                                  (rf/dispatch [::events/transact [{:db/id -1
                                                                    :person/name @input-val}]])
                                  (reset! input-val "")))}
           "Add Name"]]
         [:h2 "Names in DB"]
         (if (empty? names)
           [:p "No names yet."]
           [:ul
            (for [n names]
              ^{:key n} [:li n])])]))))
