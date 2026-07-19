# Getting Started with zItems API

This guide will help you get started developing with the zItems API to create custom effects, hooks, and integrations.

## What Can You Build?

The zItems API allows you to:

- **Custom Effects**: Create new effect handlers (like Hammer, Vein Mining, etc.)
- **Plugin Hooks**: Integrate zItems with your own plugins
- **Event Listeners**: React to zItems-specific events
- **Custom Block Providers**: Add support for custom block plugins
- **Item Source Extractors**: Extract items from custom events

---

## Setting Up Your Development Environment

### Step 1: Project Setup

**Using Gradle (Kotlin DSL)**:

```kotlin
repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    // Add zItems repository here when available
    mavenLocal() // For now, install zItems API to local Maven
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly(files("libs/zItems-api-1.0.0.jar")) // Path to zItems API
}
```

**Using Maven**:

```xml
<repositories>
    <repository>
        <id>papermc</id>
        <url>https://repo.papermc.io/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.papermc.paper</groupId>
        <artifactId>paper-api</artifactId>
        <version>1.21.5-R0.1-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>fr.traqueur.items</groupId>
        <artifactId>zItems-api</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Step 2: Plugin Setup

**plugin.yml**:

```yaml
name: MyZItemsAddon
version: 1.0.0
main: com.example.myaddon.MyAddon
api-version: 1.21
depend: [zItems]  # zItems must be loaded first

author: YourName
description: My custom zItems addon
```

**Main Plugin Class**:

```java
package com.example.myaddon;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.registries.*;
import org.bukkit.plugin.java.JavaPlugin;

public class MyAddon extends JavaPlugin {

    private ItemsPlugin zItems;

    @Override
    public void onEnable() {
        // Get zItems plugin instance
        zItems = (ItemsPlugin) getServer().getPluginManager().getPlugin("zItems");

        if (zItems == null) {
            getLogger().severe("zItems not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Successfully hooked into zItems!");

        // IMPORTANT: Register your package with all registries for auto-discovery
        registerPackages();

        getLogger().info("Custom effects registered!");
    }

    private void registerPackages() {
        // Register your plugin's package for annotation scanning
        String basePackage = "com.example.myaddon";

        // Scan for @AutoEffect handlers
        Registry.get(HandlersRegistry.class).scanPackages(basePackage);

        // Scan for @AutoHook hooks
        Registry.get(HooksRegistry.class).scanPackages(basePackage);

        // Scan for @AutoExtractor extractors
        Registry.get(ExtractorsRegistry.class).scanPackages(basePackage);

        getLogger().info("Scanned package: " + basePackage);
    }
}
```

---

## API Structure

### Core Packages

```
fr.traqueur.items.api/
├── effects/              # Effect system
│   ├── Effect.java       # Effect record
│   ├── EffectHandler.java  # Effect handler interface
│   ├── EffectContext.java  # Effect execution context
│   └── EffectSettings.java # Base settings class
├── items/                # Item system
│   ├── Item.java         # Item interface
│   └── ItemMetadata.java # Custom metadata
├── managers/             # Manager interfaces
│   ├── EffectsManager.java
│   └── ItemsManager.java
├── registries/           # Registry system
│   ├── Registry.java     # Base registry
│   ├── EffectsRegistry.java
│   ├── HandlersRegistry.java
│   └── ItemsRegistry.java
├── events/               # Custom events
│   ├── ItemBuildEvent.java
│   └── SpawnerDropEvent.java
├── annotations/          # Annotation system
│   ├── AutoEffect.java   # Auto-register effects
│   ├── AutoHook.java     # Auto-register hooks
│   └── IncompatibleWith.java
└── blocks/               # Block system
    └── CustomBlockProvider.java
```

---

## Quick Examples

### Example 1: Simple Custom Effect

```java
package com.example.myaddon.effects;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.effects.EffectSettings;
import org.bukkit.event.player.PlayerInteractEvent;

@AutoEffect("HELLO_WORLD")
public class HelloWorldEffect
    implements EffectHandler.SingleEventEffectHandler<EffectSettings, PlayerInteractEvent> {

    @Override
    public void handle(EffectContext context, EffectSettings settings) {
        PlayerInteractEvent event = context.getEventAs(PlayerInteractEvent.class);
        context.executor().sendMessage("Hello from custom effect!");
    }

    @Override
    public int priority() {
        return 0;
    }
}
```

**IMPORTANT**: After creating your effect class, you MUST call `scanPackages()` in your plugin's `onEnable()`:

```java
@Override
public void onEnable() {
    ItemsPlugin zItems = (ItemsPlugin) getServer().getPluginManager().getPlugin("zItems");

    // Register your package for annotation scanning
    Registry.get(HandlersRegistry.class).scanPackages("com.example.myaddon");
}
```

**Configuration** (`effects/hello_world.yml`):

```yaml
id: "hello_world"
type: "HELLO_WORLD"
display-name: "<gradient:#FF0000:#00FF00>Hello World</gradient>"
```

### Example 2: Accessing Managers

```java
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.managers.EffectsManager;
import fr.traqueur.items.api.managers.ItemsManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        ItemsPlugin zItems = (ItemsPlugin) getServer()
            .getPluginManager()
            .getPlugin("zItems");

