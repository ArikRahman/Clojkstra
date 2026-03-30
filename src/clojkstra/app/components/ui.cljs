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
     [clojkstra.app.theme :as theme]))

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

(defn button
      [{:keys [label on-click variant size disabled? class type]
        :or   {variant :primary size :md type "button"}}]
      [:button
       {:type     type
        :class    (classes theme/btn-base
                           (get theme/btn variant (:primary theme/btn))
                           (get theme/btn-size size (:md theme/btn-size))
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

(defn badge
      [{:keys [label variant class]
        :or   {variant :default}}]
      [:span
       {:class (classes theme/badge-base
                        (get theme/badge variant (:default theme/badge))
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
       {:class (classes (:outer theme/card) class)}
       (when (or title subtitle)
             [:div {:class (:header theme/card)}
              (when title
                    [:h3 {:class (:title theme/card)} title])
              (when subtitle
                    [:p {:class (:sub theme/card)} subtitle])])
       (into [:div {:class (:body theme/card)}] children)
       (when footer
             [:div {:class (:footer theme/card)}
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
      [:div {:class  (classes "flex items-center justify-center" class)
             :role   "status"
             :aria-label label}
       [:svg {:class    (classes (:svg theme/spinner)
                                 (get spinner-sizes size (:md spinner-sizes)))
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

(def ^:private alert-icons
     {:info    "ℹ️"
      :success "✅"
      :warning "⚠️"
      :error   "❌"})

(defn alert
      [{:keys [message title variant on-close class]
        :or   {variant :info}}]
      [:div
       {:class (classes theme/alert-base
                        (get theme/alert variant (:info theme/alert))
                        class)
        :role  "alert"}
       [:span {:class "text-lg leading-none flex-shrink-0" :aria-hidden "true"}
        (get alert-icons variant (:info alert-icons))]
       [:div {:class "flex-1 min-w-0"}
        (when title
              [:p {:class "font-semibold text-sm mb-0.5"} title])
        [:p {:class "text-sm"} message]]
       (when on-close
             [:button
              {:class      "flex-shrink-0 ml-auto text-current opacity-60 hover:opacity-100 transition-opacity leading-none text-lg"
               :on-click   on-close
               :aria-label "Dismiss"}
              "×"])])

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
       [:h1 {:class (:heading theme/page-title)} title]
       (when subtitle
             [:p {:class (:subtitle theme/page-title)} subtitle])])

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
             [:div {:class (:line theme/divider)}]]
            [:div {:class "relative flex justify-center"}
             [:span {:class (:label theme/divider)} label]]]
           [:hr {:class (classes (:rule theme/divider) class)}])))

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
      [:div {:class (classes (:outer theme/code-block) class)}
       (when lang
             [:div {:class (:lang theme/code-block)} lang])
       [:pre {:class (:pre theme/code-block)}
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
       {:class     (classes (:outer theme/notification) class)
        :role      "status"
        :aria-live "polite"}
       [:p {:class (:message theme/notification)} message]
       (when on-dismiss
             [:button
              {:class      (:dismiss theme/notification)
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
             {:class     "fixed bottom-4 right-4 z-50 flex flex-col gap-2"
              :aria-label "Notifications"}
             (for [{:keys [id message]} notifications]
                  ^{:key id}
                  [notification {:id id :message message :on-dismiss on-dismiss}])]))
