(ns clojkstra.app.views
  "App shell, global layout, and page dispatch for Clojkstra.
   [FRAMEWORK FILE] — the top-level Reagent component tree.

   Responsibilities:
     - Render the persistent app shell (navbar, notification stack, footer)
     - Subscribe to :current-handler and dispatch to the correct page component
     - Render global loading and error overlays

   Extension point:
     To add a new page:
       1. Add a route in routes.cljs
       2. Create a page component in pages/
       3. Require it here and add a case in `page-for-route`
       4. Add a nav link entry in `nav-links`"
  (:require
   [re-frame.core               :as rf]
   [clojkstra.app.subs          :as subs]
   [clojkstra.app.events        :as events]
   [clojkstra.app.routes        :as routes]
   [clojkstra.app.components.ui :as ui]
   [clojkstra.app.pages.home    :as home]
   [clojkstra.app.pages.about   :as about]
   [clojkstra.app.pages.example :as example]))

;; ---------------------------------------------------------------------------
;; Navigation link definitions
;; ---------------------------------------------------------------------------

(def ^:private nav-links
  [{:handler :home  :label "Home"}
   {:handler :about :label "About"}])

;; ---------------------------------------------------------------------------
;; Page dispatch
;; ---------------------------------------------------------------------------

(defn- page-for-route [handler]
  (case handler
    :home      [home/page]
    :about     [about/page]
    :example   [example/page]
    [:div {:class "flex flex-col items-center justify-center py-32 gap-4 text-center"}
     [:span {:class "text-6xl" :aria-hidden "true"} "🗺️"]
     [:h1 {:class "text-3xl font-bold text-gray-100"} "404 — Page Not Found"]
     [:p  {:class "text-gray-500 text-sm max-w-xs"}
      "The route you requested doesn't exist in this app."]
     [ui/button {:label    "Go Home"
                 :variant  :primary
                 :on-click #(rf/dispatch [::events/set-route {:handler :home :route-params {}}])}]]))

;; ---------------------------------------------------------------------------
;; Global loading overlay
;; ---------------------------------------------------------------------------

(defn- loading-overlay []
  (when @(rf/subscribe [::subs/loading?])
    [:div
     {:class "fixed inset-0 z-50 flex items-center justify-center bg-gray-950/80 backdrop-blur-sm"
      :aria-label "Loading"
      :role "status"}
     [ui/spinner {:size :lg :label "Loading…"}]]))

;; ---------------------------------------------------------------------------
;; Global error banner
;; ---------------------------------------------------------------------------

(defn- error-banner []
  (when-let [error @(rf/subscribe [::subs/error])]
    [:div {:class "sticky top-16 z-40 px-4 sm:px-6 lg:px-8 pt-3"}
     [:div {:class "max-w-5xl mx-auto"}
      [ui/alert {:variant  :error
                 :title    "Something went wrong"
                 :message  (str error)
                 :on-close #(rf/dispatch [::events/clear-error])}]]]))

;; ---------------------------------------------------------------------------
;; Navbar
;; ---------------------------------------------------------------------------

(defn- nav-link [{:keys [handler label active]}]
  [:a
   {:class    (str "px-3 py-2 rounded-md text-sm font-medium transition-colors duration-150 "
                   (if active
                     "bg-indigo-600 text-white"
                     "text-gray-400 hover:text-indigo-400 hover:bg-gray-800"))
    :href     (str "#" (routes/path-for handler))
    :on-click (fn [e]
                (.preventDefault e)
                (rf/dispatch [::events/set-route {:handler handler :route-params {}}]))}
   label])

(defn- app-navbar [app-name current-route]
  [:nav {:class "bg-gray-900 border-b border-gray-800 sticky top-0 z-50"}
   [:div {:class "max-w-5xl mx-auto px-4 sm:px-6 lg:px-8"}
    [:div {:class "flex items-center justify-between h-16"}
     [:a {:class    "text-xl font-bold text-indigo-400 tracking-tight"
          :href     "#/"
          :on-click (fn [e]
                      (.preventDefault e)
                      (rf/dispatch [::events/set-route {:handler :home :route-params {}}]))}
      app-name]
     [:div {:class "flex items-center gap-1"}
      (for [{:keys [handler label]} nav-links]
        ^{:key handler}
        [nav-link {:handler handler :label label :active (= current-route handler)}])]]]])

;; ---------------------------------------------------------------------------
;; Footer
;; ---------------------------------------------------------------------------

(defn- app-footer [app-name version]
  [:footer {:class "mt-auto border-t border-gray-800 bg-gray-900"}
   [:div {:class "max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-6
                  flex flex-col sm:flex-row items-center justify-between gap-3"}
    [:p {:class "text-xs text-gray-600"}
     (str "© " (.getFullYear (js/Date.)) " " app-name " " version
          " — ClojureScript + re-frame starter template")]
    [:div {:class "flex items-center gap-4"}
     [:a {:class  "text-xs text-gray-600 hover:text-indigo-400 transition-colors"
          :href   "https://github.com/ArikRahman/Clojkstra"
          :target "_blank"
          :rel    "noopener noreferrer"}
      "GitHub"]
     [:a {:class  "text-xs text-gray-600 hover:text-indigo-400 transition-colors"
          :href   "https://github.com/day8/re-frame"
          :target "_blank"
          :rel    "noopener noreferrer"}
      "re-frame"]
     [:a {:class  "text-xs text-gray-600 hover:text-indigo-400 transition-colors"
          :href   "https://shadow-cljs.org"
          :target "_blank"
          :rel    "noopener noreferrer"}
      "shadow-cljs"]
     [:a {:class  "text-xs text-gray-600 hover:text-indigo-400 transition-colors"
          :href   "https://clojurescript.org"
          :target "_blank"
          :rel    "noopener noreferrer"}
      "ClojureScript"]]]])

;; ---------------------------------------------------------------------------
;; Skip link
;; ---------------------------------------------------------------------------

(defn- skip-link []
  [:a {:class "sr-only focus:not-sr-only focus:fixed focus:top-2 focus:left-2
               focus:z-50 focus:px-4 focus:py-2 focus:bg-indigo-600 focus:text-white
               focus:rounded-lg focus:text-sm focus:font-medium"
       :href  "#main-content"}
   "Skip to content"])

;; ---------------------------------------------------------------------------
;; Notification stack
;; ---------------------------------------------------------------------------

(defn- global-notifications []
  (let [notifications @(rf/subscribe [::subs/notifications])]
    [ui/notification-stack
     {:notifications notifications
      :on-dismiss    #(rf/dispatch [::events/dismiss-notification %])}]))

;; ---------------------------------------------------------------------------
;; App root
;; ---------------------------------------------------------------------------

(defn app-root []
  (let [app-name      @(rf/subscribe [::subs/app-name])
        version       @(rf/subscribe [::subs/version])
        current-route @(rf/subscribe [::subs/current-handler])]
    [:div {:class "min-h-screen flex flex-col bg-gray-950 text-gray-100 font-sans antialiased"}
     [skip-link]
     [app-navbar app-name current-route]
     [error-banner]
     [:main {:class "flex-1 w-full" :id "main-content" :role "main"}
      [:div {:class "max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10"}
       [page-for-route current-route]]]
     [app-footer app-name version]
     [loading-overlay]
     [global-notifications]]))
