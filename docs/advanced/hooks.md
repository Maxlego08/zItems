# Hook System

Learn about zItems' hook system that integrates with third-party plugins to provide extended functionality.

---

## Overview

**Hooks** are optional plugin integrations that add features when specific plugins are installed. They enable zItems to:

- **Detect custom blocks** from ItemsAdder, Nexo, Oraxen
- **Boost job rewards** in Jobs Reborn and ZJobs
- **Respect region protection** from WorldGuard and SuperiorSkyBlock2
- **Integrate with shops** for Auto-Sell effects

Hooks are **automatically enabled** when their corresponding plugin is detected at startup.

---

## How Hooks Work

### Auto-Detection

At startup, zItems scans for `@AutoHook` annotations:

```java
@AutoHook("Jobs")
public class JobsHook implements Hook {
    @Override
    public void onEnable() {
        // Register extractors, handlers, providers
    }
}
```

If the plugin name ("Jobs") is found using `Bukkit.getPluginManager().getPlugin("Jobs")`, the hook is enabled.

### Hook Lifecycle

```
┌─────────────────────────────────┐
│ zItems Plugin Enable            │
└─────────────┬───────────────────┘
              │
              ▼
┌─────────────────────────────────┐
│ Scan for @AutoHook annotations  │
│ (using Reflections library)     │
└─────────────┬───────────────────┘
              │
              ▼
┌─────────────────────────────────┐
│ Check if plugin is installed    │
│ Bukkit.getPluginManager()...    │
└─────────────┬───────────────────┘
              │
       ┌──────┴──────┐
       │             │
   Yes │             │ No
       ▼             ▼
┌────────────┐ ┌───────────┐
│ Enable Hook│ │ Skip Hook │
└──────┬─────┘ └───────────┘
       │
       ▼
┌─────────────────────────────────┐
│ Hook.onEnable()                 │
│ - Register extractors           │
│ - Register handlers             │
│ - Register providers            │
└─────────────────────────────────┘
```

---

## Available Hooks

### Custom Block Providers

Detect and drop custom blocks from third-party plugins.

#### ItemsAdder

