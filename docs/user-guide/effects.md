# Effects System

This guide explains how the zItems effects system works and how to configure effects for your custom items.

## What are Effects?

**Effects** are reusable abilities that can be applied to items. They define behaviors like:
- Mining in a 3x3 area (Hammer)
- Auto-selling drops (Auto-Sell)
- Automatically smelting ores (Melt Mining)
- And much more...

### Key Concepts

1. **Effect Definitions** (`effects/*.yml`) - Reusable effect templates
2. **Item Configuration** (`items/*.yml`) - Items reference effects by ID
3. **Effect Handlers** - Java classes that implement the behavior
4. **Effect Context** - Shared data during effect execution

---

## Effect File Structure

Effects are defined in `plugins/zItems/effects/*.yml`:

### Minimal Effect

```yaml
id: "my_hammer"
type: "HAMMER"
```

That's it! The effect will use default settings.

### Complete Effect Template

```yaml
# ========================================
# Basic Information
# ========================================
id: "super_hammer"                      # Unique ID (required)
type: "HAMMER"                          # Handler type (required)
display-name: "<gold>⚒ HAMMER</gold>"  # Display name (optional)

# ========================================
# Effect-Specific Settings
# ========================================
# Settings vary by effect type
materials:
  - STONE
  - COBBLESTONE
width: 3
height: 3
depth: 1
damage: 1

# ========================================
# Effect Representation (Optional)
# ========================================
representation:
  # Item appearance
  material: GOLDEN_PICKAXE
  display-name: "<yellow>Hammer Stone</yellow>"
  lore:
    - "<gray>Apply to pickaxe"
  custom-model-data: 100

  # Application method
  applicator-type: SMITHING_TABLE

  # Smithing requirements
  template:
    item: "item:NETHERITE_UPGRADE_SMITHING_TEMPLATE"

  # Applicability
  applicable-materials:
    - DIAMOND_PICKAXE
    - NETHERITE_PICKAXE
```

---

## Using Effects in Items

### Method 1: Base Effects

Add effects directly to item configuration:

```yaml
id: "super_pickaxe"
material: DIAMOND_PICKAXE
display-name: "<gold>Super Pickaxe</gold>"

effects:
  - super_hammer
  - auto_smelt
  - xp_boost
```

**When applied**: During item creation (`ZItem.build()`)

**Shown in lore**: Controlled by `base-effects-visible` setting

### Method 2: Additional Effects

Apply effects via commands or smithing:

```bash
# Via command
/zitems effect apply super_hammer

# Via smithing table
# Use effect representation items
```

**When applied**: After item creation
**Shown in lore**: Controlled by `additional-effects-visible` setting

---

## Effect Display Control

### Per-Item Settings

```yaml
id: "my_pickaxe"
material: DIAMOND_PICKAXE

effects:
  - hammer
  - vein_miner
  - auto_sell

# Display control
nb-effects-view: -1                  # -1=all, 0=none, >0=limit
base-effects-visible: true           # Show base effects
additional-effects-visible: true     # Show added effects

# Effect restrictions
allow-additional-effects: true       # Can add more effects
disabled-effects:                    # Cannot add these
  - "silk_spawner"
```

### Global Default

In `config.yml`:

```yaml
default-nb-effects-view: -1  # Default for all items
```

### Effect Lore Customization

In `messages.yml`:

```yaml
effects-lore-header: ""                          # Empty line before
effects-lore-title: "<gray>Effects"              # Section title
effects-lore-line: "<dark_gray>- <effect>"       # Each effect
effects-lore-more: "<dark_gray>- <white>And More..."  # When limited
```

**Result in item lore**:
```
Super Pickaxe

Effects
- ⚒ HAMMER
- 🔥 AUTO SMELT
- And More...
```

---

## Effect Types

Effects come in four handler types:

### SingleEventEffectHandler

Listens to **one specific event** type.

**Example**: Hammer effect listens to `BlockBreakEvent`

```java
@AutoEffect("HAMMER")
public class Hammer
    implements EffectHandler.SingleEventEffectHandler<HammerSettings, BlockBreakEvent> {
    // ...
}
```

**Common Single-Event Effects**:
- Hammer (BlockBreakEvent)
- Vein Mining (BlockBreakEvent)
- Silk Spawner (BlockBreakEvent)
- XP Boost (BlockBreakEvent)

### MultiEventEffectHandler

Listens to **multiple event** types.

**Example**: Auto-Sell listens to both `BlockBreakEvent` and `EntityDeathEvent`

