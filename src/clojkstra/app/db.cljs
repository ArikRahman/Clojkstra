(ns clojkstra.app.db)

(def default-db
     {:current-route {:handler :home :route-params {}}
      :loading?      false
      :error         nil
      :notifications []
      :config        {:app-name "Clojkstra"
                      :version  "0.1.0"
                      :features {:example-feature true}}})
