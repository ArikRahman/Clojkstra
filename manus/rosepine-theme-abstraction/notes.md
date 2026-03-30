# Research Notes — Rosé Pine Moon Semantic Theme Abstraction

---

## 1. Complete Tailwind Class Audit

### components/ui.cljs

#### button
- Base: `inline-flex items-center justify-center font-medium rounded-lg transition-colors duration-150 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-rp-base disabled:opacity-40 disabled:cursor-not-allowed`
- `:primary`   → `bg-rp-iris text-rp-base hover:bg-rp-foam focus:ring-rp-iris`
- `:secondary` → `bg-rp-overlay text-rp-text border border-rp-highlight-high hover:bg-rp-highlight-med focus:ring-rp-highlight-high`
- `:ghost`     → `bg-transparent text-rp-subtle hover:text-rp-text hover:bg-rp-overlay focus:ring-rp-highlight-high`
- `:danger`    → `bg-rp-love text-rp-base hover:opacity-90 focus:ring-rp-love`
- Sizes: `px-3 py-1.5 text-sm` / `px-4 py-2 text-sm` / `px-6 py-3 text-base`

#### badge
- Base: `inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium`
- `:default` → `bg-rp-overlay text-rp-subtle`
- `:success`  → `bg-rp-overlay text-rp-pine`
- `:warning`  → `bg-rp-overlay text-rp-gold`
- `:danger`   → `bg-rp-overlay text-rp-love`
- `:info`     → `bg-rp-overlay text-rp-iris`

#### card
- Outer: `bg-rp-surface rounded-xl border border-rp-highlight-high overflow-hidden`
- Header: `px-6 py-4 border-b border-rp-highlight-high`
- Header title: `text-base font-semibold text-rp-text`
- Header subtitle: `mt-1 text-sm text-rp-muted`
- Body: `px-6 py-4`
- Footer: `px-6 py-4 bg-rp-overlay/50 border-t border-rp-highlight-high`

#### spinner
- Wrapper: `flex items-center justify-center`
- SVG: `animate-spin text-rp-iris` + size class

#### alert
- Base wrapper: `flex gap-3 p-4 rounded-lg border`
- `:info`    → `bg-rp-overlay border-rp-iris text-rp-foam`
- `:success` → `bg-rp-overlay border-rp-pine text-rp-pine`
- `:warning` → `bg-rp-overlay border-rp-gold text-rp-gold`
- `:error`   → `bg-rp-overlay border-rp-love text-rp-love`

#### page-title
- Wrapper: `mb-8`
- h1: `text-3xl font-bold text-rp-text tracking-tight`
- Subtitle: `mt-2 text-base text-rp-muted`

#### divider
- Labelled: line `w-full border-t border-rp-highlight-high`, label span `px-3 bg-rp-base text-sm text-rp-muted`
- Plain: `my-6 border-rp-highlight-high`

#### code-block
- Outer: `relative rounded-lg overflow-hidden bg-rp-base border border-rp-highlight-high`
- Lang tag: `absolute top-0 right-0 px-3 py-1 text-xs text-rp-muted font-mono bg-rp-surface rounded-bl-lg`
- Pre: `overflow-x-auto p-4 pt-6 text-sm text-rp-subtle font-mono leading-relaxed`

#### notification
- Outer: `flex items-start gap-3 bg-rp-surface border border-rp-highlight-high rounded-lg shadow-xl px-4 py-3 max-w-sm w-full`
- Message: `flex-1 text-sm text-rp-text`
- Dismiss btn: `flex-shrink-0 text-rp-muted hover:text-rp-text transition-colors text-lg leading-none`

---

### views.cljs

#### app-root shell
- `min-h-screen flex flex-col bg-rp-base text-rp-text font-sans antialiased`

#### loading-overlay
- `fixed inset-0 z-50 flex items-center justify-center bg-rp-base/80 backdrop-blur-sm`

#### error-banner (positional wrapper only, delegates to alert)
- `sticky top-16 z-40 px-4 sm:px-6 lg:px-8 pt-3`
- Inner: `max-w-5xl mx-auto`

#### nav-link
- Active:   `bg-rp-overlay text-rp-iris`
- Inactive: `text-rp-subtle hover:text-rp-text hover:bg-rp-overlay`
- Base:     `px-3 py-2 rounded-md text-sm font-medium transition-colors duration-150`