```java
@AutoEffect("AUTO_SELL")
public class AutoSell
    implements EffectHandler.MultiEventEffectHandler<AutoSellSettings> {

    @Override
    public Set<Class<? extends Event>> eventTypes() {
        return Set.of(BlockBreakEvent.class, EntityDeathEvent.class);
    }
}
```

**Common Multi-Event Effects**:
- Auto-Sell (BlockBreakEvent, EntityDeathEvent)
- Absorption (BlockBreakEvent, BlockDropItemEvent, EntityDropItemEvent)
- Farming Hoe (BlockBreakEvent, PlayerInteractEvent)

### NoEventEffectHandler

Applied **once** when effect is added to item (no events).

**Example**: Unbreakable effect sets item meta immediately

```java
@AutoEffect("UNBREAKABLE")
public class Unbreakable
    implements EffectHandler.NoEventEffectHandler<EmptySettings> {

    @Override
    public void handle(EffectContext context, EmptySettings settings) {
        context.itemSource().editMeta(meta -> {
            meta.setUnbreakable(true);
        });
    }
}
```

**Common NoEvent Effects**:
- Unbreakable
- Attributes Applicator
- Enchants Applicator

### AnyEventEffectHandler

Runs on **every** dispatched event — `canApply()` is unconditionally `true`, because this
handler doesn't decide event affinity itself. Only ever safe when something else gates its
behavior; used by the `PIPELINE` effect, which delegates that decision to its configured
**entry**.

**Example**: The `PIPELINE` effect, which only actually does anything once its entry matches

```java
@AutoEffect("PIPELINE")
public class PipelineEffectHandler
    implements EffectHandler.AnyEventEffectHandler<PipelineSettings> {
    // ...
}
```

See **[Pipelines & Entries Reference](../advanced/pipelines.md)** for the full picture:
grouping effects behind a reusable trigger condition, and the built-in entry catalog
(combat, mining, interaction, equipment, ongoing state like `SNEAKING`, and job hooks).

---

## Effect Execution Pipeline

### How Effects Work Together

When an event occurs (e.g., mining a block):

```
1. Event fires (BlockBreakEvent)
2. Extract ItemStack from event
3. Load effects from item's PDC
4. Create EffectContext
5. Find handlers for this event type
6. Sort handlers by priority
7. Execute each handler sequentially
8. Apply final state (break blocks, drop items)
```

### Effect Context

The `EffectContext` is shared across all handlers for one event:

```java
public interface EffectContext {
    Event event();                      // The trigger event
    Player executor();                  // The player
    ItemStack itemSource();             // The item
    List<ItemStack> drops();            // Accumulated drops
    Set<Block> affectedBlocks();        // Blocks to break
    void addDrop(ItemStack drop);       // Add a drop
    void addDrops(Collection<ItemStack> drops);
}
```

**Key Point**: Each handler sees modifications from previous handlers!

**Example Flow**:
```
1. Hammer: Finds blocks in 3x3 area, adds to affectedBlocks
2. Melt Mining: Converts ore drops to smelted versions
3. Auto-Sell: Sells the smelted drops
4. Final: Break blocks, spawn remaining drops
```

---

## Effect Priority

Priority determines execution order (higher = earlier):

| Priority | Execution | Common Effects |
|----------|-----------|----------------|
| 1 | First | Hammer, Vein Mining, Attributes Applicator |
| 0 | Middle | Most effects (Silk Spawner, XP Boost, etc.) |
| -1 | Last | Auto-Sell, Absorption |

**Why This Matters**:

```yaml
effects:
  - hammer          # Priority 1: Collects blocks first
  - melt_mining     # Priority 0: Smelts the drops
  - auto_sell       # Priority -1: Sells smelted drops
```

If Auto-Sell ran first, it would sell before Melt Mining could smelt!

---

## Effect Incompatibilities

Some effects cannot work together:

### Built-in Incompatibilities

```java
@AutoEffect("HAMMER")
@IncompatibleWith(VeinMiner.class)
public class Hammer { }

@AutoEffect("VEIN_MINING")
@IncompatibleWith(Hammer.class)
public class VeinMiner { }
```

**Result**: Cannot have both Hammer and Vein Mining on same item.

### Incompatibility Table

| Effect | Incompatible With | Reason |
|--------|-------------------|--------|
| Hammer | Vein Mining | Both modify block breaking |
| Vein Mining | Hammer | Both modify block breaking |
| Auto-Sell | Absorption | Both modify drops |
| Absorption | Auto-Sell | Both modify drops |

### Checking Compatibility

```bash
/zitems effect apply vein_miner
# If item has Hammer:
# → "Effect vein_miner is incompatible with existing effects"
```

---

## Effect Representation

Effects can be represented as physical items that players apply via smithing tables.

### Basic Representation

