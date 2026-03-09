(ns clojkstra.app.pages.example
    "Example page template for Clojkstra.
   [DEMO FILE] — a scaffold for building new feature pages.
   Duplicate this file into pages/ and rename the namespace to add a new page.

   Checklist for adding a new page:
     1. Copy this file to pages/my_feature.cljs
     2. Rename the namespace to clojkstra.app.pages.my-feature
     3. Add a route in routes.cljs:  \"my-feature\" :my-feature
     4. Require the page in views.cljs and add a case in page-for-route
     5. Add a nav link in views.cljs nav-links vector
     6. Seed any new state in db.cljs under a clearly named top-level key
     7. Add events in events.cljs and subscriptions in subs.cljs
     8. Delete this comment block when the page is real

   Shows:
     - Local component state with reagent.core/atom
     - A controlled text input wired to local state
     - An example reg-event-fx pattern with the :log and :set-title effects
     - ui/alert, ui/card, ui/code-block, ui/badge, ui/page-title usage
     - A placeholder section for API / data-loading wiring"
    (:require
     [reagent.core                :as r]
     [re-frame.core               :as rf]
     [clojkstra.app.subs          :as subs]
     [clojkstra.app.events        :as events]
     [clojkstra.app.routes        :as routes]
     [clojkstra.app.components.ui :as ui]))

;; ---------------------------------------------------------------------------
;; Local state
;;
;; Use a Reagent ratom for UI-only ephemeral state (text input contents,
;; open/closed toggles, hover state, etc.) that does not need to survive
;; navigation or be shared across the app.
;;
;; Promote state to app-db when:
;;   - it needs to be persisted (localStorage, URL params)
;;   - it is read by more than one component
;;   - it is produced or consumed by a re-frame event
;; ---------------------------------------------------------------------------

(def ^:private local-state
     (r/atom {:input-value  ""
              :submitted?   false
              :show-detail? false}))

;; ---------------------------------------------------------------------------
;; [DEMO] Controlled input section
;; Demonstrates local ratom state without touching app-db.
;; ---------------------------------------------------------------------------