        // Access managers
        EffectsManager effectsManager = zItems.getManager(EffectsManager.class);
        ItemsManager itemsManager = zItems.getManager(ItemsManager.class);

        // Use managers
        effectsManager.loadRecipes();
    }
}
```

### Example 3: Listening to zItems Events

```java
import fr.traqueur.items.api.events.ItemBuildEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MyListener implements Listener {

    @EventHandler
    public void onItemBuild(ItemBuildEvent event) {
        // Fired when a custom item is built
        String itemId = event.getItem().id();

        getLogger().info("Item built: " + itemId);

        // Modify the item before it's given
        event.getItemStack().editMeta(meta -> {
            // Add custom data, etc.
        });
    }
}
```

### Example 4: Accessing Registries

```java
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.registries.EffectsRegistry;
import fr.traqueur.items.api.registries.Registry;

public class MyCommand {

    public void listEffects() {
        // Get the effects registry
        EffectsRegistry registry = Registry.get(EffectsRegistry.class);

        // Iterate all effects
        for (Effect effect : registry.getAll()) {
            System.out.println("Effect: " + effect.id());
        }

        // Get specific effect
        Effect hammer = registry.getById("super_hammer");
        if (hammer != null) {
            System.out.println("Found: " + hammer.displayName());
        }
    }
}
```

---

## Key Concepts

### 1. Registry Pattern

zItems uses a centralized registry system for all components:

```java
// Access any registry
EffectsRegistry effects = Registry.get(EffectsRegistry.class);
HandlersRegistry handlers = Registry.get(HandlersRegistry.class);
ItemsRegistry items = Registry.get(ItemsRegistry.class);

// Registries are type-safe and globally accessible
```

### 2. Manager Pattern

Managers handle business logic:

```java
// Get ItemsPlugin instance
ItemsPlugin plugin = JavaPlugin.getPlugin(ItemsPlugin.class);

// Access managers through the plugin
EffectsManager effectsManager = plugin.getManager(EffectsManager.class);
ItemsManager itemsManager = plugin.getManager(ItemsManager.class);
```

### 3. Annotation-Driven Discovery

zItems automatically discovers and registers annotated classes **ONLY IF** you call `scanPackages()`:

```java
@AutoEffect("MY_EFFECT")  // Auto-registers as effect handler
public class MyEffect implements EffectHandler<...> { }

@AutoHook("MyPlugin")  // Auto-registers when MyPlugin is present
public class MyHook implements Hook { }

@AutoExtractor  // Auto-registers as event extractor
public class MyExtractor implements ItemSourceExtractor<...> { }
```

**CRITICAL**: You must call `scanPackages()` on each registry in your `onEnable()`:

```java
@Override
public void onEnable() {
    String pkg = "com.example.myaddon";

    // For @AutoEffect
    Registry.get(HandlersRegistry.class).scanPackages(pkg);

    // For @AutoHook
    Registry.get(HooksRegistry.class).scanPackages(pkg);

    // For @AutoExtractor
    Registry.get(ExtractorsRegistry.class).scanPackages(pkg);
}
```

### 4. Effect Context

Effect handlers receive a context object with all necessary data:

```java
@Override
public void handle(EffectContext context, MySettings settings) {
    // Access event
    BlockBreakEvent event = context.getEventAs(BlockBreakEvent.class);

    // Access player
    Player player = context.executor();

    // Access item that triggered effect
    ItemStack item = context.itemSource();

    // Access/modify drops
    context.addDrop(new ItemStack(Material.DIAMOND));
    context.drops().clear();

    // Access affected blocks (from Hammer/Vein Mining)
    for (Block block : context.affectedBlocks()) {
        // Process blocks
    }
}
```

---

## Development Workflow

### 1. Create Your Effect

```java
package com.example.myaddon.effects;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.effects.engine.EmptySettings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

