# Pipelines & Entries Reference

A **pipeline** groups a set of already-existing effects behind a single, reusable trigger
condition (an **entry**). It's just another effect (`type: "PIPELINE"`) — it uses the same
folder, the same `effects:` list on items, the same `EffectContext`, the same priority
system as every other effect. Nothing about the core effects system changes to support it.

## Table of Contents

- [Why Pipelines](#why-pipelines)
- [Pipeline File Structure](#pipeline-file-structure)
- [How Gating Works](#how-gating-works)
- [Entry Catalog](#entry-catalog)
  - [Combat](#combat)
  - [Mining & Blocks](#mining--blocks)
  - [Interaction](#interaction)
  - [Durability](#durability)
  - [Equipment](#equipment)
  - [Ongoing State](#ongoing-state)
  - [Job Hooks](#job-hooks)
- [Writing a Custom Entry](#writing-a-custom-entry)
- [Known Limitations](#known-limitations)

---

## Why Pipelines

A flat `effects:` list already gives you a shared, priority-sorted `EffectContext` per
event — see [Effect Execution Pipeline](../user-guide/effects.md#effect-execution-pipeline).
What it *can't* do is scope a group of effects to a specific trigger, or reuse that
scoped bundle across items without repeating every effect id each time.

```yaml
# Without a pipeline: AUTO_SELL fires on every BlockBreakEvent AND EntityDeathEvent
effects:
  - "auto_sell_pickaxe"
```

```yaml
# With a pipeline: the same AUTO_SELL effect only ever runs on a kill
effects:
  - "sword_combat"   # type: PIPELINE, entry: "kill", steps: ["auto_sell_pickaxe"]
```

## Pipeline File Structure

Pipelines live in `plugins/zItems/effects/*.yml`, same as any other effect:

```yaml
id: "sword_combat"
type: "PIPELINE"
display-name: "<red>⚔ Combat Pipeline</red>"
entry: "kill"                # references an entry id, see below
steps:
  - "combat_xp_boost"        # existing effect ids, executed in this order
  - "auto_sell_pickaxe"
```

- **`entry`** — the id of an `entries/*.yml` file (see [Entry Catalog](#entry-catalog)).
- **`steps`** — a list of *existing* effect ids, run in declaration order against the same
  `EffectContext` as the pipeline itself. There is no separate "exit" concept: whatever
  effect you place last (e.g. `AUTO_SELL`, or nothing) is the exit.

> **Load order matters.** Effect files are loaded in directory order, not dependency
> order — any effect referenced under `steps` must already be registered by the time the
> pipeline's file is parsed. Keep referenced step files sorted before the pipeline file
> until the loader becomes two-pass.

Entries are their own file type, in `plugins/zItems/entries/*.yml`:

```yaml
id: "kill"
type: "KILL"
```

## How Gating Works

The `PIPELINE` effect is registered as an `EffectHandler.AnyEventEffectHandler` — a
handler that runs on *every* dispatched event, because it doesn't decide anything about
event affinity itself. Instead:

1. `PipelineEffectHandler.handle()` resolves the pipeline's `entry` to an `EntryHandler`
   and calls `test(context, settings)`.
2. If the entry doesn't match, the pipeline does nothing — no steps run.
3. If it matches, each step effect runs in order, exactly like a flat `effects:` list,
   sharing the same `EffectContext` (drops, affected blocks, the triggering event).

```java
@AutoEffect("PIPELINE")
public class PipelineEffectHandler implements EffectHandler.AnyEventEffectHandler<PipelineSettings> {
    @Override
    public void handle(EffectContext context, PipelineSettings settings) {
        EntryHandler<?> entryHandler = Registry.get(EntryHandlersRegistry.class).getById(settings.entry().type());
        if (entryHandler == null || !test(entryHandler, context, settings.entry().settings())) {
            return;
        }
        for (Effect step : settings.steps()) {
            EffectHandler<?> handler = Registry.get(HandlersRegistry.class).getById(step.type());
            if (handler != null && handler.canApply(context.event())) {
                handler.handle(context, step.settings());
            }
        }
    }
}
```

An `Entry` only ever answers "should this pipeline run?" — it never acts on the item, so
`EntryHandler` is a separate, minimal interface (`boolean test(EffectContext, T)`), not a
variant of `EffectHandler`. This keeps a gate class from having to carry a no-op `handle()`
just to satisfy a contract it doesn't need.

---

## Entry Catalog

### Combat

| Entry | Underlying Event | Notes |
|---|---|---|
| `ATTACK` | `EntityDamageByEntityEvent` | Item source is the attacker's weapon. |
| `KILL` | `EntityDeathEvent` | Item source is the killer's weapon. |
| `DEATH` | `PlayerDeathEvent` | Item source is whatever the *dying* player was holding — a dedicated extractor overrides the inherited `EntityDeathEvent` one (which would otherwise resolve to the killer's weapon). |
| `DEFEND` | synthetic `PlayerDefendEvent`, from real `EntityDamageEvent` | Dispatched once per equipped item (helmet/chestplate/leggings/boots/main hand/off hand) by `DefendTransitionListener`. Wrapped instead of reusing the raw damage event so an `ATTACK`-gated pipeline on armor can't fire when its wearer gets hit. |
| `PROJECTILE_SHOOT` | `EntityShootBowEvent` | Item source is the bow/crossbow. |
| `PROJECTILE_HIT` | `ProjectileHitEvent` | Traces the projectile back to its shooter; item source is whatever they still have in their main hand at hit time. |

### Mining & Blocks

| Entry | Underlying Event |
|---|---|
| `BLOCK_BREAK` | `BlockBreakEvent` |
| `BLOCK_PLACE` | `BlockPlaceEvent` |
| `BLOCK_DROP` | `BlockDropItemEvent` |

### Interaction

| Entry | Underlying Event | Notes |
|---|---|---|
| `INTERACT` | `PlayerInteractEvent` | |
| `INTERACT_ENTITY` | `PlayerInteractEntityEvent` | |
| `CONSUME` | `PlayerItemConsumeEvent` | Item source is the item actually being eaten/drunk (`event.getItem()`), not necessarily the main hand. |
| `DROP` | `PlayerDropItemEvent` | Item source is the dropped item, not the main hand. |
| `PICKUP` | `EntityPickupItemEvent` | Not a `PlayerEvent` — only matches when a player picks the item up. |
| `SWAP_HANDS` | `PlayerSwapHandItemsEvent` | Item source is whatever ends up in the main hand. |
| `FISH` | `PlayerFishEvent` | |
| `BUCKET_FILL` | `PlayerBucketFillEvent` | |
| `BUCKET_EMPTY` | `PlayerBucketEmptyEvent` | |
| `SHEAR` | `PlayerShearEntityEvent` | |

### Durability

| Entry | Underlying Event | Notes |
|---|---|---|
| `ITEM_DAMAGE` | `PlayerItemDamageEvent` | Item source is the specific item taking damage — may be armor. |
| `ITEM_BREAK` | `PlayerItemBreakEvent` | Item source is the item that just broke. |
| `ITEM_MEND` | `PlayerItemMendEvent` | Item source is the item being repaired by Mending. |

### Equipment

| Entry | Underlying Event | Notes |
|---|---|---|
| `HELD` | `PlayerItemHeldEvent` | Item source is the item *about to become* held (the new slot). |
| `UNHELD` | synthetic `PlayerItemUnheldEvent` | See [Known Limitations](#known-limitations) — a dispatch can only resolve one item per real event, so the "item that just stopped being held" direction is fired manually by `HeldTransitionListener`, wrapped so it can't be confused with `HELD`. |
| `ARMOR_EQUIP` | `PlayerArmorChangeEvent` (Paper-only, `com.destroystokyo.paper`) | Lives in `versions/paper`, annotated `@PaperOnly` — this event doesn't exist in spigot-api at all. Item source is the armor piece being put on. |
| `ARMOR_UNEQUIP` | synthetic `PlayerArmorUnequipEvent` | Same limitation as `UNHELD`. The synthetic event itself lives in `:common` (no Paper dependency), so this entry works on the root module without `@PaperOnly` — it just never matches on plain Spigot, since nothing ever constructs it there. |

### Ongoing State

| Entry | Underlying Event | Notes |
|---|---|---|
| `SNEAKING` | synthetic `PlayerTickEvent` | The only entry not backed by a discrete Bukkit event — sneaking is an ongoing state, not a moment. `TickDispatcher` fires a `PlayerTickEvent` for every online player's main-hand item every 10 ticks (0.5s); `SNEAKING` matches when `Player#isSneaking()` is true at that instant. |

### Job Hooks

Registered manually by `JobsHook`/`ZJobsHook` (not `@AutoEntry`-scanned — see
[Hook System](hooks.md)), only when the corresponding plugin is installed. Both hooks
register under the same entry ids, since only one is ever active at a time.

| Entry | Underlying Event |
|---|---|
| `JOBS_EXP_GAIN` | `JobsExpGainEvent` (Jobs Reborn) / `JobExpGainEvent` (ZJobs) |
| `JOBS_MONEY_GAIN` | `JobsPrePaymentEvent` (Jobs Reborn) / `JobMoneyGainEvent` (ZJobs) |

---

## Writing a Custom Entry

An entry is a class implementing `EntryHandler<T>`, annotated `@AutoEntry("ID")`:

```java
@AutoEntry("ATTACK")
public class AttackEntry implements EntryHandler<EmptyEntrySettings> {
    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof EntityDamageByEntityEvent;
    }
}
```

If the underlying event needs a specific `ItemSourceExtractor` (because the generic
`PlayerEvent` fallback would resolve the wrong item — main hand instead of the item
actually involved), register one the same way any other effect would:

```java
@AutoExtractor(PlayerItemDamageEvent.class)
public class PlayerItemDamageExtractor implements ItemSourceExtractor<PlayerItemDamageEvent> {
    @Override
    public ExtractionResult extract(PlayerItemDamageEvent event) {
        return new ExtractionResult(event.getPlayer(), event.getItem());
    }
}
```

For a condition that needs *two* items from one real event (like `HELD`/`UNHELD`), don't
try to fit it through `ItemSourceExtractor` — that mechanism resolves exactly one item per
event. Instead, write a small dedicated `@AutoListener` that listens to the real event and
manually calls `EffectsDispatcher#dispatch` a second time, wrapped in a synthetic event:

```java
@AutoListener
public class HeldTransitionListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHeld(PlayerItemHeldEvent event) {
        ItemStack oldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        if (oldItem == null || oldItem.getType().isAir()) return;
        JavaPlugin.getPlugin(ItemsPlugin.class).getDispatcher()
                .dispatch(event.getPlayer(), oldItem, new PlayerItemUnheldEvent(event.getPlayer()));
    }
}
```

The automatic dispatch (via the extractor) keeps handling the "primary" direction; the
listener only adds the secondary one. Never reuse the real event class for the synthetic
wrapper if a *different* entry already tests `instanceof` against it — see the `DEFEND`
row above for why that would cross-fire.

## Known Limitations

- **Effect load order.** A pipeline's `steps` reference other effects by id, resolved at
  parse time — the referenced effect file must already be loaded. Files are read in
  directory order, not dependency order.
- **One item per dispatch.** Events that carry two items in one firing (`PlayerItemHeldEvent`,
  `PlayerArmorChangeEvent`) need the synthetic-event workaround above for their second
  direction. `DEFEND` sidesteps this differently — by dispatching once per equipped slot.
- **Not covered**: `JOIN`/`QUIT`, `TOGGLE_SPRINT`, `RIPTIDE`, `CRAFT`, `ENCHANT` — same
  mechanical pattern as everything above, just not built yet.

---

## Next Steps

- **[Effects System](../user-guide/effects.md)** - The base effect system pipelines build on
- **[Effect Handlers Reference](effect-handlers.md)** - Built-in effects usable as pipeline steps
- **[Hook System](hooks.md)** - How the Jobs/ZJobs entries are registered conditionally
