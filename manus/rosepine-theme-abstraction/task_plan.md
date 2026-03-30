# Task: Rosé Pine Moon — Semantic Theme Abstraction

## Goal
Replace the current shallow `rp-*` Tailwind palette token approach with a
two-layer abstraction:

1. **Layer 1 — CSS custom properties + semantic Tailwind aliases** (`index.html`
   + Tailwind config): palette values live in CSS vars; Tailwind aliases expose
   *semantic* names (`accent`, `surface`, `muted`, etc.) not palette names.

2. **Layer 2 — `theme.cljs` namespace**: a single ClojureScript map of
   component-level style tokens. Components import from `theme.cljs` instead of
   hardcoding Tailwind class strings inline.

The result: reskinning the app means editing **one config block** (palette) and
**one ClojureScript file** (component style decisions). No color strings in
component files.

---

## Phases

### Phase 1 — Research & Inventory  [x] DONE
- Audit all Tailwind classes currently used across every `.cljs` file
- Identify every unique semantic role in use (background, surface, text,
  accent, danger, muted, border, etc.)
- Map current `rp-*` tokens → proposed semantic alias names
- Identify all compound / repeated class strings that belong in `theme.cljs`
- Save findings to `notes.md`

### Phase 2 — Design the Two Layers  [x] DONE
- Draft the CSS custom properties block (palette vars)
- Draft the Tailwind `extend.colors` semantic alias map (points to `var(--*)`)
- Draft the `theme.cljs` token map structure (buttons, badges, cards, alerts,
  inputs, navbar, footer, code-block, notification, page scaffolding)
- Resolve any Tailwind JIT safety concerns (static class strings)
- Save design decisions to `notes.md`

### Phase 3 — Implement  [x] DONE
- Update `docs/index.html`: CSS vars block + semantic Tailwind aliases
- Create `src/clojkstra/app/theme.cljs`
- Refactor `components/ui.cljs` to consume `theme/` tokens
- Refactor `views.cljs` to consume `theme/` tokens
- Refactor `pages/home.cljs`, `pages/about.cljs`, `pages/example.cljs`
- Verify zero residual `rp-*` or raw color class strings in component files
- Run `just lint` and `just fmt-check`

### Phase 4 — Verify & Deliver  [x] DONE
- Confirm `just build` passes cleanly
- Adversarial self-review: check for leaking palette names, hardcoded colors,
  incomplete token coverage
- Write `deliverable.md` with summary, file listing, mapping table, and
  migration guide
- Mark all phases DONE

---

## File Index

| File | Role |
|------|------|
| `task_plan.md` | This file — phase tracker |
| `notes.md` | Research findings and design decisions |
| `deliverable.md` | Final summary output |

---

## Status
- [x] Phase 1 — Research & Inventory
- [x] Phase 2 — Design the Two Layers
- [x] Phase 3 — Implement
- [x] Phase 4 — Verify & Deliver