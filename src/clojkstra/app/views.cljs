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
       4. Add a nav-link entry in `nav-links`"
  (:require
   [re-frame.core                    :as rf]
   [clojkstra.app.subs               :as subs]
   [clojkstra.app.events             :as events]
   [clojkstra.app.components.ui      :as ui]
   [clojkstra.app.pages.home         :as home]
   [clojkstra.app.pages.about        :as about]
   [clojkstra.app.pages.example      :as example]))

;; ---------------------------------------------------------------------------
;; Navigation link definitions
;; [FRAMEWORK] Add an entry here whenever you add a new top-level page.
;; The :handler must match a key in routes/app-routes.
;; ---------------------------------------------------------------------------

(def ^:private nav-links
  [{:handler :home    :label "Home"}
   {:handler :about   :label "About"}
   {:handler :example :label "Example"}])

;; ---------------------------------------------------------------------------
;; Page dispatch
;; [FRAMEWORK] Maps a route handler keyword to its page component function.
;; Returns a hiccup vector so callers can use it directly in a parent vector.
;; ---------------------------------------------------------------------------

(defn- page-for-route
  "Returns the hiccup vector for the page matching `handler`.
   Falls through to a generic not-found view for unknown handlers."
  [handler]
  (case handler
    :home      [home/page]
    :about     [about/page]
    :example   [example/page]
    ;; --- not-found ---
    [:div {:class "flex flex-col items-center justify-center py-32 gap-4 text-center"}
     [:span {:class "text-6xl" :aria-hidden "true"} "🗺️"]
     [:h1 {:class "text-3xl font-bold text-gray-800"} "404 — Page Not Found"]
     [:p  {:class "text-gray-500 text-sm max-w-xs"}
      "The route you requested doesn't exist in this app."]
     [ui/button {:label    "Go Home"
                 :variant  :primary
                 :on-click #(rf/dispatch [::events/set-route {:handler :home :route-params {}}])}]]))

;; ---------------------------------------------------------------------------
;; Global loading overlay
;; [FRAMEWORK] Renders a full-screen spinner when :loading? is true in app-db.
;; Dispatch [::events/set-loading true/false] from async event handlers.
;; ---------------------------------------------------------------------------

(defn- loading-overlay []
  (when @(rf/subscribe [::subs/loading?])
    [:div
     {:class "fixed inset-0 z-50 flex items-center justify-center
              bg-white/70 backdrop-blur-sm"
      :aria-label "Loading"
      :role "status"}
     [ui/spinner {:size :lg :label "Loading…"}]]))

;; ---------------------------------------------------------------------------
;; Global error banner
;; [FRAMEWORK] Renders a sticky error alert when :error is non-nil in app-db.
;; Dispatch [::events/set-error "message"] to surface errors from any handler.
;; Dispatch [::events/clear-error] (or click ×) to dismiss.
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
;; Footer
;; [FRAMEWORK] Global footer rendered on every page.
;; Customise copy and links; the structure itself is framework.
;; ---------------------------------------------------------------------------

(defn- app-footer [app-name version]
  [:footer {:class "mt-auto border-t border-gray-200 bg-white"}
   [:div {:class "max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-6
                  flex flex-col sm:flex-row items-center justify-between gap-3"}
    [:p {:class "text-xs text-gray-400"}
     (str "© " (.getFullYear (js/Date.)) " " app-name " " version
          " — ClojureScript + re-frame starter template")]
    [:div {:class "flex items-center gap-4"}
     [:a {:class  "text-xs text-gray-400 hover:text-indigo-600 transition-colors"
          :href   "https://github.com"
          :target "_blank"
          :rel    "noopener noreferrer"}
      "GitHub"]
     [:a {:class  "text-xs text-gray-400 hover:text-indigo-600 transition-colors"
          :href   "https://day8.github.io/re-frame/"
          :target "_blank"
          :rel    "noopener noreferrer"}
      "re-frame docs"]
     [:a {:class  "text-xs text-gray-400 hover:text-indigo-600 transition-colors"
          :href   "https://shadow-cljs.org"
          :target "_blank"
          :rel    "noopener noreferrer"}
      "shadow-cljs"]]]])

;; ---------------------------------------------------------------------------
;; Page content wrapper
;; [FRAMEWORK] Adds consistent horizontal padding and max-width to every page.
;; ---------------------------------------------------------------------------

(defn- page-content [handler]
  [:main {:class "flex-1 w-full"
          :id    "main-content"
          :role  "main"}
   [:div {:class "max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10"}
    [page-for-route handler]]])

;; ---------------------------------------------------------------------------
;; Skip-to-content link (accessibility)
;; [FRAMEWORK] Visually hidden until focused via keyboard.
;; ---------------------------------------------------------------------------

(defn- skip-link []
  [:a {:class "sr-only focus:not-sr-only focus:fixed focus:top-2 focus:left-2
               focus:z-50 focus:px-4 focus:py-2 focus:bg-indigo-600 focus:text-white
               focus:rounded-lg focus:text-sm focus:font-medium"
       :href  "#main-content"}
   "Skip to content"])

;; ---------------------------------------------------------------------------
;; Notification stack
;; [FRAMEWORK] Renders the global notification toast list from app-db.
;; Positioned fixed bottom-right; sits above all other content.
;; ---------------------------------------------------------------------------

(defn- global-notifications []
  (let [notifications @(rf/subscribe [::subs/notifications])]
    [ui/notification-stack
     {:notifications notifications
      :on-dismiss    #(rf/dispatch [::events/dismiss-notification %])}]))

;; ---------------------------------------------------------------------------
;; App root
;; [FRAMEWORK] The single top-level component mounted by core/init.
;; This component is intentionally flat — it composes the shell from the
;; focused sub-components above.
;; ---------------------------------------------------------------------------

(defn app-root []
  (let [app-name      @(rf/subscribe [::subs/app-name])
        version       @(rf/subscribe [::subs/version])
        current-route @(rf/subscribe [::subs/current-handler])]
    [:div {:class "min-h-screen flex flex-col bg-gray-50 font-sans antialiased"}

     ;; Accessibility
     [skip-link]

     ;; Persistent navigation
     [ui/navbar
      {:app-name      app-name
       :current-route current-route
       :links         nav-links}]

     ;; Global error banner (sticky below navbar)
     [error-banner]

     ;; Page content
     [page-content current-route]

     ;; Footer
     [app-footer app-name version]

     ;; Overlays (rendered last so they sit on top)
     [loading-overlay]
     [global-notifications]]))
