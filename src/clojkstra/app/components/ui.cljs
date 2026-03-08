(ns clojkstra.app.components.ui
  "Reusable UI component library for Clojkstra.
   [FRAMEWORK FILE] — generic, stateless Reagent components.

   All components here are:
     - Pure functions of their props (no re-frame subscriptions).
     - Styleable via an optional `class` prop and inline `:style` overrides.
     - Documented with their prop signatures in the docstring.

   Components:
     button        — primary/secondary/ghost/danger variants
     badge         — small label pill
     card          — surface container with optional header/footer
     spinner       — loading indicator
     alert         — info/success/warning/error banners
     navbar        — top navigation bar shell
     nav-link      — individual navigation link item
     page-title    — standardised h1 + subtitle block
     divider       — horizontal rule
     code-block    — monospace pre/code display
     notification  — dismissible toast-style notification"
  (:require
   [clojkstra.app.routes :as routes]))

;; ---------------------------------------------------------------------------
;; Internal helpers
;; ---------------------------------------------------------------------------

(defn- classes [& cs]
  (->> cs (filter identity) (clojure.string/join " ")))

;; ---------------------------------------------------------------------------
;; button
;;
;; Props:
;;   :label     (string)   — button text                        [required]
;;   :on-click  (fn)       — click handler                      [required]
;;   :variant   (keyword)  — :primary | :secondary | :ghost | :danger
;;   :size      (keyword)  — :sm | :md | :lg   default: :md
;;   :disabled? (boolean)  — disables interaction
;;   :class     (string)   — extra CSS classes
;;   :type      (string)   — HTML button type, default "button"
;; ---------------------------------------------------------------------------