```yaml
id: "hammer_stone"
type: "HAMMER"
display-name: "<gold>⚒ HAMMER</gold>"

# Effect settings
width: 3
height: 3
depth: 1

# Representation
representation:
  material: NETHER_STAR
  display-name: "<yellow>Hammer Enhancement</yellow>"
  lore:
    - "<gray>Apply to pickaxe in smithing table"

  applicator-type: SMITHING_TABLE

  template:
    item: "item:NETHERITE_UPGRADE_SMITHING_TEMPLATE"
```

### Giving Effect Items

```bash
/zitems effect give @s hammer_stone
```

Players receive a physical item they can use in a smithing table.

### Smithing Table Usage

1. Place template (e.g., Netherite Upgrade Template)
2. Place tool (e.g., Diamond Pickaxe)
3. Place effect stone (Hammer Enhancement)
4. Take enhanced tool

### Applicator Types

```yaml
applicator-type: SMITHING_TABLE      # Vanilla smithing table
applicator-type: ZITEMS_APPLICATOR   # Custom GUI (future feature)
```

### Applicability Restrictions

Limit which items can receive the effect:

```yaml
representation:
  # ... other settings ...

  # Whitelist mode (only these materials)
  applicable-materials:
    - DIAMOND_PICKAXE
    - NETHERITE_PICKAXE
  applicability-blacklisted: false

# OR

  # Blacklist mode (all except these)
  applicable-materials:
    - WOODEN_PICKAXE
    - STONE_PICKAXE
  applicability-blacklisted: true
```

---

## Effect Settings

Each effect type has its own settings structure.

### Common Settings Pattern

Most mining effects use:

```yaml
# Block Filter
materials:                # Whitelist/blacklist
  - STONE
  - COBBLESTONE
blacklisted: false        # false=whitelist, true=blacklist

# OR use Bukkit tags
tags:
  - LOGS
  - PLANKS
```

### Examples by Type

**Hammer**:
```yaml
materials: [STONE, COBBLESTONE]
width: 3
height: 3
depth: 3
damage: 1
```

**Vein Mining**:
```yaml
materials: [DIAMOND_ORE, EMERALD_ORE]
block-limit: 64
damage: 1
```

**Auto-Sell**:
```yaml
multiplier: 1.5    # 150% sell price
```

**XP Boost**:
```yaml
boost: 2.0           # 2x experience
chance-to-boost: 100.0  # 100% chance
```

For complete settings documentation, see [Effect Handlers Reference](../advanced/effect-handlers.md).

---

## Creating Effects

### Step 1: Create Effect File

Create `plugins/zItems/effects/my_hammer.yml`:

```yaml
id: "my_hammer"
type: "HAMMER"
display-name: "<gold>⚒ My Hammer</gold>"

materials:
  - STONE
  - COBBLESTONE
  - DEEPSLATE

width: 3
height: 3
depth: 1
damage: 1
```

### Step 2: Reload Plugin

```bash
/zitems reload
```

Check console for:
```
[zItems] Loading effects from effects/...
[zItems] Loaded effect: my_hammer
```

### Step 3: Use in Item

Create `plugins/zItems/items/my_pickaxe.yml`:

```yaml
id: "my_pickaxe"
material: DIAMOND_PICKAXE
display-name: "<gold>My Pickaxe</gold>"

effects:
  - my_hammer
```

### Step 4: Test

```bash
/zitems reload
/zitems item give @s my_pickaxe
```

Mine a stone block - it should break a 3x3 area!

---

## Advanced Effect Configuration

### Multiple Effects

```yaml
id: "ultimate_pickaxe"
material: NETHERITE_PICKAXE

effects:
  - hammer_5x5        # Area mining
  - melt_mining       # Auto-smelt
  - auto_sell         # Sell drops
  - xp_boost_3x       # 3x XP
  - unbreakable       # Never breaks
```

**Execution Order** (by priority):
1. Hammer (1) - Collects blocks
2. Melt Mining (0) - Smelts
3. XP Boost (0) - Multiplies XP
4. Auto-Sell (-1) - Sells smelted drops

### Effect with Representation

