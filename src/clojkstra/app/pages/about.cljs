(ns clojkstra.app.pages.about
  "About page for Clojkstra.
   [DEMO FILE] — static informational page about the template.
   Replace this content with your app's about / team / mission page.

   Shows:
     - ::subs/app-name and ::subs/version subscriptions
     - ui/card, ui/badge, ui/code-block, ui/page-title usage
     - static content layout pattern"
  (:require
   [re-frame.core               :as rf]
   [clojkstra.app.subs          :as subs]
   [clojkstra.app.routes        :as routes]
   [clojkstra.app.components.ui :as ui]))

;; ---------------------------------------------------------------------------
;; [DEMO] Tech stack section
;; ---------------------------------------------------------------------------

(def ^:private stack-items
  [{:name "ClojureScript" :role "Language"      :url "https://clojurescript.org"}
   {:name "re-frame"      :role "State / Events" :url "https://github.com/day8/re-frame"}
   {:name "Reagent"       :role "React wrapper"  :url "https://reagent-project.github.io"}
   {:name "shadow-cljs"   :role "Build tool"     :url "https://shadow-cljs.org"}
   {:name "bidi"          :role "Routing"         :url "https://github.com/juxt/bidi"}
   {:name "pushy"         :role "History"         :url "https://github.com/kibu-australia/pushy"}
   {:name "Bun"           :role "JS runtime"      :url "https://bun.sh"}
   {:name "re-frisk"      :role "Dev inspector"   :url "https://github.com/flexsurfer/re-frisk"}])

(defn- stack-row [{:keys [name role url]}]
  [:tr {:class "border-t border-gray-100 hover:bg-gray-50 transition-colors"}
   [:td {:class "py-3 pr-4"}
    [:a {:class  "text-sm font-medium text-indigo-600 hover:underline"
         :href   url
         :target "_blank"
         :rel    "noopener noreferrer"}
     name]]
   [:td {:class "py-3 text-sm text-gray-500"} role]])

(defn- tech-stack []
  [ui/card {:title    "Tech Stack"
            :subtitle "Every dependency was chosen to be minimal, stable, and FOSS-friendly."}
   [:table {:class "w-full"}
    [:tbody
     (for [item stack-items]
       ^{:key (:name item)}
       [stack-row item])]]])

;; ---------------------------------------------------------------------------
;; [DEMO] File map section
;; ---------------------------------------------------------------------------

(def ^:private file-map
  [{:file "core.cljs"           :kind :framework :desc "Entry point — init, hot-reload, mount"}
   {:file "db.cljs"             :kind :framework :desc "App-db schema and default state"}
   {:file "events.cljs"         :kind :framework :desc "All re-frame event handlers"}
   {:file "subs.cljs"           :kind :framework :desc "All re-frame subscriptions"}
   {:file "routes.cljs"         :kind :framework :desc "bidi route table + pushy wiring"}
   {:file "effects.cljs"        :kind :framework :desc "Custom re-frame effect handlers"}
   {:file "utils.cljs"          :kind :framework :desc "Pure utility functions"}
   {:file "views.cljs"          :kind :framework :desc "App shell, layout, and page dispatch"}
   {:file "components/ui.cljs"  :kind :framework :desc "Reusable Reagent UI component library"}
   {:file "pages/home.cljs"     :kind :demo      :desc "Home page — counter + notification demo"}
   {:file "pages/about.cljs"    :kind :demo      :desc "About page — this file"}
   {:file "pages/example.cljs"  :kind :demo      :desc "Example page template for new features"}])

(defn- kind-badge [kind]
  (case kind
    :framework [ui/badge {:label "framework" :variant :info}]
    :demo      [ui/badge {:label "demo"      :variant :warning}]
    nil))