#### app-navbar
- `bg-rp-surface border-b border-rp-highlight-high sticky top-0 z-50`
- Inner: `max-w-5xl mx-auto px-4 sm:px-6 lg:px-8`
- Row: `flex items-center justify-between h-16`
- Brand: `text-xl font-bold text-rp-iris tracking-tight`
- Links row: `flex items-center gap-1`

#### app-footer
- `mt-auto border-t border-rp-highlight-high bg-rp-surface`
- Inner: `max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-6 flex flex-col sm:flex-row items-center justify-between gap-3`
- Copyright: `text-xs text-rp-muted`
- Links: `text-xs text-rp-muted hover:text-rp-iris transition-colors`

#### skip-link
- `sr-only focus:not-sr-only focus:fixed focus:top-2 focus:left-2 focus:z-50 focus:px-4 focus:py-2 focus:bg-rp-iris focus:text-rp-base focus:rounded-lg focus:text-sm focus:font-medium`

#### 404 page
- h1: `text-3xl font-bold text-rp-text`
- p:  `text-rp-muted text-sm max-w-xs`

---

### pages/home.cljs

#### hero section
- Wrapper: `py-20 text-center flex flex-col items-center gap-5`
- h1: `text-5xl font-bold text-rp-text tracking-tight`  (NOTE: uses text-rp-text directly — same as page-title h1 pattern; belongs in theme)
- p:  `text-xl text-rp-subtle max-w-xl leading-relaxed`   (NOTE: text-rp-subtle for hero body)
- Badge row: `flex flex-wrap justify-center gap-3 mt-2`
- Button row: `flex flex-wrap justify-center gap-3 mt-4`

#### stack-section items
- `bg-rp-surface border border-rp-highlight-high rounded-xl p-5 hover:border-rp-iris hover:bg-rp-overlay transition-all duration-150`
- Item name: `font-semibold text-rp-text text-sm mb-1`
- Item desc: `text-sm text-rp-muted leading-relaxed`

#### principles-section items
- `bg-rp-surface border border-rp-highlight-high rounded-xl p-6 flex gap-4 hover:border-rp-iris transition-all duration-150`
- Title: `font-semibold text-rp-text text-sm mb-1`
- Body:  `text-sm text-rp-muted leading-relaxed`

#### quick-start steps
- Step number badge: `flex-shrink-0 w-7 h-7 rounded-full bg-rp-overlay text-rp-iris text-sm font-bold flex items-center justify-center`
- Step text: `text-sm text-rp-subtle leading-relaxed pt-0.5`

---

### pages/about.cljs

#### file-map table
- thead row: `border-b border-rp-highlight-high`
- th: `text-left py-2 pr-4 font-semibold text-rp-subtle`
- tbody row: `border-t border-rp-highlight-high hover:bg-rp-overlay transition-colors`
- file code: `text-xs text-rp-iris font-mono`
- desc td: `py-2.5 text-rp-muted`

#### what-section inline emphasis
- `text-rp-text font-medium`

---

### pages/example.cljs

#### controlled-input label
- `block text-sm font-medium text-rp-subtle mb-1`

#### controlled-input input
- `w-full rounded-lg border border-rp-highlight-high bg-rp-overlay text-rp-text px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-rp-iris focus:border-transparent transition placeholder-rp-muted`

#### feature-flag enabled panel
- `rounded-lg bg-rp-overlay border border-rp-iris p-4`
- Text: `text-sm text-rp-foam`

#### toggle-detail panel
- `rounded-lg border border-rp-highlight-high bg-rp-overlay p-4 text-sm text-rp-subtle leading-relaxed`

---

## 2. Semantic Role Inventory

From the audit above, these semantic roles are actually in use (mapped to current rp-* token):

| Semantic Role        | Current Token        | Hex       | Used For |
|----------------------|----------------------|-----------|----------|
| `base`               | `rp-base`            | `#232136` | App bg, code-block bg, divider label bg, loading overlay |
| `surface`            | `rp-surface`         | `#2a273f` | Cards, navbar, footer, notification bg |
| `overlay`            | `rp-overlay`         | `#393552` | Badges bg, alert bg, secondary/ghost btn bg, hover states, input bg, feature panel |
| `text`               | `rp-text`            | `#e0def4` | Primary headings, body copy, active labels |
| `subtle`             | `rp-subtle`          | `#908caa` | Secondary text, ghost btn default, hero body, step text, code pre |
| `muted`              | `rp-muted`           | `#6e6a86` | Disabled/placeholder text, subtitles, footer copy, lang tag, divider label |
| `border`             | `rp-highlight-high`  | `#56526e` | All borders (cards, inputs, nav, footer, table, divider) |
| `selection`          | `rp-highlight-med`   | `#44415a` | Secondary btn hover bg |
| `accent`             | `rp-iris`            | `#c4a7e7` | Primary btn bg, spinner, brand name, active nav, links, skip-link, step numbers, hover borders, focus rings, file code color |
| `accent-hover`       | `rp-foam`            | `#9ccfd8` | Primary btn hover bg, alert info text, feature panel text |
| `danger`             | `rp-love`            | `#eb6f92` | Danger btn, error alert border+text, danger badge text |
| `success`            | `rp-pine`            | `#3e8fb0` | Success alert border+text, success badge text |
| `warning`            | `rp-gold`            | `#f6c177` | Warning alert border+text, warning badge text |
| `info`               | `rp-iris`            | `#c4a7e7` | Info badge text (same as accent — intentional) |

