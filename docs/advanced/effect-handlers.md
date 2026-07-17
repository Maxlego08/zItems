# Effect Handlers Reference

This page provides detailed documentation for all built-in effect handlers in zItems.

## Table of Contents

- [Mining Effects](#mining-effects)
  - [Hammer](#hammer)
  - [Vein Mining](#vein-mining)
  - [Melt Mining](#melt-mining)
- [Economic Effects](#economic-effects)
  - [Auto Sell](#auto-sell)
  - [Sell Stick](#sell-stick)
- [Farming Effects](#farming-effects)
  - [Farming Hoe](#farming-hoe)
- [Utility Effects](#utility-effects)
  - [Silk Spawner](#silk-spawner)
  - [Infinite Bucket](#infinite-bucket)
  - [Absorption](#absorption)
  - [Unbreakable](#unbreakable)
- [Enhancement Effects](#enhancement-effects)
  - [XP Boost](#xp-boost)
  - [Attributes Applicator](#attributes-applicator)
  - [Enchants Applicator](#enchants-applicator)
- [Special Effects](#special-effects)
  - [Empty](#empty)
  - [Pipeline](#pipeline)

---

## Mining Effects

### Hammer

**Type**: `HAMMER`
**Event**: `BlockBreakEvent`
**Priority**: 1
**Incompatible With**: Vein Mining

Breaks blocks in a 3D area when mining. The area is defined by width, height, and depth parameters.

#### How It Works
- Breaks blocks in a cuboid area around the mined block
- Direction-aware: adjusts based on which face you're mining
- Respects block protection (WorldGuard, SuperiorSkyBlock2, etc.)
- Works with custom blocks (ItemsAdder, Nexo, Oraxen)
- Applies tool damage per block mined

#### Configuration

```yaml
id: "super_hammer"
type: "HAMMER"
display-name: "<gradient:#FFD700:#FFA500><bold>⚒ HAMMER</bold></gradient>"

# Block Filter (whitelist or blacklist)
materials:  # Blocks that can be broken
  - STONE
  - COBBLESTONE
  - DEEPSLATE
blacklisted: false  # false = whitelist, true = blacklist

# Dimensions
width: 3   # Horizontal width
height: 3  # Vertical height
depth: 3   # Mining depth

# Durability
damage: 1  # Damage per block (-1 = auto-calculate, applies 1 damage per block mined)
```

#### Settings Object (`HammerSettings`)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `materials` | `List<Material>` | `null` | List of breakable materials |
| `tags` | `List<Tag<Material>>` | `null` | Bukkit tags for material groups |
| `blacklisted` | `boolean` | `false` | Use blacklist mode instead of whitelist |
| `width` | `int` | 3 | Horizontal width of mining area |
| `height` | `int` | 3 | Vertical height of mining area |
| `depth` | `int` | 3 | Mining depth |
| `damage` | `int` | -1 | Durability damage (-1 = 1 per block) |

#### Example Use Cases

```yaml
# Stone Hammer - Only mines stone types
id: "stone_hammer"
type: "HAMMER"
display-name: "<gray>Stone Hammer"
materials:
  - STONE
  - COBBLESTONE
  - ANDESITE
  - DIORITE
  - GRANITE
width: 3
height: 3
depth: 1
damage: 1
```

```yaml
# Everything Except Ores Hammer
id: "no_ore_hammer"
type: "HAMMER"
display-name: "<red>No-Ore Hammer"
materials:
  - DIAMOND_ORE
  - EMERALD_ORE
  - GOLD_ORE
blacklisted: true  # Blacklist mode
width: 3
height: 3
depth: 3
```

---

### Vein Mining

**Type**: `VEIN_MINING`
**Event**: `BlockBreakEvent`
**Priority**: 1
**Incompatible With**: Hammer

Mines entire veins of connected blocks (ores, logs, etc.) in one action.

#### How It Works
- Finds all connected blocks of the same type (26-directional search)
- Limits vein size with `block-limit` parameter
- Respects block protection
- Works with custom blocks
- Applies tool damage based on blocks mined

#### Configuration

```yaml
id: "vein_miner_pickaxe"
type: "VEIN_MINING"
display-name: "<gradient:#34eb9b:#2ecc71><bold>⛏ VEIN MINING</bold></gradient>"

# Block Filter
materials:
  - COAL_ORE
  - IRON_ORE
  - DIAMOND_ORE
blacklisted: false

# Limits
block-limit: 64  # Maximum blocks in one vein

# Durability
damage: 1  # Damage per block (-1 = auto)
```

#### Settings Object (`VeinMinerSettings`)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `materials` | `List<Material>` | `null` | Vein-minable materials |
| `tags` | `List<Tag<Material>>` | `null` | Bukkit tags for material groups |
| `blacklisted` | `boolean` | `false` | Blacklist mode |
| `block-limit` | `int` | 64 | Max blocks per vein |
| `damage` | `int` | -1 | Durability damage |

#### Algorithm Details

The vein mining algorithm:
1. Starts from the broken block
2. Checks all 26 adjacent positions (including diagonals)
3. Adds matching blocks to a queue
4. Continues until queue is empty or `block-limit` is reached
5. Only breaks blocks the player has permission to break

---

### Melt Mining

**Type**: `MELT_MINING`
**Event**: `BlockBreakEvent`
**Priority**: 0
**Settings**: None (EmptySettings)

Automatically smelts ores when mined, giving you the smelted result instead of raw ore.

#### How It Works
- Detects blocks with furnace recipes
- Converts drops to smelted versions
- Grants smelting experience
- Shows flame particles at mined blocks
- Works with Hammer and Vein Mining effects

#### Configuration

```yaml
id: "auto_smelt_pickaxe"
type: "MELT_MINING"
display-name: "<gradient:#FF4500:#FF8C00><bold>🔥 MELT MINING</bold></gradient>"
# No additional settings required
```

#### Supported Materials

Any block with a furnace recipe, including:
- Iron Ore → Iron Ingot
- Gold Ore → Gold Ingot
- Copper Ore → Copper Ingot
- Ancient Debris → Netherite Scrap
- Raw Iron/Gold/Copper → Smelted versions
- Sand → Glass
- Cobblestone → Stone
- And more...

#### Behavior Notes

- **Experience**: Grants smelting XP from furnace recipe
- **Visual**: Spawns flame particles at mined blocks
- **Caching**: Recipes are cached for performance
- **Compatibility**: Stacks with other mining effects

---

## Economic Effects

### Auto Sell

**Type**: `AUTO_SELL`
**Events**: `BlockBreakEvent`, `EntityDeathEvent`
**Priority**: -1 (runs last)
**Incompatible With**: Absorption

Automatically sells drops from mining or killing entities.

#### How It Works
- Intercepts drops from BlockBreakEvent and EntityDeathEvent
- Sells each drop using configured shop provider
- Applies price multiplier
- Removes sold items from drop list
- Keeps unsold items (drops normally)

#### Configuration

```yaml
id: "auto_sell_pickaxe"
type: "AUTO_SELL"
display-name: "<gradient:#00FF00:#50C878><bold>💰 AUTO SELL</bold></gradient>"
multiplier: 1.5  # 150% sell price (optional, default: 1.0)
```

#### Settings Object (`AutoSellSettings`)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `multiplier` | `double` | 1.0 | Price multiplier for selling |

#### Shop Provider Integration

Requires one of the supported shop plugins:
- **EconomyShopGUI**
- **ShopGUIPlus**
- **ZShop**

If no shop provider is available, the effect does nothing.

#### Priority Explanation

Priority `-1` means Auto Sell runs **after** other effects like Hammer or Vein Mining have collected drops. This ensures all drops are accounted for before selling.

---

### Sell Stick

**Type**: `SELL_STICK`
**Event**: `PlayerInteractEvent`
**Priority**: 0

Sells all items in a container when right-clicking it.

#### How It Works
- Right-click on chests, barrels, hoppers, etc.
- Sells all items inside the container
- Applies price multiplier
- Removes sold items
- Keeps unsold items in container
- Optionally applies durability damage

####Configuration

```yaml
id: "sell_stick"
type: "SELL_STICK"
display-name: "<gradient:#FFD700:#FFA500><bold>🪄 SELL STICK</bold></gradient>"
multiplier: 1.0      # Sell price multiplier (optional, default: 1.0)
damage: true         # Apply durability damage (optional, default: true)
action: "CLICK"      # Required click action (optional)
hand: "HAND"         # Required hand (optional)
```

#### Settings Object (`SellStickSettings`)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `multiplier` | `double` | 1.0 | Price multiplier |
| `damage` | `boolean` | `true` | Apply durability damage |
| `action` | `InteractionAction` | `null` | Required action type |
| `hand` | `EquipmentSlot` | `null` | Required hand |

#### Interaction Actions

- `CLICK` - Any click
- `RIGHT_CLICK` - Right-click only
- `LEFT_CLICK` - Left-click only
- `SHIFT_RIGHT_CLICK` - Shift + right-click
- `SHIFT_LEFT_CLICK` - Shift + left-click

#### Hand Options

- `HAND` - Main hand only
- `OFF_HAND` - Off-hand only

---

## Farming Effects

### Farming Hoe

**Type**: `FARMING_HOE`
**Events**: `BlockBreakEvent`, `PlayerInteractEvent`
**Priority**: 0

Advanced farming tool with area harvesting, auto-replanting, area tilling, and area seeding.

#### How It Works

**Harvesting** (BlockBreakEvent):
- Breaks mature crops in an area
- Auto-replants or breaks completely
- Drops at configurable location
- Can add drops to inventory
- Applies harvest damage

**Tilling** (Right-click dirt/grass):
- Converts dirt to farmland in an area
- Works on dirt, grass, podzol, mycelium
- Applies till damage

**Planting** (Right-click farmland with seeds):
- Plants seeds from inventory in an area
- Auto-detects seed type
- Supports wheat, carrots, potatoes, beetroot, melons, pumpkins, nether wart

#### Configuration

```yaml
id: "super_farming_hoe"
type: "FARMING_HOE"
display-name: "<gradient:#7CFC00:#32CD32><bold>🌾 FARMING HOE</bold></gradient>"

# Area Settings
size: 5  # Area size (must be odd: 3, 5, 7, etc.)

# Harvest Settings
auto-replant: true          # Replant after harvest
drop-location: "PLAYER"     # BLOCK, CENTER, or PLAYER
drop-in-inventory: true     # Add drops to inventory
harvest: true               # Enable harvesting
harvest-damage: 1           # Durability per harvest

# Tilling Settings
till-damage: 1              # Durability per till

# Planting Settings
plant-seeds: true           # Enable area seeding

# Optional Filters
allowed-crops:              # Whitelist harvestable crops
  - WHEAT
  - CARROTS
  - POTATOES

allowed-seeds:              # Whitelist plantable seeds
  - WHEAT_SEEDS
  - CARROT
  - POTATO

drop-blacklist:             # Remove these from drops
  - POISONOUS_POTATO
```

#### Settings Object (`FarmingHoeSettings`)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `size` | `int` | 3 | Area size (must be odd) |
| `auto-replant` | `boolean` | `true` | Auto-replant crops |
| `drop-location` | `DropLocation` | `null` | Drop location |
| `drop-in-inventory` | `boolean` | `false` | Add to inventory |
| `harvest` | `boolean` | `true` | Enable harvesting |
| `plant-seeds` | `boolean` | `true` | Enable seeding |
| `harvest-damage` | `int` | 1 | Harvest durability |
| `till-damage` | `int` | 1 | Till durability |
| `allowed-crops` | `List<Material>` | `null` | Crop whitelist |
| `allowed-seeds` | `List<Material>` | `null` | Seed whitelist |
| `drop-blacklist` | `List<Material>` | `null` | Drop blacklist |

#### Drop Locations

- `BLOCK` - Drops at each harvested block
- `CENTER` - Drops at the center (first clicked block)
- `PLAYER` - Drops at player location

---

## Utility Effects

### Silk Spawner

**Type**: `SILK_SPAWNER`
**Event**: `BlockBreakEvent`
**Priority**: 0
**Settings**: None (EmptySettings)

Mine spawners and keep them as items with the mob type preserved.

#### How It Works
- Detects spawner blocks in affected blocks
- Creates spawner item with preserved mob type
- Fires `SpawnerDropEvent` (can be cancelled)
- Adds spawner to drops

#### Configuration

```yaml
id: "silk_touch_spawner"
type: "SILK_SPAWNER"
display-name: "<gradient:#F0F0F0:#C0C0C0><bold>🕸️ SILK SPAWNER</bold></gradient>"
# No settings required
```

#### Compatibility

Works with:
- Hammer effect (mines multiple spawners)
- Any protection plugin (respects break permissions)

---

### Infinite Bucket

**Type**: `INFINITE_BUCKET`
**Events**: `PlayerBucketFillEvent`, `PlayerBucketEmptyEvent`
**Priority**: 0
**Settings**: None (EmptySettings)

Makes water and lava buckets infinite - they never empty or fill.

#### How It Works

**Emptying Bucket**:
- Places water/lava block
- Bucket stays full

**Filling Bucket**:
- Removes water/lava source
- Bucket stays empty

#### Configuration

```yaml
id: "infinite_bucket"
type: "INFINITE_BUCKET"
display-name: "<gradient:#00CED1:#1E90FF><bold>🪣 INFINITE BUCKET</bold></gradient>"
# No settings required
```

#### Forbidden Buckets

Does **not** work with:
- Milk buckets
- Powder snow buckets

---

### Absorption

**Type**: `ABSORPTION`
**Events**: `BlockBreakEvent`, `BlockDropItemEvent`, `EntityDropItemEvent`
**Priority**: -1 (runs last)
**Incompatible With**: Auto Sell
**Settings**: None (EmptySettings)

Automatically adds drops to player inventory instead of dropping on ground.

#### How It Works
- Intercepts drop events
- Adds items to player inventory
- Items that don't fit still drop on ground
- Works with all drop sources

#### Configuration

```yaml
id: "absorption_pickaxe"
type: "ABSORPTION"
display-name: "<gradient:#9B59B6:#E91E63><bold>💫 ABSORPTION</bold></gradient>"
# No settings required
```

#### Behavior Notes

- **Overflow**: Items that don't fit in inventory drop normally
- **Priority**: Runs last to catch all drops
- **Incompatibility**: Cannot be used with Auto Sell (both modify drops)

---

### Unbreakable

**Type**: `UNBREAKABLE`
**Event**: None (NoEventEffect)
**Priority**: 0
**Settings**: None (EmptySettings)

Makes the item unbreakable by setting the unbreakable NBT tag.

#### How It Works
- Applied when effect is added to item
- Sets `Unbreakable: true` in item meta
- Permanent - cannot be removed

#### Configuration

```yaml
id: "unbreakable_tool"
type: "UNBREAKABLE"
display-name: "<gradient:#696969:#2F4F4F><bold>🛡️ UNBREAKABLE</bold></gradient>"
# No settings required
```

#### Note

This is a `NoEventEffectHandler`, meaning it only runs when the effect is first applied to an item, not during gameplay events.

---

## Enhancement Effects

### XP Boost

**Type**: `XP_BOOST`
**Event**: `BlockBreakEvent`
**Priority**: 0

Multiplies experience gained from mining blocks.

#### How It Works
- Calculates XP for mined blocks
- Applies boost multiplier
- Works with Hammer/Vein Mining (sums all block XP)
- Optionally has a chance to trigger

#### Configuration

```yaml
id: "xp_boost_pickaxe"
type: "XP_BOOST"
display-name: "<gradient:#00FF00:#ADFF2F><bold>⭐ XP BOOST</bold></gradient>"
boost: 2.0              # XP multiplier (2.0 = 200%)
chance-to-boost: 100.0  # Trigger chance (optional, -1 = always)
```

#### Settings Object (`BoostSettings`)

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `boost` | `double` | 1.0 | XP multiplier |
| `chance-to-boost` | `double` | -1 | Trigger chance (0-100, -1 = always) |

#### XP Values

Built-in XP values for ores:
- Coal Ore, Nether Gold: 0-2 XP
- Lapis: 2-5 XP
- Redstone: 1-5 XP
- Diamond, Emerald: 3-7 XP
- Ancient Debris: 2-6 XP
- Spawner: 15-42 XP

---

### Attributes Applicator

**Type**: `ATTRIBUTES_APPLICATOR`
**Event**: None (NoEventEffect)
**Priority**: 1
**Paper Only**: Yes

Applies custom attribute modifiers to items when the effect is added.

#### How It Works
- Applied when effect is added to item
- Adds attribute modifiers to item meta
- Supports all Minecraft attributes
- Modern Paper API implementation

#### Configuration

```yaml
id: "legendary_sword"
type: "ATTRIBUTES_APPLICATOR"
display-name: "<gradient:#FF0000:#FFD700><bold>⚔ LEGENDARY ATTRIBUTES</bold></gradient>"
attributes:
  - attribute: ATTACK_DAMAGE
    operation: ADD_NUMBER
    amount: 10.0
    slot: HAND
  - attribute: ATTACK_SPEED
    operation: ADD_NUMBER
    amount: 2.0
    slot: HAND
  - attribute: MOVEMENT_SPEED
    operation: ADD_SCALAR
    amount: 0.2  # 20% speed boost
    slot: HAND
```

#### Settings Object (`AttributesSettings`)

| Parameter | Type | Description |
|-----------|------|-------------|
| `attributes` | `List<AttributeWrapper>` | List of attributes to apply |
| `strategy` | `AttributeMergeStrategy` | Merge strategy (REPLACE, ADD, MULTIPLY) |

#### Attribute Operations

- `ADD_NUMBER` - Adds flat value (e.g., +10 attack damage)
- `ADD_SCALAR` - Adds percentage (e.g., +20% = 0.2)
- `MULTIPLY_SCALAR_1` - Multiplies total (e.g., 2x = 1.0)

#### Equipment Slots

- `HAND` / `MAINHAND` - Main hand
- `OFF_HAND` / `OFFHAND` - Off-hand
- `HEAD` / `HELMET` - Helmet slot
- `CHEST` / `CHESTPLATE` - Chestplate slot
- `LEGS` / `LEGGINGS` - Leggings slot
- `FEET` / `BOOTS` - Boots slot
- `ARMOR` - All armor slots
- `ANY` - Any slot
- `BODY` - Body slot (Paper 1.20.5+)

---

### Enchants Applicator

**Type**: `ENCHANTS_APPLICATOR`
**Event**: None (NoEventEffect)
**Priority**: 0

Increases or decreases enchantment levels when the effect is applied.

#### How It Works
- Applied when effect is added to item
- Increases or decreases existing enchantment levels
- Can add or remove enchantments
- Validates before applying (won't create negative levels)

#### Configuration

```yaml
id: "super_enchanted_sword"
type: "ENCHANTS_APPLICATOR"
display-name: "<gradient:#00BFFF:#1E90FF><bold>✨ SUPER ENCHANTS</bold></gradient>"
enchantments:
  - enchantment: SHARPNESS
    evolution: INCREASE
    level: 10  # Add 10 levels
  - enchantment: FIRE_ASPECT
    evolution: INCREASE
    level: 3
  - enchantment: UNBREAKING
    evolution: DECREASE
    level: 1  # Remove 1 level
```

#### Settings Object (`EnchantsSettings`)

| Parameter | Type | Description |
|-----------|------|-------------|
| `enchantments` | `List<EnchantSetting>` | Enchantment modifications |

#### EnchantSetting Object

| Field | Type | Description |
|-------|------|-------------|
| `wrapper` | `EnchantmentWrapper` | Enchantment + level |
| `evolution` | `Evolution` | INCREASE or DECREASE |

#### Evolution Types

- `INCREASE` - Adds levels to enchantment
- `DECREASE` - Removes levels from enchantment

#### Behavior Notes

- **Validation**: Checks that enchantment exists before modifying
- **Removal**: If level reaches 0, enchantment is completely removed
- **Error Handling**: If any modification would create an invalid state (negative level), no changes are applied

---

## Special Effects

### Empty

**Type**: `EMPTY`
**Event**: None (NoEventEffect)
**Priority**: -1
**Settings**: None (EmptySettings)

A placeholder effect that does nothing. Useful for testing or as a template.

#### Configuration

```yaml
id: "empty_effect"
type: "EMPTY"
display-name: "<gray>Empty Effect"
# Does nothing - useful for testing
```

---

### Pipeline

**Type**: `PIPELINE`
**Event**: Any (AnyEventEffectHandler — see [Pipelines & Entries Reference](pipelines.md))
**Priority**: 0
**Settings**: `entry` (declared inline), `steps` (list of effect ids, resolved lazily)

Groups existing effects behind a shared, reusable trigger condition (an **entry**) instead
of letting them react to every event their handler declares. Only actually runs its steps
once the entry matches — see the dedicated [Pipelines & Entries Reference](pipelines.md)
for the full entry catalog and how to write custom ones.

#### Configuration

```yaml
id: "sword_combat"
type: "PIPELINE"
entry:
  type: "KILL"             # declared inline, no separate file/id
steps:
  - "combat_xp_boost"      # existing effect ids, run in this order
  - "auto_sell_pickaxe"
```

---

## Effect Priorities

Effect priority determines execution order. Lower priority executes first:

| Priority | Effects |
|----------|---------|
| -1 | Auto Sell, Absorption, Empty |
| 0 | Most effects (Silk Spawner, XP Boost, Farming Hoe, Pipeline, etc.) |
| 1 | Hammer, Vein Mining, Attributes Applicator |

**Why Priority Matters**:
- Hammer/Vein Mining (priority 1) collect blocks first
- Auto Sell/Absorption (priority -1) process the collected drops last

---

## Effect Incompatibilities

Some effects cannot be used together:

| Effect | Incompatible With | Reason |
|--------|-------------------|--------|
| Hammer | Vein Mining | Both modify block breaking behavior |
| Vein Mining | Hammer | Both modify block breaking behavior |
| Auto Sell | Absorption | Both modify drop handling |
| Absorption | Auto Sell | Both modify drop handling |

Attempting to apply incompatible effects will result in `EffectApplicationResult.INCOMPATIBLE`.

---

## Common Settings Types

### Material/Tag Filtering

Many effects support material filtering:

```yaml
# Whitelist mode (default)
materials:
  - DIAMOND_ORE
  - EMERALD_ORE
blacklisted: false

# Blacklist mode
materials:
  - BEDROCK
  - END_PORTAL_FRAME
blacklisted: true

# Using Bukkit tags
tags:
  - LOGS
  - PLANKS
```

### Damage Settings

Effects that break blocks support damage configuration:

```yaml
damage: 1   # Apply 1 durability damage per block
damage: -1  # Auto-calculate (1 damage per block broken)
```

---

## Next Steps

- **[Effect Lore Display](effect-lore.md)** - Control how effects appear on items

---

Need help? Check our [GitHub Issues](https://github.com/GroupeZ-dev/zItems/issues) or join our Discord!