@AutoEffect("EXPLOSION_MINING")
public class ExplosionMining
    implements EffectHandler.SingleEventEffectHandler<EmptySettings, BlockBreakEvent> {

    @Override
    public void handle(EffectContext context, EmptySettings settings) {
        Block block = context.getEventAs(BlockBreakEvent.class).getBlock();

        // Create explosion
        block.getWorld().createExplosion(
            block.getLocation(),
            4.0f,  // Power
            false, // Don't set fire
            false  // Don't break blocks
        );

        // Add extra drops
        context.addDrop(new ItemStack(Material.TNT, 1));
    }

    @Override
    public int priority() {
        return 0;
    }
}
```

### 2. Register Your Package

```java
package com.example.myaddon;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.api.registries.Registry;
import org.bukkit.plugin.java.JavaPlugin;

public class MyAddon extends JavaPlugin {

    @Override
    public void onEnable() {
        ItemsPlugin zItems = (ItemsPlugin) getServer()
            .getPluginManager()
            .getPlugin("zItems");

        if (zItems == null) {
            getLogger().severe("zItems not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // IMPORTANT: Scan your package for @AutoEffect annotations
        Registry.get(HandlersRegistry.class).scanPackages("com.example.myaddon");

        getLogger().info("Custom effects registered!");
    }
}
```

### 3. Test Your Effect

Create a test configuration:

```yaml
# effects/explosion_mining_test.yml
id: "explosion_mining_test"
type: "EXPLOSION_MINING"
display-name: "<red><bold>💥 EXPLOSION MINING</bold></red>"
```

Create a test item:

```yaml
# items/explosion_pickaxe.yml
id: "explosion_pickaxe"
material: DIAMOND_PICKAXE
display-name: "<red>Explosion Pickaxe</red>"
effects:
  - explosion_mining_test
```

Test in-game:

```
/zitems reload
/zitems item give @s explosion_pickaxe
```

### 4. Add Custom Settings

```java
package com.example.myaddon.settings;

import fr.traqueur.items.api.effects.EffectSettings;
import org.bukkit.inventory.ItemStack;

public record ExplosionSettings(
    float power,
    boolean setFire,
    boolean breakBlocks
) implements EffectSettings {
    @Override
    public boolean canApplyTo(ItemStack item) {
        return item.getType().name().contains("PICKAXE");
    }
}
```

Update your handler:

```java
@AutoEffect("EXPLOSION_MINING")
public class ExplosionMining
    implements EffectHandler.SingleEventEffectHandler<ExplosionSettings, BlockBreakEvent> {

    @Override
    public void handle(EffectContext context, ExplosionSettings settings) {
        Block block = context.getEventAs(BlockBreakEvent.class).getBlock();

        block.getWorld().createExplosion(
            block.getLocation(),
            settings.power(),
            settings.setFire(),
            settings.breakBlocks()
        );
    }

    @Override
    public int priority() {
        return 0;
    }
}
```

Update configuration:

```yaml
id: "explosion_mining"
type: "EXPLOSION_MINING"
display-name: "<red><bold>💥 EXPLOSION MINING</bold></red>"
power: 4.0
set-fire: false
break-blocks: false
```

---

## Best Practices

### 1. Always Call scanPackages()

```java
@Override
public void onEnable() {
    // REQUIRED for annotations to work!
    Registry.get(HandlersRegistry.class).scanPackages("your.package.name");
}
```

Without this, your `@AutoEffect`, `@AutoHook`, and `@AutoExtractor` annotations will be ignored!

### 2. Use Sealed Interfaces

zItems uses sealed interfaces for type safety:

```java
// EffectHandler is sealed - implement one of:
// - SingleEventEffectHandler<Settings, Event>
// - MultiEventEffectHandler<Settings>
// - NoEventEffectHandler<Settings>
```

### 3. Handle Errors Gracefully

```java
@Override
public void handle(EffectContext context, MySettings settings) {
    try {
        // Your logic
    } catch (Exception e) {
        Logger.severe("Error in effect: {}", e, e.getMessage());
        // Don't crash the server!
    }
}
```

### 4. Respect Permissions

```java
import fr.traqueur.items.infrastructure.support.EventUtil;

// Check if player can break block
if (!EventUtil.canBreakBlock(player, block.getLocation())) {
    return; // Respect protection plugins
}
```

### 5. Use Priority Correctly

```java
// Run early (before other effects)
@Override
public int priority() {
    return 1;  // Higher = earlier
}

// Run late (after other effects)
@Override
public int priority() {
    return -1;  // Lower = later
}
```

### 6. Document Your Code

```java
/**
 * Explosion Mining Effect
 *
 * Creates an explosion when mining blocks, adding visual flair
 * without actually destroying terrain.
 *
 * @since 1.0.0
 * @author YourName
 */
@AutoEffect("EXPLOSION_MINING")
public class ExplosionMining implements EffectHandler<...> {
    // ...
}
```

---

## Complete Example Plugin

Here's a complete working example:

**Main Class**:

```java
package com.example.explosionmining;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.api.registries.Registry;
import org.bukkit.plugin.java.JavaPlugin;

public class ExplosionMiningPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Check for zItems
        ItemsPlugin zItems = (ItemsPlugin) getServer()
            .getPluginManager()
            .getPlugin("zItems");

        if (zItems == null) {
            getLogger().severe("zItems not found! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register package for @AutoEffect scanning
        Registry.get(HandlersRegistry.class)
            .scanPackages("com.example.explosionmining");

        getLogger().info("Explosion Mining effect registered!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Explosion Mining disabled!");
    }
}
```

**Effect Handler**:

```java
package com.example.explosionmining.effects;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import com.example.explosionmining.settings.ExplosionSettings;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

@AutoEffect("EXPLOSION_MINING")
public class ExplosionMining
    implements EffectHandler.SingleEventEffectHandler<ExplosionSettings, BlockBreakEvent> {

    @Override
    public void handle(EffectContext context, ExplosionSettings settings) {
        BlockBreakEvent event = context.getEventAs(BlockBreakEvent.class);
        Block block = event.getBlock();

        // Create visual explosion
        block.getWorld().createExplosion(
            block.getLocation().add(0.5, 0.5, 0.5),
            settings.power(),
            settings.setFire(),
            settings.breakBlocks()
        );

        // Spawn particles
        block.getWorld().spawnParticle(
            Particle.EXPLOSION_HUGE,
            block.getLocation().add(0.5, 0.5, 0.5),
            1
        );

        // Play sound
        block.getWorld().playSound(
            block.getLocation(),
            Sound.ENTITY_GENERIC_EXPLODE,
            1.0f,
            1.0f
        );

        // Bonus drops
        if (settings.bonusDrops()) {
            context.addDrop(new ItemStack(Material.TNT, 1));
        }
    }

    @Override
    public int priority() {
        return 0;
    }
}
```

**Settings Class**:

```java
package com.example.explosionmining.settings;

import fr.traqueur.items.api.effects.EffectSettings;
import org.bukkit.inventory.ItemStack;

public record ExplosionSettings(
    float power,
    boolean setFire,
    boolean breakBlocks,
    boolean bonusDrops
) implements EffectSettings {

    @Override
    public boolean canApplyTo(ItemStack item) {
        // Only pickaxes can have this effect
        return item.getType().name().endsWith("_PICKAXE");
    }
}
```

**plugin.yml**:

```yaml
name: ExplosionMining
version: 1.0.0
main: com.example.explosionmining.ExplosionMiningPlugin
api-version: 1.21
depend: [zItems]
author: YourName
description: Adds explosion effects to mining
```

---

## Troubleshooting

### Effect Not Registered

**Problem**: "Handler not found for effect type: MY_EFFECT"

**Solution**: Make sure you called `scanPackages()` in `onEnable()`:

```java
Registry.get(HandlersRegistry.class).scanPackages("your.package.name");
```

### Wrong Package Scanned

**Problem**: Effects in subpackages not found

**Solution**: Scan the base package - it will scan all subpackages:

```java
// Good - scans all subpackages
scanPackages("com.example.myaddon");

// Bad - only scans specific package
scanPackages("com.example.myaddon.effects");
```

### Settings Not Loading

**Problem**: Effect settings always null or default

**Solution**: Make sure your settings class:
1. Implements `EffectSettings`
2. Is a `record` or has proper getters
3. Has field names matching YAML keys (snake_case → camelCase)

---

## Resources

- **JavaDocs**: (Coming soon)
- **Example Plugins**: Check `/examples` in the GitHub repository
- **GitHub**: [zItems Repository](https://github.com/GroupeZ-dev/zItems)
- **Discord**: Join for development support

---

Need help? Ask in our [Discord](https://groupez.dev) or open an issue on [GitHub](https://github.com/GroupeZ-dev/zItems/issues)!