Observations:
- `rp-iris` doubles as both the accent color AND the info semantic role — fine, iris is the info/focus color per Rosé Pine spec.
- `rp-foam` is only used as accent-hover (primary btn) and info alert text — a subtle secondary accent.
- `rp-highlight-low` is NOT currently used — was in the CSS vars but no component references it. Could be used for cursorline / low-emphasis hover in future.
- `rp-rose` is NOT used — available for booleans/modified state if needed.

---

## 3. Proposed Semantic Alias Map (Layer 1 — Tailwind config)

These are the names components will use. They point to CSS vars, NOT hex.

```
// Tailwind extend.colors — semantic aliases
"base"          → "var(--rp-base)"       // primary app background
"surface"       → "var(--rp-surface)"    // panel / card background
"overlay"       → "var(--rp-overlay)"    // popup / badge / input background
"on-base"       → "var(--rp-text)"       // primary text on base
"on-surface"    → "var(--rp-text)"       // primary text on surface (same value)
"subtle"        → "var(--rp-subtle)"     // secondary text
"muted"         → "var(--rp-muted)"      // disabled / placeholder / caption text
"border"        → "var(--rp-highlight-high)"   // all structural borders
"selection"     → "var(--rp-highlight-med)"    // hover fill, selected bg
"accent"        → "var(--rp-iris)"       // primary interactive accent
"accent-hover"  → "var(--rp-foam)"       // accent hover state
"danger"        → "var(--rp-love)"       // error / destructive
"warn"          → "var(--rp-gold)"       // warning
"ok"            → "var(--rp-pine)"       // success
"info"          → "var(--rp-iris)"       // informational (same as accent)
"info-text"     → "var(--rp-foam)"       // info alert body text (foam)
```

Decision rationale:
- `on-base` / `on-surface` both resolve to `rp-text` — they're aliases for clarity not distinct values.
- `accent` and `info` resolving to the same var (`rp-iris`) is correct per Rosé Pine spec.
- Naming avoids Tailwind built-in names (`red`, `blue`, `green`, etc.) entirely.
- Avoids `rp-` prefix — that was the palette layer; these are the *semantic* layer.

**IMPORTANT — Tailwind JIT safety:**
Tailwind CDN (Play CDN / JIT) scans for complete class strings. Since all class strings in `.cljs` files are string literals (not dynamically constructed from partial strings), the scanner will see them. The semantic alias names (`text-accent`, `bg-surface`, `border-border`, etc.) will be in complete strings — safe.

One awkward alias: `border-border` (Tailwind utility `border-{color}` + alias name `border`).
Decision: rename the alias from `border` to `edge` → `border-edge`, `divide-edge`. Cleaner.

Revised alias: `"edge" → "var(--rp-highlight-high)"`.

---

## 4. theme.cljs Token Map Design (Layer 2)

Namespace: `clojkstra.app.theme`

Structure: flat `def` per component group, each a map of keyword → class string.
Components require `[clojkstra.app.theme :as theme]` and do `(:primary theme/btn)`.

### Token groups needed:

