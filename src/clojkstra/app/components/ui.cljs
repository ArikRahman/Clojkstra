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
     notification  — dismissible toast-style notification

   Extension point:
     Add new components to this file as the app grows, or split into
     sub-namespaces (e.g. components.forms, components.data-display)
     when the file gets long."
  (:require
   [re-frame.core        :as rf]
   [clojkstra.app.routes :as routes]))

;; ---------------------------------------------------------------------------
;; Internal helpers
;; ---------------------------------------------------------------------------

(defn- classes
  "Joins a variable list of class strings/nils into a single class string,
   ignoring nil/false entries."
  [& cs]
  (->> cs (filter identity) (clojure.string/join " ")))

;; ---------------------------------------------------------------------------
;; button
;;
;; Props:
;;   :label     (string)   — button text                        [required]
;;   :on-click  (fn)       — click handler                      [required]
;;   :variant   (keyword)  — :primary | :secondary | :ghost | :danger
;;                           default: :primary
;;   :size      (keyword)  — :sm | :md | :lg   default: :md
;;   :disabled? (boolean)  — disables interaction
;;   :class     (string)   — extra CSS classes
;;   :type      (string)   — HTML button type, default "button"
;; ---------------------------------------------------------------------------

(def ^:private button-base
  "inline-flex items-center justify-center font-medium rounded-lg
   transition-colors duration-150 focus:outline-none focus:ring-2
   focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed")

(def ^:private button-variants
  {:primary   "bg-indigo-600 text-white hover:bg-indigo-700 focus:ring-indigo-500"
   :secondary "bg-white text-indigo-600 border border-indigo-600 hover:bg-indigo-50 focus:ring-indigo-500"
   :ghost     "bg-transparent text-gray-600 hover:bg-gray-100 focus:ring-gray-400"
   :danger    "bg-red-600 text-white hover:bg-red-700 focus:ring-red-500"})

(def ^:private button-sizes
  {:sm "px-3 py-1.5 text-sm"
   :md "px-4 py-2 text-sm"
   :lg "px-6 py-3 text-base"})

(defn button
  [{:keys [label on-click variant size disabled? class type]
    :or   {variant :primary size :md type "button"}}]
  [:button
   {:type      type
    :class     (classes button-base
                        (get button-variants variant (button-variants :primary))
                        (get button-sizes size (button-sizes :md))
                        class)
    :disabled  disabled?
    :on-click  on-click}
   label])

;; ---------------------------------------------------------------------------
;; badge
;;
;; Props:
;;   :label    (string)   — badge text                          [required]
;;   :variant  (keyword)  — :default | :success | :warning | :danger | :info
;;                          default: :default
;;   :class    (string)   — extra CSS classes
;; ---------------------------------------------------------------------------

(def ^:private badge-variants
  {:default "bg-gray-100 text-gray-700"
   :success "bg-green-100 text-green-700"
   :warning "bg-yellow-100 text-yellow-800"
   :danger  "bg-red-100 text-red-700"
   :info    "bg-blue-100 text-blue-700"})

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
;;   :footer   (hiccup)   — optional footer slot (rendered as-is)
;;   :class    (string)   — extra CSS classes on the outer wrapper
;;   :children (hiccup)   — card body content; pass as reagent children
;;
;; Usage:
;;   [card {:title "My Card"}
;;     [:p "Card body content here."]]
;; ---------------------------------------------------------------------------

(defn card
  [{:keys [title subtitle footer class]} & children]
  [:div
   {:class (classes "bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden" class)}
   (when (or title subtitle)
     [:div {:class "px-6 py-4 border-b border-gray-100"}
      (when title
        [:h3 {:class "text-base font-semibold text-gray-900"} title])
      (when subtitle
        [:p {:class "mt-1 text-sm text-gray-500"} subtitle])])
   (into [:div {:class "px-6 py-4"}] children)
   (when footer
     [:div {:class "px-6 py-4 bg-gray-50 border-t border-gray-100"}
      footer])])

;; ---------------------------------------------------------------------------
;; spinner
;;
;; Props:
;;   :size   (keyword) — :sm | :md | :lg   default: :md
;;   :class  (string)  — extra CSS classes
;;   :label  (string)  — sr-only accessible label, default "Loading…"
;; ---------------------------------------------------------------------------

(def ^:private spinner-sizes
  {:sm "w-4 h-4"
   :md "w-6 h-6"
   :lg "w-10 h-10"})

(defn spinner
  [{:keys [size class label]
    :or   {size :md label "Loading…"}}]
  [:div {:class (classes "flex items-center justify-center" class)
         :role "status"
         :aria-label label}
   [:svg {:class     (classes "animate-spin text-indigo-600"
                               (get spinner-sizes size (spinner-sizes :md)))
           :xmlns     "http://www.w3.org/2000/svg"
           :fill      "none"
           :view-box  "0 0 24 24"}
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
;;                          default: :info
;;   :on-close (fn)       — if provided, renders a dismiss ✕ button
;;   :class    (string)   — extra CSS classes
;; ---------------------------------------------------------------------------

(def ^:private alert-variants
  {:info    {:wrapper "bg-blue-50 border-blue-200 text-blue-800"
             :icon    "ℹ️"}
   :success {:wrapper "bg-green-50 border-green-200 text-green-800"
             :icon    "✅"}
   :warning {:wrapper "bg-yellow-50 border-yellow-200 text-yellow-800"
             :icon    "⚠️"}
   :error   {:wrapper "bg-red-50 border-red-200 text-red-800"
             :icon    "❌"}})

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
        {:class    "flex-shrink-0 ml-auto text-current opacity-60 hover:opacity-100
                    transition-opacity leading-none text-lg"
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
;;
;; Calls routes/navigate! on link clicks (no full page reload).
;; ---------------------------------------------------------------------------

(defn nav-link
  "Individual navigation link.  Highlights when :handler matches :active."
  [{:keys [handler label active class]}]
  [:a
   {:class    (classes
               "px-3 py-2 rounded-md text-sm font-medium transition-colors duration-150"
               (if active
                 "bg-indigo-600 text-white"
                 "text-gray-600 hover:text-indigo-600 hover:bg-indigo-50")
               class)
    :href     (str "#" (routes/path-for handler))
    :on-click (fn [e]
                (.preventDefault e)
                (routes/navigate! handler))}
   label])

(defn navbar
  [{:keys [app-name current-route links class]}]
  [:nav
   {:class (classes "bg-white border-b border-gray-200 sticky top-0 z-50" class)}
   [:div {:class "max-w-5xl mx-auto px-4 sm:px-6 lg:px-8"}
    [:div {:class "flex items-center justify-between h-16"}
     ;; Brand
     [:a
      {:class    "text-xl font-bold text-indigo-600 tracking-tight"
       :href     "#/"
       :on-click (fn [e] (.preventDefault e) (routes/navigate! :home))}
      app-name]
     ;; Nav links
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
   [:h1 {:class "text-3xl font-bold text-gray-900 tracking-tight"} title]
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
       [:div {:class "w-full border-t border-gray-200"}]]
      [:div {:class "relative flex justify-center"}
       [:span {:class "px-3 bg-white text-sm text-gray-500"} label]]]
     [:hr {:class (classes "my-6 border-gray-200" class)}])))

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
  [:div {:class (classes "relative rounded-lg overflow-hidden bg-gray-900" class)}
   (when lang
     [:div {:class "absolute top-0 right-0 px-3 py-1 text-xs text-gray-400 font-mono
                    bg-gray-800 rounded-bl-lg"}
      lang])
   [:pre {:class "overflow-x-auto p-4 pt-6 text-sm text-gray-100 font-mono leading-relaxed"}
    [:code code]]])

;; ---------------------------------------------------------------------------
;; notification
;;
;; A dismissible toast-style notification item.
;; Intended to be rendered from the notifications list in app-db.
;;
;; Props:
;;   :id        (string)  — unique id (passed back to on-dismiss)
;;   :message   (string)  — notification body text
;;   :on-dismiss (fn)     — called with id when the dismiss button is clicked
;;   :class     (string)  — extra CSS classes
;; ---------------------------------------------------------------------------

(defn notification
  [{:keys [id message on-dismiss class]}]
  [:div
   {:class (classes
            "flex items-start gap-3 bg-white border border-gray-200 rounded-lg
             shadow-md px-4 py-3 max-w-sm w-full"
            class)
    :role  "status"
    :aria-live "polite"}
   [:p {:class "flex-1 text-sm text-gray-700"} message]
   (when on-dismiss
     [:button
      {:class    "flex-shrink-0 text-gray-400 hover:text-gray-700 transition-colors text-lg leading-none"
       :on-click #(on-dismiss id)
       :aria-label "Dismiss notification"}
      "×"])])

;; ---------------------------------------------------------------------------
;; notification-stack
;;
;; Renders all active notifications from a seq.
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
