(ns clojkstra.app.theme
    "Semantic component style tokens — Layer 2 of the Clojkstra theme system.
   [FRAMEWORK FILE] — single source of truth for all themed class strings.

   Architecture:
     Layer 1 — CSS custom properties (:root block in index.html)
               Rosé Pine Moon palette values as --rp-* vars.
               Swap the entire palette here without touching anything else.

     Layer 2 — Semantic Tailwind aliases (extend.colors in index.html)
               Intent-named aliases: accent, surface, edge, danger, etc.
               These point at Layer 1 vars, not hex values.

     Layer 3 — This file.
               Component-level token maps. Each def is a map of
               keyword → complete Tailwind class string.
               Components require this ns and look up their classes here.

   Usage in components:
     (:require [clojkstra.app.theme :as theme])
     ...
     {:class (:primary theme/btn)}
     {:class (theme/card :outer)}

   Reskinning scope:
     - To change the palette (e.g. Moon → Dawn): edit Layer 1 only.
     - To change a component's color role (e.g. make nav use ok instead of
       accent): edit the relevant def in this file only.
     - To change component structure (layout, spacing, radius): edit the
       component file itself — structural classes live there, not here.

   Scope of tokens in this file:
     OWNED:   background, text, border, ring/focus colors, shadow variants,
              opacity modifiers on themed colors, color-carrying hover states.
     NOT OWNED: flex/grid/gap/padding/margin/width/height, rounded-*,
                text-sm/font-*, overflow/position/z-index. Those stay in
                component files because they are structural, not thematic.")

;; ---------------------------------------------------------------------------
;; button
;; ---------------------------------------------------------------------------