```
theme/btn            — button variant class strings (full compound string)
theme/btn-base       — button base classes (shared across all variants)
theme/btn-size       — button size classes
theme/badge          — badge variant class strings
theme/badge-base     — badge base classes
theme/card           — card part class strings (outer, header, body, footer)
theme/alert          — alert variant class strings
theme/spinner        — spinner svg class, sizes
theme/page-title     — h1 and subtitle classes
theme/divider        — line and label classes
theme/code-block     — outer, lang-tag, pre classes
theme/notification   — outer, message, dismiss classes
theme/nav-link       — active and inactive classes, base
theme/navbar         — outer, inner, row, brand, links-row classes
theme/footer         — outer, inner, copyright, link classes
theme/shell          — app root, main content wrapper classes
theme/overlay-screen — loading overlay classes
theme/skip-link      — skip link classes
theme/input          — text input classes
theme/label          — form label classes
theme/card-item      — hover card item (stack/principles cards on home page)
theme/table          — thead-row, th, tbody-row, td-file, td-desc classes
theme/emphasis       — inline emphasis span class
theme/step-badge     — numbered step indicator (quick-start on home page)
theme/feature-panel  — feature flag enabled panel
theme/detail-panel   — toggle detail panel
theme/page-404       — 404 heading and paragraph classes
```

### Key design decisions:
1. Each token value is a **complete Tailwind class string** — no string concatenation in components.
2. Maps use keyword keys matching the component's internal concept (`:outer`, `:header`, `:body` etc.).
3. No logic in `theme.cljs` — pure data. Components do `(theme/card :outer)` not conditional logic.
4. Structural/layout classes (flex, grid, gap, padding, max-width) stay in component files — only color/typography/border/shadow tokens move to theme.cljs. This preserves the component's layout responsibility.

**Clarification on scope:** `theme.cljs` owns:
- Background colors
- Text colors
- Border colors
- Ring/focus colors
- Shadow variants (themed)
- Opacity modifiers on theme colors

`theme.cljs` does NOT own:
- flex / grid / gap / padding / margin / width / height
- rounded-* (border radius — structural)
- text-sm / text-xs / font-* (typography scale — structural)
- overflow / position / z-index

This keeps theme.cljs strictly about *appearance* and components about *structure*.

---

## 5. Revised Tailwind Alias List (post `edge` rename)

```js
colors: {
  "base":         "var(--rp-base)",
  "surface":      "var(--rp-surface)",
  "overlay":      "var(--rp-overlay)",
  "on-base":      "var(--rp-text)",
  "subtle":       "var(--rp-subtle)",
  "muted":        "var(--rp-muted)",
  "edge":         "var(--rp-highlight-high)",
  "selection":    "var(--rp-highlight-med)",
  "lo":           "var(--rp-highlight-low)",
  "accent":       "var(--rp-iris)",
  "accent-hi":    "var(--rp-foam)",
  "danger":       "var(--rp-love)",
  "warn":         "var(--rp-gold)",
  "ok":           "var(--rp-pine)",
  "info-hi":      "var(--rp-foam)",
  "rose":         "var(--rp-rose)",
}
```

Notes:
- `on-base` covers both "on base" and "on surface" — same Rosé Pine `text` value.
- `lo` = `highlight-low` — reserved for cursorline/low-emphasis hovers, not used yet.
- `accent-hi` and `info-hi` both resolve to `foam` — distinct aliases make intent clear at usage site.
- `rose` included for completeness (booleans, modified state) even if unused currently.
- Dropped `on-surface` (redundant with `on-base`), dropped `info` (use `accent` — same value).

---

## 6. File Change Plan

| File | Change |
|------|--------|
| `docs/index.html` | (1) Add CSS custom properties block for all 15 Rosé Pine Moon vars. (2) Replace `extend.colors` with semantic alias map. (3) Update body class from `bg-rp-base text-rp-text` to `bg-base text-on-base`. (4) Update loading spinner hex values to use the new CSS vars or equivalent semantic hex. |
| `src/clojkstra/app/theme.cljs` | **NEW FILE** — all themed class strings as keyword maps. |
| `src/clojkstra/app/components/ui.cljs` | Require `theme`, replace all themed inline strings with `(theme/X :key)` lookups. |
| `src/clojkstra/app/views.cljs` | Require `theme`, replace themed inline strings. |
| `src/clojkstra/app/pages/home.cljs` | Require `theme`, replace themed inline strings. |
| `src/clojkstra/app/pages/about.cljs` | Require `theme`, replace themed inline strings. |
| `src/clojkstra/app/pages/example.cljs` | Require `theme`, replace themed inline strings. |

---

## 7. Linting / Formatting Notes

- `just lint` runs clj-kondo — no issues expected from pure data maps.
- `just fmt-check` runs cljfmt — `theme.cljs` alignment-style maps should be fine with cljfmt defaults but verify after writing.
- `just build` must pass cleanly — shadow-cljs compilation will catch any require or symbol errors.
```

Now I'll write the notes, then move straight to implementation. Let me create the notes file and begin Phase 3.