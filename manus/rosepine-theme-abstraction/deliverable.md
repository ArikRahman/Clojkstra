# Deliverable: Rosé Pine Moon — Semantic Theme Abstraction

## Summary

Replaced the shallow `rp-*` Tailwind palette token approach with a clean
two-layer abstraction. The project now has a proper separation between
*what the palette values are* and *what those values mean* in context.

Reskinning the entire app now requires editing exactly two places:
1. The `:root` CSS variables block in `docs/index.html` — to swap palette variants.
2. `src/clojkstra/app/theme.cljs` — to change how semantic roles map to components.

No component file needs to be touched for either operation.

---

## Architecture

```
Layer 1 — Palette (docs/index.html :root block)
  --rp-base: #232136
  --rp-iris: #c4a7e7
  ... (15 Rosé Pine Moon vars)
        │
        ▼
Layer 2a — Semantic Tailwind aliases (docs/index.html extend.colors)
  accent  → var(--rp-iris)
  surface → var(--rp-surface)
  danger  → var(--rp-love)
  edge    → var(--rp-highlight-high)
  ... (16 intent-named aliases)
        │
        ▼
Layer 2b — Component token maps (src/clojkstra/app/theme.cljs)
  (def btn {:primary "bg-accent text-base ..."})
  (def card {:outer "bg-surface border border-edge ..."})
  ... (25 token defs covering every component)
        │
        ▼
Layer 3 — Components (ui.cljs, views.cljs, pages/*.cljs)
  {:class (:primary theme/btn)}
  {:class (:outer theme/card)}
  — zero color strings here
```

---

## Files Changed

| File | Change Type | Description |
|------|-------------|-------------|
| `docs/index.html` | Modified | Added `--rp-*` CSS custom properties in `:root`. Replaced `extend.colors` `rp-*` hex map with semantic alias map pointing at CSS vars. Updated `<body>` class and loading spinner inline styles to use CSS vars. |
| `src/clojkstra/app/theme.cljs` | **New file** | 250-line namespace containing all themed class strings as keyword maps. Single source of truth for all component-level color and typography decisions. |
| `src/clojkstra/app/components/ui.cljs` | Modified | Added `[clojkstra.app.theme :as theme]` require. Replaced all inline `def` color maps and hardcoded class strings with `theme/` lookups. Removed `button-base`, `button-variants`, `button-sizes`, `badge-variants`, `alert-variants` private defs. |
| `src/clojkstra/app/views.cljs` | Modified | Added theme require. Replaced all navbar, footer, shell, overlay, nav-link, skip-link inline class strings with `theme/` lookups. |
| `src/clojkstra/app/pages/home.cljs` | Modified | Added theme require. Replaced hero, stack-section, principles-section, quick-start inline class strings with `theme/` lookups. |
| `src/clojkstra/app/pages/about.cljs` | Modified | Added theme require. Replaced table, emphasis, section heading inline class strings. Added `theme.cljs` entry to the file-map data. |
| `src/clojkstra/app/pages/example.cljs` | Modified | Added theme require. Replaced input, label, feature-panel, detail-panel inline class strings with `theme/` lookups. |

---

## Semantic Alias Map (Layer 2a)

| Alias | CSS Var | Hex | Semantic Role |
|-------|---------|-----|---------------|
| `base` | `--rp-base` | `#232136` | Primary app background |
| `surface` | `--rp-surface` | `#2a273f` | Card / panel background |
| `overlay` | `--rp-overlay` | `#393552` | Badge / input / popup background |
| `on-base` | `--rp-text` | `#e0def4` | Primary readable text |
| `subtle` | `--rp-subtle` | `#908caa` | Secondary text, ghost controls |
| `muted` | `--rp-muted` | `#6e6a86` | Disabled / placeholder / captions |
| `edge` | `--rp-highlight-high` | `#56526e` | All structural borders and separators |
| `selection` | `--rp-highlight-med` | `#44415a` | Hover fill, selected background |
| `lo` | `--rp-highlight-low` | `#2a283e` | Cursorline, low-emphasis bg (reserved) |
| `accent` | `--rp-iris` | `#c4a7e7` | Primary interactive accent, links, focus |
| `accent-hi` | `--rp-foam` | `#9ccfd8` | Accent hover / info body text |
| `danger` | `--rp-love` | `#eb6f92` | Error, destructive actions |
| `warn` | `--rp-gold` | `#f6c177` | Warnings, caution states |
| `ok` | `--rp-pine` | `#3e8fb0` | Success, affirmative states |
| `info-hi` | `--rp-foam` | `#9ccfd8` | Info alert body text |
| `rose` | `--rp-rose` | `#ea9a97` | Modified state, booleans (reserved) |

---

## Component Token Map (Layer 2b — theme.cljs defs)