(defn- controlled-input-demo []
       (let [{:keys [input-value submitted?]} @local-state]
            [ui/card {:title    "Local State Demo"
                      :subtitle "A controlled input backed by a Reagent ratom — no app-db needed."}
             [:div {:class "flex flex-col gap-4"}
              [:div {:class "flex gap-3 items-end"}
               [:div {:class "flex-1"}
                [:label {:class "block text-sm font-medium text-gray-300 mb-1"
                         :for   "example-input"}
                 "Type something"]
                [:input {:id          "example-input"
                         :class       "w-full rounded-lg border border-gray-700 bg-gray-800 text-gray-100 px-3 py-2 text-sm
                                focus:outline-none focus:ring-2 focus:ring-indigo-500
                                focus:border-transparent transition placeholder-gray-600"
                         :type        "text"
                         :value       input-value
                         :placeholder "e.g. my new feature name"
                         :on-change   #(swap! local-state assoc
                                              :input-value (.. % -target -value)
                                              :submitted?  false)}]]
               [ui/button {:label    "Submit"
                           :variant  :primary
                           :size     :md
                           :disabled? (clojure.string/blank? input-value)
                           :on-click  (fn []
                                          (swap! local-state assoc :submitted? true)
                                ;; Demonstrate dispatching an event from a
                                ;; local interaction — adds a notification to
                                ;; the global notification stack in app-db.
                                          (rf/dispatch
                                           [::events/add-notification
                                            (str "Submitted: \"" input-value "\"")]))}]]
              (when submitted?
                    [ui/alert {:variant  :success
                               :title    "Submitted!"
                               :message  (str "You entered: \"" input-value "\"")
                               :on-close #(swap! local-state assoc :submitted? false :input-value "")}])]]))

;; ---------------------------------------------------------------------------
;; [DEMO] Feature flag gate
;; Demonstrates reading a feature flag from app-db via a subscription.
;; Gate any in-progress or experimental UI behind a flag in db.cljs.
;; ---------------------------------------------------------------------------

(defn- feature-flag-demo []
       (let [enabled? @(rf/subscribe [::subs/feature-enabled? :example-feature])]
            [ui/card {:title    "Feature Flag Demo"
                      :subtitle "Read a boolean flag from :config :features in app-db."}
             [:div {:class "flex flex-col gap-3"}
              [:div {:class "flex items-center gap-3"}
               [:span {:class "text-sm text-gray-400"} ":example-feature is currently"]
               (if enabled?
                   [ui/badge {:label "enabled" :variant :success}]
                   [ui/badge {:label "disabled" :variant :danger}])]
              (when enabled?
                    [:div {:class "rounded-lg bg-indigo-950 border border-indigo-800 p-4"}
                     [:p {:class "text-sm text-indigo-300"}
                      "✅ This content is only visible when the feature flag is on. "
                      "Toggle it in db.cljs under :config :features :example-feature."]])
              [ui/button {:label    (if enabled? "Disable flag" "Enable flag")
                          :variant  :ghost
                          :size     :sm
                          :on-click #(rf/dispatch [::events/toggle-feature :example-feature])}]]]))

;; ---------------------------------------------------------------------------
;; [DEMO] Effect dispatch example
;; Shows an event that uses reg-event-fx to combine a db mutation with
;; the :log, :set-title, and :set-timeout side-effects.
;; ---------------------------------------------------------------------------

(def ^:private effect-snippet
     ";; Example reg-event-fx in events.cljs:
;;
;; (rf/reg-event-fx
;;  ::load-my-feature
;;  standard-interceptors
;;  (fn [{:keys [db]} [_ params]]
;;    {:db         (assoc db :loading? true)
;;     :log        {:level :info
;;                  :msg   \"Loading my feature\"
;;                  :data  params}
;;     :set-title  \"My Feature – Clojkstra\"
;;     ;; Simulate async: clear loading after 1.5 s
;;     :set-timeout {:ms       1500
;;                   :dispatch [::events/set-loading false]}}))
;;
;; Dispatch it from a page on mount:
;;
;; (defn page []
;;   (r/with-let [_ (rf/dispatch [::events/load-my-feature {:id 42}])]
;;     [:div ...]))")

(defn- effect-example []
       [ui/card {:title    "Effect Handler Pattern"
                 :subtitle "reg-event-fx lets one event trigger both a db update and side effects."}
        [ui/code-block {:code effect-snippet :lang "clojure"}]])

;; ---------------------------------------------------------------------------
;; [DEMO] Data-loading placeholder
;; Shows the standard pattern for pages that fetch remote data.
;; Replace the simulated delay with a real :http effect in effects.cljs.
;; ---------------------------------------------------------------------------

(def ^:private loading-snippet
     ";; 1. In db.cljs — add initial state:
;;    :my-feature {:items [] :loading? false :error nil}
;;
;; 2. In events.cljs — fetch on page load:
;;    (rf/reg-event-fx
;;     ::fetch-items
;;     (fn [{:keys [db]} _]
;;       {:db   (assoc-in db [:my-feature :loading?] true)
;;        :http {:method     :get
;;               :url        \"/api/items\"
;;               :on-success [::items-loaded]
;;               :on-failure [::items-failed]}}))
;;
;; 3. In subs.cljs — expose to views:
;;    (rf/reg-sub ::items
;;      (fn [db _] (get-in db [:my-feature :items])))
;;
;; 4. In this page — trigger on mount with r/with-let:
;;    (defn page []
;;      (r/with-let [_ (rf/dispatch [::events/fetch-items])]
;;        (let [items   @(rf/subscribe [::subs/items])
;;              loading? @(rf/subscribe [::subs/loading?])]
;;          (if loading?
;;            [ui/spinner {}]
;;            [items-list items]))))")

(defn- data-loading-pattern []
       [ui/card {:title    "Data Loading Pattern"
                 :subtitle "Wire a page to a remote API using this convention."}
        [ui/code-block {:code loading-snippet :lang "clojure"}]])

;; ---------------------------------------------------------------------------
;; [DEMO] Toggle detail panel
;; Demonstrates a simple show/hide interaction with local state.
;; ---------------------------------------------------------------------------

(defn- toggle-detail []
       (let [open? (:show-detail? @local-state)]
            [:div {:class "flex flex-col gap-3"}
             [ui/button {:label    (if open? "Hide details ▲" "Show details ▼")
                         :variant  :secondary
                         :size     :sm
                         :on-click #(swap! local-state update :show-detail? not)}]
             (when open?
                   [:div {:class "rounded-lg border border-gray-700 bg-gray-800 p-4 text-sm text-gray-400
                      leading-relaxed"}
                    [:p "This panel is toggled by a Reagent ratom — no re-frame event needed."]
                    [:p {:class "mt-2"}
                     "Use local ratom state for transient UI concerns (open/closed, hover, tab index)."]
                    [:p {:class "mt-2"}
                     "Use app-db + events for anything that needs to survive navigation, be shared
          across components, or be replayed in re-frisk."]])]))

;; ---------------------------------------------------------------------------
;; Page root
;; ---------------------------------------------------------------------------

(defn page []
      (let [app-name @(rf/subscribe [::subs/app-name])]
           [:div {:class "flex flex-col gap-8"}

     ;; Header
            [:header {:class "flex flex-col gap-2"}
             [ui/page-title
              {:title    "Example Page"
               :subtitle (str "A scaffold for new " app-name " feature pages. "
                              "Duplicate this file and follow the checklist at the top.")}]
             [:div {:class "flex flex-wrap gap-2"}
              [ui/badge {:label "template" :variant :warning}]
              [ui/badge {:label "safe to delete" :variant :default}]]]

     ;; Checklist reminder
            [ui/alert
             {:variant :info
              :title   "This is a template file"
              :message "See the namespace docstring for a step-by-step checklist on
                 turning this into a real feature page."}]

     ;; Two-column demo grid
            [:div {:class "grid grid-cols-1 md:grid-cols-2 gap-6"}
             [controlled-input-demo]
             [feature-flag-demo]]

     ;; Toggle detail demo
            [ui/card {:title    "Local Toggle Pattern"
                      :subtitle "Show/hide a detail panel using Reagent local state."}
             [toggle-detail]]

            [ui/divider {:label "Code Patterns"}]

     ;; Code pattern cards
            [effect-example]
            [data-loading-pattern]

     ;; Navigation
            [:div {:class "flex flex-wrap gap-3 pt-2"}
             [ui/button {:label    "← Home"
                         :variant  :ghost
                         :size     :sm
                         :on-click #(routes/navigate! :home)}]
             [ui/button {:label    "About →"
                         :variant  :secondary
                         :size     :sm
                         :on-click #(routes/navigate! :about)}]]]))
