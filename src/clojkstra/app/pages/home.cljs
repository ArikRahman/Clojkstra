(ns clojkstra.app.pages.home
  "Home page for Clojkstra.
   [DEMO FILE] — project explainer / landing page.
   Replace this content with your own when cloning."
  (:require
   [clojkstra.app.components.ui :as ui]
   [clojkstra.app.routes        :as routes]))

;; ---------------------------------------------------------------------------
;; Data
;; ---------------------------------------------------------------------------

(def ^:private stack
  [{:name "ClojureScript" :desc "Compiled, functional, lisp on the browser"}
   {:name "re-frame"      :desc "Predictable state via events and subscriptions"}
   {:name "Reagent"       :desc "Minimal React wrapper with ClojureScript idioms"}
   {:name "shadow-cljs"   :desc "Fast, deps.edn-native build tool"}

   {:name "Bun"           :desc "Fast JS runtime — no npm, no node"}])

(def ^:private principles
  [{:icon "🗂️"
    :title "One source of truth"
    :body "All state lives in a single immutable app-db map. Nothing is hidden in component-local state that other parts of the app need to know about."}
   {:icon "⚡"
    :title "Events, not mutations"
    :body "The only way to change state is to dispatch a named event. Every transition is explicit, logged, and replayable in the re-frisk dev inspector."}
   {:icon "🔍"
    :title "Subscriptions, not prop drilling"
    :body "Views subscribe to exactly the slice of state they need. No prop chains, no context providers — just a named subscription and a deref."}
   {:icon "🧩"
    :title "Framework vs. demo"
    :body "Every file is labelled. Framework files form the reusable base. Demo files show how to use it. Delete the demos and the architecture stands on its own."}
   {:icon "📦"
    :title "Clone and rename"
    :body "The whole point is that you fork this, rename the namespace root, delete the demo pages, seed your own db keys, and ship a real app — fast."}
   {:icon "🛠️"
    :title "Nix-first tooling"
    :body "A flake.nix devShell pins every tool: JDK, Clojure CLI, Bun, clj-kondo, cljfmt. One 'nix develop' and the environment is fully reproducible."}])

;; ---------------------------------------------------------------------------
;; Sections
;; ---------------------------------------------------------------------------

(defn- hero []
  [:header {:class "py-20 text-center flex flex-col items-center gap-5"}
   [:div {:class "text-6xl" :aria-hidden "true"} "⚡"]
   [:h1 {:class "text-5xl font-bold text-gray-100 tracking-tight"}
    "Clojkstra"]
   [:p {:class "text-xl text-gray-400 max-w-xl leading-relaxed"}
    "A ClojureScript + React (via Reagent + Re-frame) template designed for bootstrapping.
     Build new SPAs by forking, renaming, and deleting the demo pages."]
   [:div {:class "flex flex-wrap justify-center gap-3 mt-2"}
    [ui/badge {:label "ClojureScript" :variant :info}]
    [ui/badge {:label "re-frame"      :variant :info}]
    [ui/badge {:label "shadow-cljs"   :variant :default}]
    [ui/badge {:label "GitHub Pages"  :variant :success}]
    [ui/badge {:label "Nix devShell"  :variant :default}]]
   [:div {:class "flex flex-wrap justify-center gap-3 mt-4"}
    [ui/button {:label    "About →"
                :variant  :primary
                :on-click #(routes/navigate! :about)}]
    [ui/button {:label    "See the template →"
                :variant  :secondary
                :on-click #(routes/navigate! :example)}]]])

(defn- stack-section []
  [:section {:aria-labelledby "stack-heading"}
   [:h2 {:id    "stack-heading"
         :class "text-2xl font-bold text-gray-100 mb-6 text-center"}
    "The Stack"]
   [:div {:class "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4"}
    (for [{:keys [name desc]} stack]
      ^{:key name}
      [:div {:class "bg-gray-900 border border-gray-800 rounded-xl p-5
                     hover:border-indigo-700 hover:bg-gray-800/60
                     transition-all duration-150"}
       [:p {:class "font-semibold text-gray-100 text-sm mb-1"} name]
       [:p {:class "text-sm text-gray-500 leading-relaxed"} desc]])]])

(defn- principles-section []
  [:section {:aria-labelledby "principles-heading"}
   [:h2 {:id    "principles-heading"
         :class "text-2xl font-bold text-gray-100 mb-6 text-center"}
    "Design Principles"]
   [:div {:class "grid grid-cols-1 md:grid-cols-2 gap-5"}
    (for [{:keys [icon title body]} principles]
      ^{:key title}
      [:div {:class "bg-gray-900 border border-gray-800 rounded-xl p-6 flex gap-4
                     hover:border-indigo-700 transition-all duration-150"}
       [:span {:class "text-2xl flex-shrink-0 mt-0.5" :aria-hidden "true"} icon]
       [:div
        [:h3 {:class "font-semibold text-gray-100 text-sm mb-1"} title]
        [:p  {:class "text-sm text-gray-500 leading-relaxed"} body]]])]])

(defn- quick-start-section []
  [ui/card {:title    "Bootstrap a new app from this"
            :subtitle "Five steps from clone to a working, renamed SPA."}
   [:ol {:class "flex flex-col gap-4 mt-1"}
    (for [[n step] (map-indexed vector
                    ["Fork or clone this repo and rename the directory."
                     "Move src/clojkstra/ to src/your_app/ and do a project-wide find-and-replace: clojkstra.app → your-app.app"
                     "Update :app-name and :version in db.cljs. Update :init-fn in shadow-cljs.edn."
                     "Delete pages/home.cljs, pages/about.cljs, pages/example.cljs. Remove their [DEMO] events and subs."
                     "Add your domain keys to db.cljs, write your first real page in pages/, and ship."])]
      ^{:key n}
      [:li {:class "flex gap-4 items-start"}
       [:span {:class "flex-shrink-0 w-7 h-7 rounded-full bg-indigo-900 text-indigo-300
                        text-sm font-bold flex items-center justify-center"}
        (inc n)]
       [:p {:class "text-sm text-gray-400 leading-relaxed pt-0.5"} step]])]])

;; ---------------------------------------------------------------------------
;; Page root
;; ---------------------------------------------------------------------------

(defn page []
  [:div {:class "flex flex-col gap-16"}
   [hero]
   [ui/divider]
   [stack-section]
   [ui/divider]
   [principles-section]
   [ui/divider]
   [quick-start-section]])
