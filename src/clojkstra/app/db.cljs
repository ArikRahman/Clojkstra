(ns clojkstra.app.db
  (:require [datascript.core :as d]))

(def default-db
  {:current-route {:handler :home :route-params {}}
   :loading?      false
   :error         nil
   :notifications []
   :datascript/db (d/empty-db)
   :config        {:app-name "Clojkstra"
                   :version  "0.1.0"
                   :features {:example-feature true}}})
