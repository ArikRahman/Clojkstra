(ns clojkstra.app.pages.home
  "Home page for Clojkstra.
   [DEMO FILE] — demonstrates re-frame event dispatch and subscriptions.
   Safe to gut and replace with your own landing page content.

   Shows:
     - ::subs/counter + ::events/increment|decrement|reset-counter
     - ::events/add-notification dispatch
     - links to other pages"
  (:require
   [re-frame.core               :as rf]
   [clojkstra.app.subs          :as subs]
   [clojkstra.app.events        :as events]
   [clojkstra.app.routes        :as routes]
   [clojkstra.app.components.ui :as ui]))

;; ---------------------------------------------------------------------------
;; [DEMO] Counter widget
;; Demonstrates the simplest possible event-dispatch + subscription loop.
;; ---------------------------------------------------------------------------

(defn- counter-demo []
  (let [label @(rf/subscribe [::subs/counter-label])
        count @(rf/subscribe [::subs/counter])]
    [ui/card {:title    "Event Dispatch Demo"
              :subtitle "Increment, decrement, and reset a counter stored in app-db."}
     [:div {:class "flex flex-col items-center gap-6 py-2"}
      [:span {:class "text-5xl font-bold text-indigo-600 tabular-nums"
              :aria-live "polite"
              :aria-label label}
       count]
      [:div {:class "flex items-center gap-3"}
       [ui/button {:label    "−"
                   :variant  :secondary
                   :size     :lg
                   :on-click #(rf/dispatch [::events/decrement-counter])}]
       [ui/button {:label    "+"
                   :variant  :primary
                   :size     :lg
                   :on-click #(rf/dispatch [::events/increment-counter])}]]
      [ui/button {:label    "Reset"
                  :variant  :ghost
                  :size     :sm
                  :on-click #(rf/dispatch [::events/reset-counter])}]]]))

;; ---------------------------------------------------------------------------
;; [DEMO] Notification trigger
;; Demonstrates transient UI state flowing through app-db.
;; ---------------------------------------------------------------------------

(defn- notification-demo []
  [ui/card {:title    "Notification Demo"
            :subtitle "Dispatch an event that adds a toast notification to app-db."}
   [:div {:class "flex flex-col gap-3"}
    [ui/button {:label    "Send a notification"
                :variant  :secondary
                :on-click #(rf/dispatch
                            [::events/add-notification
                             (str "Hello from the home page! 👋  "
                                  "Sent at " (.toLocaleTimeString (js/Date.)))])}]
    [:p {:class "text-xs text-gray-400"}
     "Notifications appear in the bottom-right corner and can be dismissed individually."]]])

;; ---------------------------------------------------------------------------
;; [DEMO] Quick-start cards
;; Static content pointing contributors at the key extension points.
;; ---------------------------------------------------------------------------

(def ^:private quick-start-items
  [{:icon  "📦"
    :title "app-db"
    :body  "Seed your domain state in db.cljs. Every key should have a comment."
    :file  "src/clojkstra/app/db.cljs"}
   {:icon  "⚡"
    :title "Events"
    :body  "Register new event handlers in events.cljs. Use reg-event-fx for side effects."
    :file  "src/clojkstra/app/events.cljs"}
   {:icon  "🔍"
    :title "Subscriptions"
    :body  "Derive view data in subs.cljs. Layer 2 extracts; layer 3 computes."
    :file  "src/clojkstra/app/subs.cljs"}
   {:icon  "🛣️"
    :title "Routes"
    :body  "Add new routes to app-routes in routes.cljs and a page component in pages/."
    :file  "src/clojkstra/app/routes.cljs"}
   {:icon  "🧩"
    :title "Components"
    :body  "Build reusable, prop-driven Reagent components in components/ui.cljs."
    :file  "src/clojkstra/app/components/ui.cljs"}
   {:icon  "🔧"
    :title "Effects"
    :body  "Register custom side-effect handlers in effects.cljs (HTTP, storage, etc.)."
    :file  "src/clojkstra/app/effects.cljs"}])

(defn- quick-start-card [{:keys [icon title body file]}]
  [:div {:class "bg-white border border-gray-200 rounded-xl p-5 flex flex-col gap-2
                 hover:border-indigo-300 hover:shadow-sm transition-all duration-150"}
   [:div {:class "flex items-center gap-2"}
    [:span {:class "text-2xl" :aria-hidden "true"} icon]
    [:h3 {:class "font-semibold text-gray-900 text-sm"} title]]
   [:p {:class "text-sm text-gray-500 leading-relaxed"} body]
   [:code {:class "text-xs text-indigo-500 font-mono mt-auto pt-1"} file]])

(defn- quick-start-grid []
  [:section {:aria-labelledby "qs-heading"}
   [:h2 {:id "qs-heading" :class "text-xl font-semibold text-gray-800 mb-4"}
    "Extension Points"]
   [:div {:class "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4"}
    (for [item quick-start-items]
      ^{:key (:title item)}
      [quick-start-card item])]])

;; ---------------------------------------------------------------------------
;; [DEMO] Navigation links
;; ---------------------------------------------------------------------------

(defn- page-links []
  [:div {:class "flex flex-wrap gap-3 items-center"}
   [:span {:class "text-sm text-gray-500"} "Explore the demo:"]
   [ui/button {:label    "About →"
               :variant  :secondary
               :size     :sm
               :on-click #(routes/navigate! :about)}]
   [ui/button {:label    "Example Page →"
               :variant  :ghost
               :size     :sm
               :on-click #(routes/navigate! :example)}]])

;; ---------------------------------------------------------------------------
;; Page root
;; ---------------------------------------------------------------------------

(defn page []
  (let [app-name @(rf/subscribe [::subs/app-name])]
    [:div {:class "flex flex-col gap-10"}

     ;; Hero
     [:header {:class "flex flex-col gap-3"}
      [ui/page-title
       {:title    (str "Welcome to " app-name)
        :subtitle "A ClojureScript + re-frame starter template. Clone it, rename it, ship it."}]
      [page-links]]

     ;; Demo widgets
     [:section {:class "grid grid-cols-1 md:grid-cols-2 gap-6"
                :aria-label "Interactive demos"}
      [counter-demo]
      [notification-demo]]

     [ui/divider {:label "How this template is organised"}]

     ;; Extension point guide
     [quick-start-grid]]))