(defn- file-map-table []
  [ui/card {:title    "File Map"
            :subtitle "Framework files form the reusable base. Demo files are safe to delete."}
   [:table {:class "w-full text-sm"}
    [:thead
     [:tr {:class "border-b border-gray-200"}
      [:th {:class "text-left py-2 pr-4 font-semibold text-gray-700"} "File"]
      [:th {:class "text-left py-2 pr-4 font-semibold text-gray-700"} "Kind"]
      [:th {:class "text-left py-2 font-semibold text-gray-700"}      "Purpose"]]]
    [:tbody
     (for [{:keys [file kind desc]} file-map]
       ^{:key file}
       [:tr {:class "border-t border-gray-100 hover:bg-gray-50 transition-colors"}
        [:td {:class "py-2.5 pr-4"}
         [:code {:class "text-xs text-indigo-600 font-mono"} file]]
        [:td {:class "py-2.5 pr-4"}
         [kind-badge kind]]
        [:td {:class "py-2.5 text-gray-500"} desc]])]]])

;; ---------------------------------------------------------------------------
;; [DEMO] re-frame data flow diagram (text)
;; ---------------------------------------------------------------------------

(def ^:private data-flow-snippet
  ";; The re-frame data flow in one picture:
;;
;;   User interaction
;;        │
;;        ▼
;;   (rf/dispatch [::events/some-event payload])
;;        │
;;        ▼
;;   reg-event-db / reg-event-fx   ← reads :db, may trigger :effects
;;        │
;;        ▼
;;   app-db (single immutable map)
;;        │
;;        ▼
;;   reg-sub  (layer 2 → extract, layer 3 → derive)
;;        │
;;        ▼
;;   @(rf/subscribe [::subs/some-value])
;;        │
;;        ▼
;;   Reagent component re-renders  →  DOM update")

(defn- data-flow-section []
  [ui/card {:title "Data Flow"}
   [ui/code-block {:code data-flow-snippet :lang "clojure"}]])

;; ---------------------------------------------------------------------------
;; Page root
;; ---------------------------------------------------------------------------

(defn page []
  (let [app-name @(rf/subscribe [::subs/app-name])
        version  @(rf/subscribe [::subs/version])]
    [:div {:class "flex flex-col gap-8"}

     ;; Header
     [:header {:class "flex flex-col gap-3"}
      [ui/page-title
       {:title    (str "About " app-name)
        :subtitle (str "Version " version
                       " — a re-frame starter template designed for cloning.")}]
      [:div {:class "flex flex-wrap gap-2"}
       [ui/badge {:label "ClojureScript" :variant :info}]
       [ui/badge {:label "re-frame"      :variant :info}]
       [ui/badge {:label "shadow-cljs"   :variant :default}]
       [ui/badge {:label "GitHub Pages ready" :variant :success}]]]

     ;; What is this?
     [ui/card {:title "What is Clojkstra?"}
      [:div {:class "flex flex-col gap-3 text-sm text-gray-600 leading-relaxed"}
       [:p
        (str app-name " is not a one-off application. It is a reusable starter template,
        engine, and vehicle for future ClojureScript projects. Clone it, rename the
        namespace root, delete the demo pages, and you have a production-ready SPA
        scaffold in minutes.")]
       [:p
        "Every file is clearly labelled "
        [:strong "framework"] " (keep it) or "
        [:strong "demo"] " (delete it). The architecture follows idiomatic re-frame
        separation between app-db, event handlers, subscriptions, view components,
        and effect handlers."]]]

     ;; Two-column layout for stack + data flow
     [:div {:class "grid grid-cols-1 lg:grid-cols-2 gap-6"}
      [tech-stack]
      [data-flow-section]]

     ;; Full-width file map
     [file-map-table]

     ;; Back link
     [:div {:class "flex gap-3"}
      [ui/button {:label    "← Back to Home"
                  :variant  :ghost
                  :size     :sm
                  :on-click #(routes/navigate! :home)}]
      [ui/button {:label    "Example Page →"
                  :variant  :secondary
                  :size     :sm
                  :on-click #(routes/navigate! :example)}]]]))
