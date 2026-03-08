(ns clojkstra.app.pages.about
  "About page for Clojkstra.
   [DEMO FILE] — explains what this template is and how it is structured.
   Replace with your own about / team / mission content when cloning."
  (:require
   [re-frame.core               :as rf]
   [clojkstra.app.subs          :as subs]
   [clojkstra.app.routes        :as routes]
   [clojkstra.app.components.ui :as ui]))

;; ---------------------------------------------------------------------------
;; Data
;; ---------------------------------------------------------------------------

(def ^:private file-map
  [{:file "core.cljs"          :kind :framework :desc "Entry point — init, hot-reload, mount"}
   {:file "db.cljs"            :kind :framework :desc "App-db schema and seeded default state"}
   {:file "events.cljs"        :kind :framework :desc "All re-frame event handlers"}
   {:file "subs.cljs"          :kind :framework :desc "All re-frame subscriptions"}
   {:file "routes.cljs"        :kind :framework :desc "bidi route table + pushy hash-routing"}
   {:file "effects.cljs"       :kind :framework :desc "Custom re-frame effect handlers"}
   {:file "utils.cljs"         :kind :framework :desc "Pure utility functions"}
   {:file "views.cljs"         :kind :framework :desc "App shell, layout, page dispatch"}
   {:file "components/ui.cljs" :kind :framework :desc "Reusable Reagent UI component library"}
   {:file "pages/home.cljs"    :kind :demo      :desc "Landing / explainer page"}
   {:file "pages/about.cljs"   :kind :demo      :desc "This page"}
   {:file "pages/example.cljs" :kind :demo      :desc "Scaffold template for new feature pages"}])

(def ^:private flow-code
  ";; User interaction
;;      │
;;      ▼
;; (rf/dispatch [::events/something payload])
;;      │
;;      ▼
;; reg-event-db / reg-event-fx   ← may also trigger :effects
;;      │
;;      ▼
;; app-db  (single immutable map — the only source of truth)
;;      │
;;      ▼
;; reg-sub  (layer 2: extract  →  layer 3: derive)
;;      │
;;      ▼
;; @(rf/subscribe [::subs/something])
;;      │
;;      ▼
;; Reagent component re-renders  →  DOM update")

;; ---------------------------------------------------------------------------
;; Sections
;; ---------------------------------------------------------------------------

(defn- kind-badge [kind]
  (case kind
    :framework [ui/badge {:label "framework" :variant :info}]
    :demo      [ui/badge {:label "demo"      :variant :warning}]
    nil))

(defn- file-map-section []
  [ui/card {:title    "File Map"
            :subtitle "Framework files are the reusable base. Demo files show usage — delete them freely."}
   [:table {:class "w-full text-sm mt-1"}
    [:thead
     [:tr {:class "border-b border-gray-700"}
      [:th {:class "text-left py-2 pr-4 font-semibold text-gray-400 w-52"} "File"]
      [:th {:class "text-left py-2 pr-4 font-semibold text-gray-400 w-28"} "Kind"]
      [:th {:class "text-left py-2 font-semibold text-gray-400"}           "Purpose"]]]
    [:tbody
     (for [{:keys [file kind desc]} file-map]
       ^{:key file}
       [:tr {:class "border-t border-gray-800 hover:bg-gray-800/50 transition-colors"}
        [:td {:class "py-2.5 pr-4"}
         [:code {:class "text-xs text-indigo-400 font-mono"} file]]
        [:td {:class "py-2.5 pr-4"}
         [kind-badge kind]]
        [:td {:class "py-2.5 text-gray-500"} desc]])]]])

(defn- data-flow-section []
  [ui/card {:title    "Data Flow"
            :subtitle "Every interaction follows the same one-way loop."}
   [ui/code-block {:code flow-code :lang "clojure"}]])

(defn- what-section [app-name version]
  [ui/card {:title (str "What is " app-name "?")}
   [:div {:class "flex flex-col gap-3 text-sm text-gray-400 leading-relaxed"}
    [:p
     (str app-name " (v" version ") is a ClojureScript + re-frame starter template. "
          "It is not a one-off application — it is a scaffold designed to be cloned, "
          "renamed, and built upon. The demo pages show how the framework is used; "
          "the framework files form a base you keep.")]
    [:p
     "The architecture enforces a strict separation: "
     [:span {:class "text-gray-200 font-medium"} "app-db"]
     " holds all state, "
     [:span {:class "text-gray-200 font-medium"} "events"]
     " are the only way to change it, "
     [:span {:class "text-gray-200 font-medium"} "subscriptions"]
     " are the only way views read it, and "
     [:span {:class "text-gray-200 font-medium"} "effects"]
     " handle everything that reaches outside the app."]
    [:p
     "This makes every state transition explicit, logged, and replayable — "
     "and makes the codebase easy to reason about as it grows."]]])

;; ---------------------------------------------------------------------------
;; Page root
;; ---------------------------------------------------------------------------

(defn page []
  (let [app-name @(rf/subscribe [::subs/app-name])
        version  @(rf/subscribe [::subs/version])]
    [:div {:class "flex flex-col gap-8"}

     [:header {:class "flex flex-col gap-3"}
      [ui/page-title
       {:title    (str "About " app-name)
        :subtitle "What it is, how it is structured, and why it works this way."}]
      [:div {:class "flex flex-wrap gap-2"}
       [ui/badge {:label "ClojureScript" :variant :info}]
       [ui/badge {:label "re-frame"      :variant :info}]
       [ui/badge {:label "shadow-cljs"   :variant :default}]
       [ui/badge {:label "GitHub Pages"  :variant :success}]]]

     [what-section app-name version]

     [:div {:class "grid grid-cols-1 lg:grid-cols-2 gap-6"}
      [file-map-section]
      [data-flow-section]]

     [:div {:class "flex gap-3"}
      [ui/button {:label    "← Home"
                  :variant  :ghost
                  :size     :sm
                  :on-click #(routes/navigate! :home)}]
      [ui/button {:label    "Example →"
                  :variant  :secondary
                  :size     :sm
                  :on-click #(routes/navigate! :example)}]]]))