```yaml
id: "hammer_upgrade"
type: "HAMMER"
display-name: "<gradient:#FFD700:#FFA500>⚒ HAMMER UPGRADE</gradient>"

# Effect settings
materials:
  - STONE
  - COBBLESTONE
  - DEEPSLATE
  - ANDESITE
  - DIORITE
  - GRANITE
width: 5
height: 5
depth: 3
damage: 1

# Representation as item
representation:
  material: GOLDEN_PICKAXE
  display-name: "<yellow><bold>⚒ Hammer Enhancement Stone</bold></yellow>"
  lore:
    - ""
    - "<gray>Apply this to a pickaxe in a"
    - "<gray>smithing table to grant the"
    - "<gold>Hammer</gold> <gray>effect!"
    - ""
    - "<yellow>Mining Area: <white>5x5x3"
    - "<yellow>Durability Cost: <white>1 per block"
    - ""
    - "<dark_gray>▸ <white>Applicable: <yellow>Pickaxes"
    - "<dark_gray>▸ <white>Application: <aqua>Smithing Table"
  custom-model-data: 100

  applicator-type: SMITHING_TABLE

  template:
    item: "item:NETHERITE_UPGRADE_SMITHING_TEMPLATE"

  # Only pickaxes
  applicable-materials:
    - WOODEN_PICKAXE
    - STONE_PICKAXE
    - IRON_PICKAXE
    - GOLDEN_PICKAXE
    - DIAMOND_PICKAXE
    - NETHERITE_PICKAXE
  applicability-blacklisted: false
```

---

## Best Practices

### 1. Descriptive IDs

```yaml
# Good
id: "hammer_5x5_stone"
id: "auto_sell_1.5x"
id: "vein_miner_ores_64"

# Bad
id: "effect1"
id: "test"
id: "hammer"
```

### 2. Clear Display Names

```yaml
# Good
display-name: "<gold>⚒ HAMMER (5x5)</gold>"
display-name: "<green>💰 AUTO SELL (1.5x)</green>"

# Bad
display-name: "effect"
display-name: "Hammer"  # No visual flair
```

### 3. Test Incrementally

1. Create effect file
2. Reload: `/zitems reload`
3. Check console for errors
4. Create test item
5. Test in-game
6. Iterate

### 4. Document Your Effects

```yaml
# Ultimate Hammer Effect
# Version: 2.0
# Purpose: Large-area mining for endgame tools
# Compatible with: All pickaxes
id: "hammer_ultimate"
type: "HAMMER"
# ...
```

### 5. Use Consistent Naming

```yaml
# Naming convention:
# [effect_type]_[variant]_[material/scope]

id: "hammer_3x3_stone"
id: "hammer_5x5_all"
id: "vein_miner_ores"
id: "auto_sell_1.5x"
```

---

## Troubleshooting

### Effect Not Loading

**Console Error**: "Unknown effect type: HAMMMER"

**Solutions**:
- Check spelling of `type` field
- Ensure handler is registered (`@AutoEffect`)
- Verify effect type exists

**Valid Types**: See [Effect Handlers Reference](../advanced/effect-handlers.md)

### Effect Not Working on Item

**Symptoms**: Item has effect in lore but doesn't work

**Checklist**:
1. Is the effect ID correct in item config?
2. Does the effect have required settings?
3. Is the item material compatible?
4. Check console for errors

**Debug**:
```bash
/zitems effect view
# While holding the item - shows all effects
```

### Effect Not Showing in Lore

**Symptoms**: Effect works but not shown in lore

**Causes**:
- `nb-effects-view: 0` (hides all)
- `base-effects-visible: false`
- Effect has no `display-name`

**Solution**:
```yaml
# In effect file
display-name: "<gold>⚒ HAMMER</gold>"

# In item file
nb-effects-view: -1
base-effects-visible: true
```

### Effect Applied But Nothing Happens

**Checklist**:
1. Does the effect require specific conditions? (e.g., Hammer needs breakable blocks)
2. Is the effect compatible with item type? (e.g., Farming Hoe on hoes only)
3. Check console for runtime errors
4. Enable debug mode in `config.yml`

---

## Performance Considerations

### Effect Overhead

- **NoEventEffects**: Applied once, no overhead
- **SingleEventEffects**: Minimal overhead per event
- **MultiEventEffects**: Slightly more overhead (multiple event types)

### Optimizing Effects

**Good**:
```yaml
# Specific material list
materials:
  - DIAMOND_ORE
  - EMERALD_ORE
```

**Bad**:
```yaml
# Blacklisting everything (processes all blocks)
materials:
  - BEDROCK
blacklisted: true
```

### Block Limit

For Vein Mining, use reasonable limits:

```yaml
block-limit: 64    # Good
block-limit: 999999  # Bad - can lag server
```

---

## Next Steps

- **[Effect Handlers Reference](../advanced/effect-handlers.md)** - All available effects and their settings
- **[Commands](commands.md)** - Effect-related commands
- **[Creating Items](creating-items.md)** - Using effects in items

---

Need help? Join our [Discord](https://groupez.dev) or check [GitHub Issues](https://github.com/GroupeZ-dev/zItems/issues)!