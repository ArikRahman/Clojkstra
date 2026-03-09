(ns clojkstra.app.utils
    "Shared utility functions for Clojkstra.
   [FRAMEWORK FILE] — generic helpers with no re-frame or domain dependencies.

   Sections:
     - String utilities
     - Collection utilities
     - Date / time formatting
     - DOM helpers
     - Dev / debug helpers

   Convention:
     All functions here must be pure (no side effects) except those in the
     'DOM helpers' section which are clearly marked.
     Keep this namespace free of re-frame, routes, and app-db references so
     it can be required by any other namespace without creating cycles.")

;; ---------------------------------------------------------------------------
;; String utilities
;; ---------------------------------------------------------------------------

(defn blank?
      "Returns true if s is nil, empty, or contains only whitespace."
      [s]
      (or (nil? s)
          (zero? (count (clojure.string/trim (str s))))))

(defn presence
      "Returns s trimmed if non-blank, otherwise nil.
   Useful for coercing empty form inputs to nil."
      [s]
      (when-not (blank? s)
                (clojure.string/trim (str s))))

(defn kebab->title
      "Converts a kebab-case keyword or string to a Title Case string.
   Example: :my-cool-page => \"My Cool Page\""
      [s]
      (->> (clojure.string/split (name s) #"-")
           (map clojure.string/capitalize)
           (clojure.string/join " ")))

(defn truncate
      "Truncates s to at most `max-len` characters, appending `ellipsis` if
   truncation occurred.  Default ellipsis is \"…\"."
      ([s max-len]
       (truncate s max-len "…"))
      ([s max-len ellipsis]
       (if (> (count s) max-len)
           (str (subs s 0 max-len) ellipsis)
           s)))

(defn slugify
      "Converts a string to a URL-safe lowercase slug.
   Example: \"Hello World!\" => \"hello-world\""
      [s]
      (-> (str s)
          clojure.string/lower-case
          (clojure.string/replace #"[^\w\s-]" "")
          (clojure.string/replace #"[\s_]+" "-")
          (clojure.string/replace #"-+" "-")
          (clojure.string/replace #"^-|-$" "")))

;; ---------------------------------------------------------------------------
;; Collection utilities
;; ---------------------------------------------------------------------------

(defn index-by
      "Converts a seq of maps into a map keyed by `f` applied to each item.
   Example: (index-by :id [{:id 1 :name \"a\"} {:id 2 :name \"b\"}])
            => {1 {:id 1 :name \"a\"} 2 {:id 2 :name \"b\"}}"
      [f coll]
      (into {} (map (juxt f identity)) coll))

(defn remove-by-id
      "Returns a vector of `coll` with the item matching `id` removed.
   Assumes each item has an :id key."
      [coll id]
      (filterv #(not= (:id %) id) coll))

(defn move-item
      "Moves the item at `from-idx` to `to-idx` in vector `v`.
   Returns a new vector."
      [v from-idx to-idx]
      (let [item (nth v from-idx)
            without (vec (concat (subvec v 0 from-idx)
                                 (subvec v (inc from-idx))))]
           (vec (concat (subvec without 0 to-idx)
                        [item]
                        (subvec without to-idx)))))

(defn deep-merge
      "Recursively merges maps.  Non-map values in `b` shadow those in `a`."
      [a b]
      (if (and (map? a) (map? b))
          (merge-with deep-merge a b)
          b))

;; ---------------------------------------------------------------------------
;; Date / time formatting
;; ---------------------------------------------------------------------------

(defn now-ms
      "Returns the current time as a Unix epoch millisecond integer."
      []
      (.now js/Date))

(defn format-date
      "Formats a js/Date (or epoch-ms number) as a locale date string.
   Accepts an optional `opts` map passed to toLocaleDateString.
   Example: (format-date (js/Date.) {:year \"numeric\" :month \"long\" :day \"numeric\"})"
      ([date]
       (format-date date nil))
      ([date opts]
       (let [d (if (number? date) (js/Date. date) date)]
            (if opts
                (.toLocaleDateString d js/undefined (clj->js opts))
                (.toLocaleDateString d)))))

(defn relative-time
      "Returns a human-readable relative time string given an epoch-ms timestamp.
   Example: 'just now', '3 minutes ago', '2 days ago'."
      [ts]
      (let [delta-s (/ (- (now-ms) ts) 1000)]
           (cond
            (< delta-s 10)   "just now"
            (< delta-s 60)   (str (Math/floor delta-s) " seconds ago")
            (< delta-s 3600) (let [m (Math/floor (/ delta-s 60))]
                                  (str m " minute" (when (> m 1) "s") " ago"))
            (< delta-s 86400) (let [h (Math/floor (/ delta-s 3600))]
                                   (str h " hour" (when (> h 1) "s") " ago"))
            :else             (let [d (Math/floor (/ delta-s 86400))]
                                   (str d " day" (when (> d 1) "s") " ago")))))

;; ---------------------------------------------------------------------------
;; DOM helpers  [SIDE-EFFECTFUL]
;; ---------------------------------------------------------------------------

(defn scroll-to-top!
      "Scrolls the window to the top.  Call on route changes to reset scroll
   position between pages."
      []
      (.scrollTo js/window 0 0))

(defn set-document-title!
      "Sets document.title.  Prefer the :set-title re-frame effect in event
   handlers; use this helper in one-off scenarios."
      [title]
      (set! (.-title js/document) (str title)))

(defn element-by-id
      "Returns the DOM element with the given id, or nil if not found."
      [id]
      (.getElementById js/document (str id)))

(defn focus!
      "Focuses the DOM element with the given id.  No-ops if not found."
      [id]
      (when-let [el (element-by-id id)]
                (.focus el)))

;; ---------------------------------------------------------------------------
;; Dev / debug helpers
;; ---------------------------------------------------------------------------

(defn log
      "Thin wrapper around js/console.log that pretty-prints ClojureScript
   data structures.  Only call from development paths — strip before prod
   or guard with goog.DEBUG."
      [label value]
      (js/console.log (str "[clojkstra] " label) (clj->js value)))

(defn warn
      "Like `log` but at the warn level."
      [label value]
      (js/console.warn (str "[clojkstra] " label) (clj->js value)))

(defn tap
      "Threading-friendly debug tap.  Logs `value` under `label` and returns
   `value` unchanged so it can be inserted into -> or ->> chains.
   Example: (->> items (tap \"before filter\") (filter active?))"
      [label value]
      (log label value)
      value)

(defn guard
      "Assert-style helper for development.  If `pred` applied to `value` is
   falsy, logs a warning with `msg` and returns `fallback`.
   Otherwise returns `value` unchanged."
      ([pred value msg]
       (guard pred value msg nil))
      ([pred value msg fallback]
       (if (pred value)
           value
           (do
            (warn msg value)
            fallback))))