(def ^:private button-base
  "inline-flex items-center justify-center font-medium rounded-lg
   transition-colors duration-150 focus:outline-none focus:ring-2
   focus:ring-offset-2 focus:ring-offset-gray-950
   disabled:opacity-40 disabled:cursor-not-allowed")

(def ^:private button-variants
  {:primary   "bg-indigo-600 text-white hover:bg-indigo-500 focus:ring-indigo-500"
   :secondary "bg-gray-800 text-gray-100 border border-gray-700 hover:bg-gray-700 focus:ring-gray-600"
   :ghost     "bg-transparent text-gray-400 hover:text-gray-100 hover:bg-gray-800 focus:ring-gray-600"
   :danger    "bg-red-700 text-white hover:bg-red-600 focus:ring-red-500"})

(def ^:private button-sizes
  {:sm "px-3 py-1.5 text-sm"
   :md "px-4 py-2 text-sm"
   :lg "px-6 py-3 text-base"})

(defn button
  [{:keys [label on-click variant size disabled? class type]
    :or   {variant :primary size :md type "button"}}]
  [:button
   {:type     type
    :class    (classes button-base
                       (get button-variants variant (button-variants :primary))
                       (get button-sizes size (button-sizes :md))
                       class)
    :disabled disabled?
    :on-click on-click}
   label])

;; ---------------------------------------------------------------------------
;; badge
;;
;; Props:
;;   :label    (string)   — badge text                          [required]
;;   :variant  (keyword)  — :default | :success | :warning | :danger | :info
;;   :class    (string)   — extra CSS classes
;; ---------------------------------------------------------------------------

(def ^:private badge-variants
  {:default "bg-gray-800 text-gray-400"
   :success "bg-green-900 text-green-300"
   :warning "bg-yellow-900 text-yellow-300"
   :danger  "bg-red-900 text-red-300"
   :info    "bg-indigo-900 text-indigo-300"})

(defn badge
  [{:keys [label variant class]
    :or   {variant :default}}]
  [:span
   {:class (classes "inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    (get badge-variants variant (badge-variants :default))
                    class)}
   label])

;; ---------------------------------------------------------------------------
;; card
;;
;; Props:
;;   :title    (string)   — optional card header title
;;   :subtitle (string)   — optional subtitle below title
;;   :footer   (hiccup)   — optional footer slot
;;   :class    (string)   — extra CSS classes on the outer wrapper
;;
;; Usage:
;;   [card {:title "My Card"}
;;     [:p "Card body content here."]]
;; ---------------------------------------------------------------------------

(defn card
  [{:keys [title subtitle footer class]} & children]
  [:div
   {:class (classes "bg-gray-900 rounded-xl border border-gray-800 overflow-hidden" class)}
   (when (or title subtitle)
     [:div {:class "px-6 py-4 border-b border-gray-800"}
      (when title
        [:h3 {:class "text-base font-semibold text-gray-100"} title])
      (when subtitle
        [:p {:class "mt-1 text-sm text-gray-500"} subtitle])])
   (into [:div {:class "px-6 py-4"}] children)
   (when footer
     [:div {:class "px-6 py-4 bg-gray-800/50 border-t border-gray-800"}
      footer])])

;; ---------------------------------------------------------------------------
;; spinner
;;
;; Props:
;;   :size   (keyword) — :sm | :md | :lg   default: :md
;;   :class  (string)  — extra CSS classes
;;   :label  (string)  — sr-only accessible label
;; ---------------------------------------------------------------------------

(def ^:private spinner-sizes
  {:sm "w-4 h-4"
   :md "w-6 h-6"
   :lg "w-10 h-10"})

(defn spinner
  [{:keys [size class label]
    :or   {size :md label "Loading…"}}]
  [:div {:class (classes "flex items-center justify-center" class)
         :role  "status"
         :aria-label label}
   [:svg {:class    (classes "animate-spin text-indigo-400"
                             (get spinner-sizes size (spinner-sizes :md)))
          :xmlns    "http://www.w3.org/2000/svg"
          :fill     "none"
          :view-box "0 0 24 24"}
    [:circle {:class "opacity-25" :cx "12" :cy "12" :r "10"
              :stroke "currentColor" :stroke-width "4"}]
    [:path   {:class "opacity-75" :fill "currentColor"
              :d "M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"}]]
   [:span {:class "sr-only"} label]])

;; ---------------------------------------------------------------------------
;; alert
;;
;; Props:
;;   :message  (string)   — alert body text                     [required]
;;   :title    (string)   — optional bold heading
;;   :variant  (keyword)  — :info | :success | :warning | :error
;;   :on-close (fn)       — if provided, renders a dismiss button
;;   :class    (string)   — extra CSS classes
;; ---------------------------------------------------------------------------

(def ^:private alert-variants
  {:info    {:wrapper "bg-indigo-950 border-indigo-800 text-indigo-200"  :icon "ℹ️"}
   :success {:wrapper "bg-green-950 border-green-800 text-green-200"     :icon "✅"}
   :warning {:wrapper "bg-yellow-950 border-yellow-800 text-yellow-200"  :icon "⚠️"}
   :error   {:wrapper "bg-red-950 border-red-800 text-red-200"           :icon "❌"}})

(defn alert
  [{:keys [message title variant on-close class]
    :or   {variant :info}}]
  (let [{:keys [wrapper icon]} (get alert-variants variant (alert-variants :info))]
    [:div
     {:class (classes "flex gap-3 p-4 rounded-lg border" wrapper class)
      :role  "alert"}
     [:span {:class "text-lg leading-none flex-shrink-0" :aria-hidden "true"} icon]
     [:div {:class "flex-1 min-w-0"}
      (when title
        [:p {:class "font-semibold text-sm mb-0.5"} title])
      [:p {:class "text-sm"} message]]
     (when on-close
       [:button
        {:class    "flex-shrink-0 ml-auto text-current opacity-60 hover:opacity-100 transition-opacity leading-none text-lg"
         :on-click on-close
         :aria-label "Dismiss"}
        "×"])]))

;; ---------------------------------------------------------------------------
;; navbar
;;
;; Props:
;;   :app-name      (string)  — brand name rendered on the left
;;   :current-route (keyword) — active route handler for highlight logic
;;   :links         (vector)  — [{:handler :home :label "Home"} ...]
;;   :class         (string)  — extra CSS classes on the <nav> element
;; ---------------------------------------------------------------------------

(defn nav-link
  [{:keys [handler label active class]}]
  [:a
   {:class    (classes
               "px-3 py-2 rounded-md text-sm font-medium transition-colors duration-150"
               (if active
                 "bg-indigo-600 text-white"
                 "text-gray-400 hover:text-gray-100 hover:bg-gray-800")
               class)
    :href     (str "#" (routes/path-for handler))
    :on-click (fn [e]
                (.preventDefault e)
                (routes/navigate! handler))}
   label])

(defn navbar
  [{:keys [app-name current-route links class]}]
  [:nav
   {:class (classes "bg-gray-900 border-b border-gray-800 sticky top-0 z-50" class)}
   [:div {:class "max-w-5xl mx-auto px-4 sm:px-6 lg:px-8"}
    [:div {:class "flex items-center justify-between h-16"}
     [:a
      {:class    "text-xl font-bold text-indigo-400 tracking-tight"
       :href     "#/"
       :on-click (fn [e] (.preventDefault e) (routes/navigate! :home))}
      app-name]
     [:div {:class "flex items-center gap-1"}
      (for [{:keys [handler label]} links]
        ^{:key handler}
        [nav-link {:handler handler
                   :label   label
                   :active  (= current-route handler)}])]]]])

;; ---------------------------------------------------------------------------
;; page-title
;;
;; Props:
;;   :title     (string)  — main heading                        [required]
;;   :subtitle  (string)  — optional supporting text
;;   :class     (string)  — extra CSS classes on the wrapper
;; ---------------------------------------------------------------------------

(defn page-title
  [{:keys [title subtitle class]}]
  [:div {:class (classes "mb-8" class)}
   [:h1 {:class "text-3xl font-bold text-gray-100 tracking-tight"} title]
   (when subtitle
     [:p {:class "mt-2 text-base text-gray-500"} subtitle])])

;; ---------------------------------------------------------------------------
;; divider
;;
;; Props:
;;   :label  (string)  — optional centred label text
;;   :class  (string)  — extra CSS classes
;; ---------------------------------------------------------------------------

(defn divider
  ([]
   (divider {}))
  ([{:keys [label class]}]
   (if label
     [:div {:class (classes "relative my-6" class)}
      [:div {:class "absolute inset-0 flex items-center" :aria-hidden "true"}
       [:div {:class "w-full border-t border-gray-800"}]]
      [:div {:class "relative flex justify-center"}
       [:span {:class "px-3 bg-gray-950 text-sm text-gray-600"} label]]]
     [:hr {:class (classes "my-6 border-gray-800" class)}])))

;; ---------------------------------------------------------------------------
;; code-block
;;
;; Props:
;;   :code   (string)  — source code string to display          [required]
;;   :lang   (string)  — optional language label shown top-right
;;   :class  (string)  — extra CSS classes on the outer wrapper
;; ---------------------------------------------------------------------------

(defn code-block
  [{:keys [code lang class]}]
  [:div {:class (classes "relative rounded-lg overflow-hidden bg-gray-950 border border-gray-800" class)}
   (when lang
     [:div {:class "absolute top-0 right-0 px-3 py-1 text-xs text-gray-500 font-mono bg-gray-900 rounded-bl-lg"}
      lang])
   [:pre {:class "overflow-x-auto p-4 pt-6 text-sm text-gray-300 font-mono leading-relaxed"}
    [:code code]]])

;; ---------------------------------------------------------------------------
;; notification
;;
;; Props:
;;   :id         (string)  — unique id
;;   :message    (string)  — notification body text
;;   :on-dismiss (fn)      — called with id when dismiss is clicked
;;   :class      (string)  — extra CSS classes
;; ---------------------------------------------------------------------------

(defn notification
  [{:keys [id message on-dismiss class]}]
  [:div
   {:class    (classes "flex items-start gap-3 bg-gray-900 border border-gray-700
                        rounded-lg shadow-xl px-4 py-3 max-w-sm w-full" class)
    :role     "status"
    :aria-live "polite"}
   [:p {:class "flex-1 text-sm text-gray-300"} message]
   (when on-dismiss
     [:button
      {:class      "flex-shrink-0 text-gray-600 hover:text-gray-300 transition-colors text-lg leading-none"
       :on-click   #(on-dismiss id)
       :aria-label "Dismiss notification"}
      "×"])])

;; ---------------------------------------------------------------------------
;; notification-stack
;;
;; Props:
;;   :notifications  (seq)  — [{:id "..." :message "..."} ...]
;;   :on-dismiss     (fn)   — called with id on dismiss
;; ---------------------------------------------------------------------------

(defn notification-stack
  [{:keys [notifications on-dismiss]}]
  (when (seq notifications)
    [:div
     {:class "fixed bottom-4 right-4 z-50 flex flex-col gap-2"
      :aria-label "Notifications"}
     (for [{:keys [id message]} notifications]
       ^{:key id}
       [notification {:id id :message message :on-dismiss on-dismiss}])]))
