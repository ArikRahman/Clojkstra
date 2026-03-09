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
     notification  — dismissible toast-style notification")

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
   focus:ring-offset-2 focus:ring-offset-rp-base
   disabled:opacity-40 disabled:cursor-not-allowed")

(def ^:private button-variants
  {:primary   "bg-rp-iris text-rp-base hover:bg-rp-foam focus:ring-rp-iris"
   :secondary "bg-rp-overlay text-rp-text border border-rp-highlight-high hover:bg-rp-highlight-med focus:ring-rp-highlight-high"
   :ghost     "bg-transparent text-rp-subtle hover:text-rp-text hover:bg-rp-overlay focus:ring-rp-highlight-high"
   :danger    "bg-rp-love text-rp-base hover:opacity-90 focus:ring-rp-love"})

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
  {:default "bg-rp-overlay text-rp-subtle"
   :success "bg-rp-overlay text-rp-pine"
   :warning "bg-rp-overlay text-rp-gold"
   :danger  "bg-rp-overlay text-rp-love"
   :info    "bg-rp-overlay text-rp-iris"})

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
   {:class (classes "bg-rp-surface rounded-xl border border-rp-highlight-high overflow-hidden" class)}
   (when (or title subtitle)
     [:div {:class "px-6 py-4 border-b border-rp-highlight-high"}
      (when title
        [:h3 {:class "text-base font-semibold text-rp-text"} title])
      (when subtitle
        [:p {:class "mt-1 text-sm text-rp-muted"} subtitle])])
   (into [:div {:class "px-6 py-4"}] children)
   (when footer
     [:div {:class "px-6 py-4 bg-rp-overlay/50 border-t border-rp-highlight-high"}
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
   [:svg {:class    (classes "animate-spin text-rp-iris"
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
  {:info    {:wrapper "bg-rp-overlay border-rp-iris text-rp-foam"    :icon "ℹ️"}
   :success {:wrapper "bg-rp-overlay border-rp-pine text-rp-pine"    :icon "✅"}
   :warning {:wrapper "bg-rp-overlay border-rp-gold text-rp-gold"    :icon "⚠️"}
   :error   {:wrapper "bg-rp-overlay border-rp-love text-rp-love"    :icon "❌"}})

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
   [:h1 {:class "text-3xl font-bold text-rp-text tracking-tight"} title]
   (when subtitle
     [:p {:class "mt-2 text-base text-rp-muted"} subtitle])])

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
       [:div {:class "w-full border-t border-rp-highlight-high"}]]
      [:div {:class "relative flex justify-center"}
       [:span {:class "px-3 bg-rp-base text-sm text-rp-muted"} label]]]
     [:hr {:class (classes "my-6 border-rp-highlight-high" class)}])))

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
  [:div {:class (classes "relative rounded-lg overflow-hidden bg-rp-base border border-rp-highlight-high" class)}
   (when lang
     [:div {:class "absolute top-0 right-0 px-3 py-1 text-xs text-rp-muted font-mono bg-rp-surface rounded-bl-lg"}
      lang])
   [:pre {:class "overflow-x-auto p-4 pt-6 text-sm text-rp-subtle font-mono leading-relaxed"}
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
   {:class    (classes "flex items-start gap-3 bg-rp-surface border border-rp-highlight-high
                        rounded-lg shadow-xl px-4 py-3 max-w-sm w-full" class)
    :role     "status"
    :aria-live "polite"}
   [:p {:class "flex-1 text-sm text-rp-text"} message]
   (when on-dismiss
     [:button
      {:class      "flex-shrink-0 text-rp-muted hover:text-rp-text transition-colors text-lg leading-none"
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