| Def | Keys | Used By |
|-----|------|---------|
| `btn-base` | (string) | `ui/button` — shared base classes |
| `btn` | `:primary` `:secondary` `:ghost` `:danger` | `ui/button` |
| `btn-size` | `:sm` `:md` `:lg` | `ui/button` |
| `badge-base` | (string) | `ui/badge` |
| `badge` | `:default` `:success` `:warning` `:danger` `:info` | `ui/badge` |
| `card` | `:outer` `:header` `:title` `:sub` `:body` `:footer` | `ui/card` |
| `card-item` | `:outer` `:outer-flex` `:heading` `:body` | `pages/home` stack + principles grids |
| `spinner` | `:svg` | `ui/spinner` |
| `alert-base` | (string) | `ui/alert` |
| `alert` | `:info` `:success` `:warning` `:error` | `ui/alert` |
| `page-title` | `:heading` `:subtitle` | `ui/page-title` |
| `divider` | `:line` `:label` `:rule` | `ui/divider` |
| `code-block` | `:outer` `:lang` `:pre` | `ui/code-block` |
| `notification` | `:outer` `:message` `:dismiss` | `ui/notification` |
| `input` | (string) | `pages/example` input |
| `label` | (string) | `pages/example` label |
| `navbar` | `:outer` `:brand` `:links-row` | `views/app-navbar` |
| `nav-link` | `:base` `:active` `:inactive` | `views/nav-link` |
| `footer` | `:outer` `:copyright` `:link` | `views/app-footer` |
| `shell` | `:root` `:overlay` | `views/app-root`, `views/loading-overlay` |
| `skip-link` | (string) | `views/skip-link` |
| `page-404` | `:heading` `:body` | `views/page-for-route` fallback |
| `table` | `:thead-row` `:th` `:tbody-row` `:td-code` `:td-muted` | `pages/about` file-map table |
| `step-badge` | `:badge` `:text` | `pages/home` quick-start steps |
| `emphasis` | (string) | `pages/about` prose highlights |
| `feature-panel` | `:outer` `:text` | `pages/example` feature flag panel |
| `detail-panel` | (string) | `pages/example` toggle detail |

---

## Migration Guide

### To swap to Rosé Pine (main) or Rosé Pine Dawn

Edit only the `:root` block in `docs/index.html`. Replace the 15 hex values
with the target variant's values from STYLE.org. Every component updates
automatically — no other file changes needed.

Example for Rosé Pine Dawn:
```css
:root {
  --rp-base: #faf4ed;
  --rp-surface: #fffaf3;
  --rp-overlay: #f2e9e1;
  --rp-muted: #9893a5;
  --rp-subtle: #797593;
  --rp-text: #575279;
  --rp-love: #b4637a;
  --rp-gold: #ea9d34;
  --rp-rose: #d7827e;
  --rp-pine: #286983;
  --rp-foam: #56949f;
  --rp-iris: #907aa9;
  --rp-highlight-low: #f4ede8;
  --rp-highlight-med: #dfdad9;
  --rp-highlight-high: #cecacd;
}
```

### To add a new component

1. Add a new `def` in `theme.cljs` with keyword keys for each themed part.
2. Require `[clojkstra.app.theme :as theme]` in your component namespace.
3. Use `(:key theme/your-def)` in your Hiccup class attributes.
4. Never write a color, border-color, text-color, or ring-color class
   directly in a component file — add a token to `theme.cljs` first.

### To change a color decision (e.g. make danger buttons use `warn` instead)

Edit the relevant value in `theme.cljs` only. Example:

```clojure
;; Before
(def btn
  {:danger "bg-danger text-base hover:opacity-90 focus:ring-danger"})

;; After
(def btn
  {:danger "bg-warn text-base hover:opacity-90 focus:ring-warn"})
```

### To add a new semantic alias (e.g. a new status color)

1. Add the CSS custom property to the `:root` block in `docs/index.html`
   if a new palette color is needed (e.g. `--rp-pine` is already there).
2. Add the alias to `extend.colors` in the same file:
   `"new-alias": "var(--rp-pine)"`.
3. Use `text-new-alias`, `bg-new-alias`, `border-new-alias` in `theme.cljs`.

---

## Verification

- `just lint`     — 0 errors, 4 pre-existing warnings (clojure.string
                    false positive, unused subs in core.cljs). None introduced.
- `just fmt`      — all 6 modified `.cljs` files auto-formatted clean.
- `just fmt-check`— passes after fmt.
- `just build`    — exits 0, production bundle emitted to `docs/cljs-out/`.
- Manual grep     — zero `rp-*` tokens outside `theme.cljs`, zero raw hex
                    strings, zero `gray-`/`indigo-`/`red-`/`green-` Tailwind
                    classes in any component file.