**Plugin**: [ItemsAdder](https://www.spigotmc.org/resources/itemsadder.73355/)
**Hook**: `IAHook.java`
**Purpose**: Custom blocks, items, textures

**What it provides**:
- Detects ItemsAdder custom blocks using `CustomBlock.byAlreadyPlaced()`
- Detects ItemsAdder furniture using `CustomFurniture.byAlreadySpawned()`
- Returns correct ItemStack for custom drops

**Integration**:
```java
@AutoHook("ItemsAdder")
public class IAHook implements Hook {
    @Override
    public void onEnable() {
        Registry.get(CustomBlockProviderRegistry.class).register("itemsadder", (block, player) -> {
            CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
            CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(block);

            if (customBlock == null && customFurniture == null) {
                return Optional.empty();
            }

            if (customFurniture != null) {
                customFurniture.remove(false);
                return Optional.ofNullable(List.of(customFurniture.getItemStack()));
            }

            return Optional.ofNullable(customBlock.getLoot());
        });
    }
}
```

**Effects that use it**:
- Hammer
- Vein Mining

#### Nexo

**Plugin**: [Nexo](https://github.com/Nexo-Craft/Nexo)
**Hook**: `NexoHook.java`
**Purpose**: Custom blocks and items

**What it provides**:
- Detects Nexo custom blocks using `NexoBlocks.customBlockMechanic()`
- Returns correct ItemStack for custom drops

**Effects that use it**:
- Hammer
- Vein Mining

#### Oraxen

**Plugin**: [Oraxen](https://www.spigotmc.org/resources/oraxen.72448/)
**Hook**: `OraxenHook.java`
**Purpose**: Custom items and blocks

**What it provides**:
- Detects Oraxen custom blocks using `OraxenBlocks.isOraxenBlock()`
- Returns correct ItemStack using `OraxenBlocks.getOraxenBlock()`

**Effects that use it**:
- Hammer
- Vein Mining

---

### Custom Entity Providers

Detect custom mobs from third-party plugins, for pipeline entries like `KILL`.

#### MythicMobs

**Plugin**: [MythicMobs](https://www.spigotmc.org/resources/mythicmobs.5702/)
**Hook**: `MythicMobsHook.java`
**Purpose**: Custom mob type detection

**What it provides**:
- Detects MythicMobs-spawned entities using `MythicBukkit.inst().getMobManager().getActiveMob(uuid)`
- Returns the mob's type id via `ActiveMob#getMobType()`

**Integration**:
```java
@AutoHook("MythicMobs")
public class MythicMobsHook implements Hook {
    @Override
    public void onEnable() {
        Registry.get(CustomEntityProviderRegistry.class).register("mythicmobs", new MythicMobsProvider());
    }
}
```

**Entries that use it**:
- `KILL` (`entities: ["mythicmobs:my_boss"]`)

---

### Job System Integrations

Boost job experience and money rewards.

#### Jobs Reborn

**Plugin**: [Jobs Reborn](https://www.spigotmc.org/resources/jobs-reborn.4216/)
**Hook**: `JobsHook.java`
**Purpose**: Job system with leveling

**What it provides**:

**Extractors**:
- `JobExpGainEventExtractor` - Extracts ItemStack from `JobsExpGainEvent`
- `JobMoneyGainEventExtractor` - Extracts ItemStack from `JobsPrePaymentEvent`

**Handlers**:
- `JobsExperienceMultiplier` - Multiplies job XP gained
- `JobsMoneyMultiplier` - Multiplies job money gained

**Entries** (for [pipelines](pipelines.md)):
- `JOBS_EXP_GAIN` - Matches `JobsExpGainEvent`
- `JOBS_MONEY_GAIN` - Matches `JobsPrePaymentEvent`

**Example effect**:
```yaml
id: "jobs_xp_boost"
type: "JOB_XP_BOOST"
display-name: "<green>Jobs XP x2</green>"
boost: 2.0  # 2x XP
can-apply-to:
  materials:
    - DIAMOND_PICKAXE
    - IRON_PICKAXE
```

**How it works**:
```java
public class JobsExperienceMultiplier extends JobsHandler<JobsExpGainEvent> {
    @Override
    protected void setNewValue(JobsExpGainEvent event, double boost) {
        event.setExp(event.getExp() * boost);  // Multiply XP
    }
}
```

#### ZJobs

**Plugin**: [ZJobs](https://groupez.dev) (GroupeZ)
**Hook**: `ZJobsHook.java`
**Purpose**: GroupeZ custom job system

**What it provides**:

**Extractors**:
- `ZJobExpGainEventExtractor` - Extracts ItemStack from ZJobs XP events
- `ZJobMoneyGainEventExtractor` - Extracts ItemStack from ZJobs money events

**Handlers**:
- `ZJobsExperienceMultiplier` - Multiplies ZJobs XP
- `ZJobsMoneyMultiplier` - Multiplies ZJobs money

**Entries** (for [pipelines](pipelines.md)):
- `JOBS_EXP_GAIN` - Matches `JobExpGainEvent` (same entry id as the Jobs Reborn hook — only one hook is ever active)
- `JOBS_MONEY_GAIN` - Matches `JobMoneyGainEvent`

**Example effect**:
```yaml
id: "zjobs_money_boost"
type: "ZJOB_MONEY_BOOST"
display-name: "<gold>ZJobs Money x1.5</gold>"
boost: 1.5  # 1.5x money
can-apply-to:
  materials:
    - DIAMOND_PICKAXE
```

---

### Protection Plugins

Respect region protection and island permissions.

#### WorldGuard

**Plugin**: [WorldGuard](https://enginehub.org/worldguard)
**Hook**: `WorldGuardHook.java`
**Purpose**: Region protection

**What it provides**:
- Location access checking for protected regions
- Prevents Hammer/Vein Mining in protected areas

**Implementation**:
```java
@AutoHook("WorldGuard")
public class WorldGuardHook implements Hook {
    @Override
    public void onEnable() {
        Registry.get(LocationAccessRegistry.class).register("worldguard", new WorldGuardLocationAccess());
    }
}
```

**Location Access Check**:
```java
public class WorldGuardLocationAccess implements LocationAccess {
    @Override
    public boolean canAccess(Player player, Location location) {
        // Check if player can break blocks in this region
        return WorldGuard.getInstance()
            .getPlatform()
            .getRegionContainer()
            .createQuery()
            .testState(location, player, Flags.BLOCK_BREAK);
    }
}
```

**How effects use it**:
```java
// In Hammer effect handler
for (Block block : blocksToBreak) {
    if (!EventUtil.canBreakBlock(player, block.getLocation())) {
        // Skip this block - protected by WorldGuard
        continue;
    }
    // Break the block...
}
```

#### SuperiorSkyBlock2

**Plugin**: [SuperiorSkyBlock2](https://www.spigotmc.org/resources/superiorskyblock2.63905/)
**Hook**: `SuperiorSkyBlockHook.java`
**Purpose**: Skyblock plugin with island protection

**What it provides**:
- Island permission checking
- Prevents non-members from using effects on islands

**Implementation**:
```java
public class SuperiorSkyBlockLocationAccess implements LocationAccess {
    @Override
    public boolean canAccess(Player player, Location location) {
        Island island = SuperiorSkyblockAPI.getIslandAt(location);
        if (island == null) {
            return true; // No island = no protection
        }

        // Check if player has island permissions
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        return island.hasPermission(superiorPlayer, IslandPrivileges.BREAK);
    }
}
```

---

### Shop Integrations

Provide item pricing for Auto-Sell effect.

#### EconomyShopGUI

**Plugin**: [EconomyShopGUI](https://www.spigotmc.org/resources/economyshopgui.69927/)
**Hook**: `EconomyShopGUIHook.java`
**Purpose**: Shop plugin

**What it provides**:
- Shop pricing via `EconomyShopGUIProvider`
- Auto-sell functionality

**Implementation**:
```java
public class EconomyShopGUIProvider implements ShopProvider {
    @Override
    public boolean sell(Plugin plugin, ItemStack itemStack, int amount, double multiplier, Player player) {
        // Get price from EconomyShopGUI
        double price = getPrice(itemStack) * amount * multiplier;

        if (price <= 0) {
            return false; // Item not in shop
        }

        // Add money to player
        economy.depositPlayer(player, price);
        return true;
    }
}
```

**Effect that uses it**:
- Auto Sell

#### ShopGUIPlus

**Plugin**: [ShopGUIPlus](https://www.spigotmc.org/resources/shopgui-plus.6515/)
**Hook**: `ShopGUIPlusHook.java`
**Purpose**: Shop plugin

**What it provides**:
- Shop pricing via `ShopGUIPlusProvider`
- Auto-sell functionality

#### ZShop

**Plugin**: [ZShop](https://groupez.dev) (GroupeZ)
**Hook**: `ZShopHook.java`
**Purpose**: GroupeZ custom shop system

**What it provides**:
- Shop pricing via `ZShopProvider`
- Auto-sell functionality with GroupeZ-specific pricing

---

## Hook Components

### Extractors

**Purpose**: Extract ItemStack from plugin-specific events

**Interface**: `ItemSourceExtractor<E extends Event>`

```java
public interface ItemSourceExtractor<E extends Event> {
    ExtractionResult extract(E event);
}
```

**Example**:
```java
public class JobExpGainEventExtractor implements ItemSourceExtractor<JobsExpGainEvent> {
    @Override
    public ExtractionResult extract(JobsExpGainEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            return ExtractionResult.empty();
        }

        return new ExtractionResult(player, item);
    }
}
```

**Registration**:
```java
ExtractorsRegistry registry = Registry.get(ExtractorsRegistry.class);
registry.register(JobsExpGainEvent.class, new JobExpGainEventExtractor());
```

### Handlers

**Purpose**: Custom effect handlers for plugin-specific features

**Example**: Jobs XP Boost

```yaml
id: "jobs_xp_boost"
type: "JOB_XP_BOOST"  # Handler ID
display-name: "<green>Jobs XP Boost</green>"
boost: 2.0  # Settings
can-apply-to:
  materials:
    - DIAMOND_PICKAXE
```

**Implementation**:
```java
public class JobsExperienceMultiplier extends JobsHandler<JobsExpGainEvent> {
    @Override
    protected void setNewValue(JobsExpGainEvent event, double boost) {
        event.setExp(event.getExp() * boost);
    }
}
```

**Registration**:
```java
HandlersRegistry registry = Registry.get(HandlersRegistry.class);
registry.register("JOB_XP_BOOST", new JobsExperienceMultiplier());
```

### Providers

**Purpose**: Provide plugin-specific services (shops, custom blocks, protection)

**Interface Example**: `CustomBlockProvider`

```java
public interface CustomBlockProvider {
    Optional<List<ItemStack>> getCustomBlockDrop(Block block, Player player);
}
```

**Registration**:
```java
CustomBlockProviderRegistry registry = Registry.get(CustomBlockProviderRegistry.class);
registry.register("itemsadder", (block, player) -> {
    // Detect and return ItemsAdder drops
});
```

---

## Creating Custom Hooks

### Step 1: Create Hook Module

Create a new directory in `hooks/`:

```
hooks/
└── MyPlugin/
    └── src/
        └── main/
            └── java/
                └── fr/
                    └── traqueur/
                        └── items/
                            └── hooks/
                                └── myplugin/
                                    └── MyPluginHook.java
```

### Step 2: Implement Hook Interface

```java
package fr.traqueur.items.hooks.myplugin;

import fr.traqueur.items.api.annotations.AutoHook;
import fr.traqueur.items.api.hooks.Hook;
import fr.traqueur.items.api.registries.*;

@AutoHook("MyPlugin")  // Plugin name
public class MyPluginHook implements Hook {
    @Override
    public void onEnable() {
        // Register components
        HandlersRegistry handlersRegistry = Registry.get(HandlersRegistry.class);
        ExtractorsRegistry extractorsRegistry = Registry.get(ExtractorsRegistry.class);

        // Register extractors
        extractorsRegistry.register(MyPluginEvent.class, new MyPluginEventExtractor());

        // Register handlers
        handlersRegistry.register("MY_CUSTOM_EFFECT", new MyCustomEffectHandler());

        // Register providers (if applicable)
        CustomBlockProviderRegistry blockRegistry = Registry.get(CustomBlockProviderRegistry.class);
        blockRegistry.register("myplugin", new MyPluginBlockProvider());
    }
}
```

### Step 3: Create Extractor (Optional)

```java
package fr.traqueur.items.hooks.myplugin.extractors;

import fr.traqueur.items.api.extractors.ExtractionResult;
import fr.traqueur.items.api.extractors.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MyPluginEventExtractor implements ItemSourceExtractor<MyPluginEvent> {
    @Override
    public ExtractionResult extract(MyPluginEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemUsed();

        if (item == null || item.getType().isAir()) {
            return ExtractionResult.empty();
        }

        return new ExtractionResult(player, item);
    }
}
```

### Step 4: Create Handler (Optional)

```java
package fr.traqueur.items.hooks.myplugin.handlers;

import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.effects.SingleEventEffectHandler;

public class MyCustomEffectHandler implements SingleEventEffectHandler<MyEffectSettings, MyPluginEvent> {
    @Override
    public void handle(Player player, ItemStack item, MyPluginEvent event, EffectContext context) {
        MyEffectSettings settings = getSettings(item);
        // Apply custom effect logic
    }
}
```

### Step 5: Add to Build

Edit `settings.gradle.kts` (auto-detected if directory exists):

```kotlin
// Hooks modules are automatically included if directory exists
```

### Step 6: Add Dependency

Create `hooks/MyPlugin/build.gradle.kts`:

```kotlin
dependencies {
    compileOnly(files("path/to/MyPlugin.jar"))
}
```

### Step 7: Test

1. Build: `./gradlew build`
2. Install zItems + MyPlugin
3. Check logs for:
```
[zItems] Detected MyPlugin - enabling hook
```

---

## Hook Registry System

All hooks use the centralized registry pattern:

### CustomBlockProviderRegistry

```java
CustomBlockProviderRegistry registry = Registry.get(CustomBlockProviderRegistry.class);

// Register provider
registry.register("itemsadder", (block, player) -> {
    // Return custom drops
});

// Use in effects
Optional<List<ItemStack>> drops = registry.getCustomBlockDrop(block, player);
```

### LocationAccessRegistry

```java
LocationAccessRegistry registry = Registry.get(LocationAccessRegistry.class);

// Register access checker
registry.register("worldguard", new WorldGuardLocationAccess());

// Use in effects
if (!EventUtil.canBreakBlock(player, location)) {
    // Protected location
}
```

### ShopProviderRegistry

```java
ShopProvider provider = ShopProvider.get(); // Singleton

// Sell items
boolean sold = provider.sell(plugin, itemStack, amount, multiplier, player);
```

---

## Checking Enabled Hooks

### In Console

At startup, zItems logs detected hooks:

```
[zItems] Enabling zItems v1.0.0
[zItems] Detected ItemsAdder - enabling custom block support
[zItems] Detected WorldGuard - enabling region protection
[zItems] Detected EconomyShopGUI - enabling shop integration
[zItems] Registered 12 items and 24 effects
[zItems] zItems has been enabled!
```

### In Code

```java
// Check if hook is enabled
HooksRegistry registry = Registry.get(HooksRegistry.class);
boolean jobsEnabled = registry.isEnabled("Jobs");

// Check if provider exists
CustomBlockProviderRegistry blockRegistry = Registry.get(CustomBlockProviderRegistry.class);
Optional<List<ItemStack>> drops = blockRegistry.getCustomBlockDrop(block, player);
// Returns Optional.empty() if no provider handles this block
```

---

## Soft Dependencies

All hooks are defined as soft dependencies in `plugin.yml`:

```yaml
name: zItems
version: 1.0.0
main: fr.traqueur.items.ZItems
depend:
  - zMenu  # Hard dependency
softdepend:
  - EconomyShopGUI
  - ShopGUIPlus
  - ZShop
  - WorldGuard
  - SuperiorSkyBlock2
  - PlaceholderAPI
  - ItemsAdder
  - Nexo
  - Oraxen
  - Jobs
  - ZJobs
```

**Soft dependencies** are optional - zItems loads even if they're not present.

---

## Troubleshooting

### Hook not detected

**Symptoms**:
- Plugin is installed but hook doesn't enable
- Console shows no "Detected [Plugin]" message

**Checklist**:
1. Is the plugin name correct in `@AutoHook("PluginName")`?
2. Is the plugin loaded before zItems? (Check load order)
3. Is the hook class in the correct package? (`fr.traqueur.items.hooks.*`)

**Debug**:
```yaml
# config.yml
debug: true
```

Look for: `[zItems] [DEBUG] Scanning for @AutoHook annotations...`

### Custom blocks not dropping

**Symptoms**:
- ItemsAdder/Oraxen blocks drop vanilla items

**Checklist**:
1. Is the hook enabled? (Check console logs)
2. Is the custom block plugin loaded?
3. Are you using Hammer or Vein Mining effects?

**Test**:
Break the custom block manually (without zItems). Does it drop correctly? If no, the issue is with the custom block plugin, not zItems.

### Auto-Sell not working

**Symptoms**:
- Items aren't sold automatically
- No money received

**Checklist**:
1. Is a shop plugin installed? (EconomyShopGUI, ShopGUIPlus, ZShop)
2. Is the shop plugin detected? (Check console logs)
3. Does the item have a price in the shop?

**Test**:
```
/zitems reload
```

Look for: `[zItems] Detected EconomyShopGUI - enabling shop integration`

---

## Best Practices

1. **Use @AutoHook annotation** - Automatic detection and loading
2. **Register all components in onEnable()** - Centralized registration
3. **Use Registry pattern** - Type-safe component access
4. **Provide meaningful provider names** - e.g., "itemsadder", "worldguard"
5. **Handle Optional.empty() gracefully** - Providers may not handle all cases
6. **Test with and without plugin** - Ensure graceful degradation
7. **Document required dependencies** - In hook's JavaDoc

---

## Related Documentation

- **[Dependencies](../getting-started/dependencies.md)** - Required and optional plugins
- **[Effect Handlers Reference](effect-handlers.md)** - How effects use hooks
- **[Block Tracking](block-tracking.md)** - zItems custom block tracking

---

Need help? Join our [Discord](https://groupez.dev) or check [GitHub Issues](https://github.com/GroupeZ-dev/zItems/issues)!