(def btn-base
     "inline-flex items-center justify-center font-medium rounded-lg
   transition-colors duration-150 focus:outline-none focus:ring-2
   focus:ring-offset-2 focus:ring-offset-base
   disabled:opacity-40 disabled:cursor-not-allowed")

(def btn
     {:primary   "bg-accent text-base hover:bg-accent-hi focus:ring-accent"
      :secondary "bg-overlay text-on-base border border-edge hover:bg-selection focus:ring-edge"
      :ghost     "bg-transparent text-subtle hover:text-on-base hover:bg-overlay focus:ring-edge"
      :danger    "bg-danger text-base hover:opacity-90 focus:ring-danger"})

(def btn-size
     {:sm "px-3 py-1.5 text-sm"
      :md "px-4 py-2 text-sm"
      :lg "px-6 py-3 text-base"})

;; ---------------------------------------------------------------------------
;; badge
;; ---------------------------------------------------------------------------

(def badge-base
     "inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium")

(def badge
     {:default "bg-overlay text-subtle"
      :success "bg-overlay text-ok"
      :warning "bg-overlay text-warn"
      :danger  "bg-overlay text-danger"
      :info    "bg-overlay text-accent"})

;; ---------------------------------------------------------------------------
;; card
;; ---------------------------------------------------------------------------

(def card
     {:outer  "bg-surface rounded-xl border border-edge overflow-hidden"
      :header "px-6 py-4 border-b border-edge"
      :title  "text-base font-semibold text-on-base"
      :sub    "mt-1 text-sm text-muted"
      :body   "px-6 py-4"
      :footer "px-6 py-4 bg-overlay/50 border-t border-edge"})

;; ---------------------------------------------------------------------------
;; card-item  (hover card used in stack/principles grids on home page)
;; ---------------------------------------------------------------------------

(def card-item
     {:outer       "bg-surface border border-edge rounded-xl transition-all duration-150 hover:border-accent hover:bg-overlay"
      :outer-flex  "bg-surface border border-edge rounded-xl flex gap-4 transition-all duration-150 hover:border-accent"
      :heading     "font-semibold text-on-base text-sm mb-1"
      :body        "text-sm text-muted leading-relaxed"})

;; ---------------------------------------------------------------------------
;; spinner
;; ---------------------------------------------------------------------------

(def spinner
     {:svg "animate-spin text-accent"})

;; ---------------------------------------------------------------------------
;; alert
;; ---------------------------------------------------------------------------

(def alert-base
     "flex gap-3 p-4 rounded-lg border")

(def alert
     {:info    "bg-overlay border-accent text-info-hi"
      :success "bg-overlay border-ok text-ok"
      :warning "bg-overlay border-warn text-warn"
      :error   "bg-overlay border-danger text-danger"})

;; ---------------------------------------------------------------------------
;; page-title
;; ---------------------------------------------------------------------------

(def page-title
     {:heading  "text-3xl font-bold text-on-base tracking-tight"
      :subtitle "mt-2 text-base text-muted"})

;; ---------------------------------------------------------------------------
;; divider
;; ---------------------------------------------------------------------------

(def divider
     {:line  "w-full border-t border-edge"
      :label "px-3 bg-base text-sm text-muted"
      :rule  "my-6 border-edge"})

;; ---------------------------------------------------------------------------
;; code-block
;; ---------------------------------------------------------------------------

(def code-block
     {:outer   "relative rounded-lg overflow-hidden bg-base border border-edge"
      :lang    "absolute top-0 right-0 px-3 py-1 text-xs text-muted font-mono bg-surface rounded-bl-lg"
      :pre     "overflow-x-auto p-4 pt-6 text-sm text-subtle font-mono leading-relaxed"})

;; ---------------------------------------------------------------------------
;; notification
;; ---------------------------------------------------------------------------

(def notification
     {:outer   "flex items-start gap-3 bg-surface border border-edge rounded-lg shadow-xl px-4 py-3 max-w-sm w-full"
      :message "flex-1 text-sm text-on-base"
      :dismiss "flex-shrink-0 text-muted hover:text-on-base transition-colors text-lg leading-none"})

;; ---------------------------------------------------------------------------
;; input / label  (form controls)
;; ---------------------------------------------------------------------------

(def input
     "w-full rounded-lg border border-edge bg-overlay text-on-base px-3 py-2 text-sm
   focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent
   transition placeholder-muted")

(def label
     "block text-sm font-medium text-subtle mb-1")

;; ---------------------------------------------------------------------------
;; navbar
;; ---------------------------------------------------------------------------

(def navbar
     {:outer     "bg-surface border-b border-edge sticky top-0 z-50"
      :brand     "text-xl font-bold text-accent tracking-tight"
      :links-row "flex items-center gap-1"})

(def nav-link
     {:base     "px-3 py-2 rounded-md text-sm font-medium transition-colors duration-150"
      :active   "bg-overlay text-accent"
      :inactive "text-subtle hover:text-on-base hover:bg-overlay"})

;; ---------------------------------------------------------------------------
;; footer
;; ---------------------------------------------------------------------------

(def footer
     {:outer     "mt-auto border-t border-edge bg-surface"
      :copyright "text-xs text-muted"
      :link      "text-xs text-muted hover:text-accent transition-colors"})

;; ---------------------------------------------------------------------------
;; app shell
;; ---------------------------------------------------------------------------

(def shell
     {:root    "min-h-screen flex flex-col bg-base text-on-base font-sans antialiased"
      :overlay "fixed inset-0 z-50 flex items-center justify-center bg-base/80 backdrop-blur-sm"})

;; ---------------------------------------------------------------------------
;; skip link
;; ---------------------------------------------------------------------------

(def skip-link
     "sr-only focus:not-sr-only focus:fixed focus:top-2 focus:left-2
   focus:z-50 focus:px-4 focus:py-2 focus:bg-accent focus:text-base
   focus:rounded-lg focus:text-sm focus:font-medium")

;; ---------------------------------------------------------------------------
;; 404 page
;; ---------------------------------------------------------------------------

(def page-404
     {:heading "text-3xl font-bold text-on-base"
      :body    "text-muted text-sm max-w-xs"})

;; ---------------------------------------------------------------------------
;; table  (file-map in about page)
;; ---------------------------------------------------------------------------

(def table
     {:thead-row "border-b border-edge"
      :th        "text-left py-2 pr-4 font-semibold text-subtle"
      :tbody-row "border-t border-edge hover:bg-overlay transition-colors"
      :td-code   "text-xs text-accent font-mono"
      :td-muted  "py-2.5 text-muted"})

;; ---------------------------------------------------------------------------
;; step-badge  (numbered steps in quick-start on home page)
;; ---------------------------------------------------------------------------

(def step-badge
     {:badge "flex-shrink-0 w-7 h-7 rounded-full bg-overlay text-accent text-sm font-bold flex items-center justify-center"
      :text  "text-sm text-subtle leading-relaxed pt-0.5"})

;; ---------------------------------------------------------------------------
;; inline emphasis  (about page prose highlights)
;; ---------------------------------------------------------------------------

(def emphasis
     "text-on-base font-medium")

;; ---------------------------------------------------------------------------
;; feature-flag panel  (example page — shown when flag is enabled)
;; ---------------------------------------------------------------------------

(def feature-panel
     {:outer "rounded-lg bg-overlay border border-accent p-4"
      :text  "text-sm text-info-hi"})

;; ---------------------------------------------------------------------------
;; detail-panel  (example page toggle detail)
;; ---------------------------------------------------------------------------

(def detail-panel
     "rounded-lg border border-edge bg-overlay p-4 text-sm text-subtle leading-relaxed")
