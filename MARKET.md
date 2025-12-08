# ⚔️ zItems

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/GroupeZ-dev/zItems)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green.svg)](https://www.spigotmc.org/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)](https://groupez.dev)

A modern, extensible Minecraft plugin for creating advanced custom items with powerful effects and seamless third-party integrations.

## 📋 Description

**zItems** revolutionizes custom item creation on your Minecraft server. Built with cutting-edge Java 21 architecture, create unique tools, weapons, and armor with powerful effects like 3D area mining, automatic selling, advanced farming, and much more. Transform your server rewards, crate items, and donation perks into truly legendary gear!

## 🎬 Showcase

> **Note:** GIFs are demonstrations of the plugin's capabilities in action!

### Hammer Effect - 3x3 Area Mining
Watch as players mine entire areas at once with the powerful Hammer effect. Configurable dimensions and material filters make it perfect for any mining scenario.

![Hammer Mining](https://img.groupez.dev/zitems/hammer-mining.gif)

### Vein Miner - Instant Ore Vein Mining
Mine an entire ore vein with a single break! The Vein Miner effect intelligently detects connected ores and mines them all at once.

![Vein Miner](https://img.groupez.dev/zitems/vein-miner.gif)

### Farming Hoe - Auto-Harvest & Replant
Revolutionary farming with auto-harvest, auto-replant, and area tilling. Perfect for farming servers and automated agriculture!

![Farming Hoe](https://img.groupez.dev/zitems/farming-hoe.gif)

### Effect Application - Smithing Table
Apply effects to items using the vanilla Smithing Table. Familiar mechanics for your players with full customization!

![Smithing Table Application](https://img.groupez.dev/zitems/smithing-application.gif)

### Effect Application - Custom GUI
Advanced applicator with ingredient requirements. Create progression systems where players need rare materials to upgrade their gear!

![Custom GUI Application](https://img.groupez.dev/zitems/custom-applicator.gif)

### Auto-Sell Effect
Automatically sell mined blocks and drops! Perfect for prison and economy servers. Watch the money roll in as you mine!

![Auto-Sell](https://img.groupez.dev/zitems/auto-sell.gif)

### Silk Spawner Effect
Mine spawners and keep them as items! Compatible with all mob types and custom block providers.

![Silk Spawner](https://img.groupez.dev/zitems/silk-spawner.gif)

### Beautiful Effect Display
Dynamic lore showing effects with full visibility control. Use gradients and MiniMessage formatting for stunning item descriptions!

![Effect Lore Display](https://img.groupez.dev/zitems/effect-lore.gif)

### Full Video Showcase
See all features in action with our comprehensive video tutorial!

[![zItems Showcase](https://img.youtube.com/vi/VIDEO_ID/maxresdefault.jpg)](https://youtube.com/watch?v=VIDEO_ID)

## ✨ Key Features

- **⚡ 16+ Powerful Effects** - Hammer, Vein Mining, Auto-Sell, Farming Hoe, Silk Spawners, XP Boost, and more
- **🎨 Complete Item Customization** - Names, lore, enchantments, attributes, armor trims, leather colors, potions, food metadata
- **🔧 Effect Application System** - Apply effects via Smithing Table or custom ingredient-based GUI
- **💎 Smart Effect Display** - Dynamic lore with full visibility control for base and additional effects
- **📦 Recipe System** - Create custom crafting recipes (shaped/shapeless) with vanilla or custom ingredients
- **🔗 11 Plugin Integrations** - Jobs, WorldGuard, ItemsAdder, Nexo, Oraxen, shops, and more
- **🎮 Beautiful GUIs** - Powered by zMenu for stunning inventory interfaces
- **⚙️ Hot Reload** - Update configurations without restarting your server
- **🚀 Modern Architecture** - Java 21, efficient PDC storage, Folia support

## ⚔️ Built-in Effects

### ⛏️ Mining & Harvesting

| Effect           | Description                                                       |
|------------------|-------------------------------------------------------------------|
| **HAMMER**       | Mine in configurable 3D areas (e.g., 3x3x3) with material filters |
| **VEIN_MINING**  | Mine entire ore veins at once with block limit control            |
| **MELT_MINING**  | Auto-smelt ores while mining (instant furnace processing)         |
| **SILK_SPAWNER** | Mine spawners and keep them as items with mob type                |
| **FARMING_HOE**  | Auto-harvest, auto-replant, area tilling, and seed planting       |
| **ABSORPTION**   | Auto-pickup drops directly to inventory                           |

### 💰 Economy & Progression

| Effect              | Description                                           |
|---------------------|-------------------------------------------------------|
| **AUTO_SELL**       | Auto-sell drops with configurable price multiplier    |
| **SELL_STICK**      | Right-click containers to sell all contents           |
| **XP_BOOST**        | Multiply XP gained with configurable boost and chance |
| **JOB_XP_BOOST**    | Boost Jobs/ZJobs plugin XP earnings                   |
| **JOB_MONEY_BOOST** | Boost Jobs/ZJobs plugin money earnings                |

### 🛡️ Item Enhancement

| Effect                    | Description                                                                 |
|---------------------------|-----------------------------------------------------------------------------|
| **ATTRIBUTES_APPLICATOR** | Apply custom attributes (attack damage, speed, armor, movement speed, etc.) |
| **ENCHANTS_APPLICATOR**   | Apply enchantments with level control and increase/decrease evolution       |
| **UNBREAKABLE**           | Make items unbreakable - perfect for rewards                                |
| **INFINITE_BUCKET**       | Never-ending water and lava buckets                                         |

## 🎁 Item Customization

**Everything You Need:**

- **Display**: Custom names with gradients, hex colors, multi-line lore, custom model data, max stack size
- **Enchantments**: Any enchantment at any level with hide flag support
- **Attributes**: Custom attributes with 5 merge strategies (REPLACE, ADD, KEEP_HIGHEST, KEEP_LOWEST, SUM)
- **Armor**: Leather dyeing (hex colors), armor trims (any material/pattern)
- **Potions**: Custom colors, base types, multiple effects with duration/amplifier
- **Food**: Custom nutrition, saturation, eat duration, potion effects on consumption (Paper only)
- **Recipes**: Create shaped or shapeless crafting recipes with custom ingredients

## 🔧 Effect Representation

Transform effects into **tradeable items** that players can obtain, trade, and apply!

**Two Application Methods:**

- **Smithing Table**: Use vanilla mechanics - familiar and simple
- **Custom Applicator GUI**: Require specific ingredients for advanced effects

**Perfect For:**
- Server rewards and achievements
- Donation perks and crate items
- Player-driven economy
- Progression systems

**Example:**
```yaml
representation:
  material: EMERALD
  display-name: "<green>⛏ Vein Miner Crystal</green>"
  applicator-type: SMITHING_TABLE
  applicable-materials:
    - DIAMOND_PICKAXE
    - NETHERITE_PICKAXE
```

## 🔗 Plugin Integrations

**11 Automatic Integrations** - No configuration needed!

| Plugin                                   | Type          | Features                                         |
|------------------------------------------|---------------|--------------------------------------------------|
| **Jobs / ZJobs**                         | Economy       | XP/Money boost effects                           |
| **ItemsAdder / Nexo / Oraxen**           | Custom Blocks | Auto-detect custom blocks for Hammer/Vein Mining |
| **WorldGuard / SuperiorSkyBlock2**       | Protection    | Respect region/island permissions                |
| **PlaceholderAPI**                       | Placeholders  | Use in messages and configs                      |
| **EconomyShopGUI / ShopGUIPlus / ZShop** | Shops         | Auto-sell integration                            |

## 🎮 Commands

| Command                                          | Description                        |
|--------------------------------------------------|------------------------------------|
| `/zitems give <player> <item> [amount]`          | Give custom item to player         |
| `/zitems apply <player> <effect>`                | Apply effect to player's held item |
| `/zitems effect give <player> <effect> [amount]` | Give effect as tradeable item      |
| `/zitems effect view`                            | View effects on held item          |
| `/zitems gui`                                    | Open zItems GUI                    |
| `/zitems reload`                                 | Reload all configurations          |

**Aliases:** `/zit`, `/zitem`

## ⚙️ Configuration Examples

### Powerful Mining Pickaxe

```yaml
id: "super_pickaxe"
material: DIAMOND_PICKAXE
display-name: "<gradient:#00FFFF:#0080FF><bold>⛏ Legendary Pickaxe</bold></gradient>"
lore:
  - "<gray>A pickaxe imbued with incredible power"
  - ""
  - "<yellow>• Mines in 3x3 areas"
  - "<yellow>• Auto-smelts ores"
  - "<yellow>• 200% XP gain"

effects:
  - super_hammer
  - auto_smelt_pickaxe
  - xp_boost_pickaxe

enchantments:
  - enchantment: efficiency
    level: 5
  - enchantment: fortune
    level: 3

unbreakable: true
```

### Custom Armor with Attributes

```yaml
id: "ruby_chestplate"
material: LEATHER_CHESTPLATE
display-name: "<gradient:#FF0000:#8B0000><bold>💎 Ruby Chestplate</bold></gradient>"

enchantments:
  - enchantment: protection
    level: 5

attributes:
  - attribute: armor
    operation: ADD_NUMBER
    amount: 10.0
    slot: CHEST
  - attribute: movement_speed
    operation: ADD_SCALAR
    amount: 0.2  # +20% speed
    slot: CHEST

attribute-merge-strategy: SUM

metadata:
  leather-armor:
    color: "#FF0000"
  trim:
    material: DIAMOND
    pattern: VEX
```

### Tradeable Effect Item

```yaml
id: "vein_miner_effect"
type: "VEIN_MINING"
display-name: "<gradient:#34eb9b:#2ecc71><bold>⛏ VEIN MINING</bold></gradient>"

representation:
  material: EMERALD
  display-name: "<green>⛏ Vein Miner Crystal</green>"
  applicator-type: SMITHING_TABLE
  applicable-materials:
    - DIAMOND_PICKAXE
    - NETHERITE_PICKAXE

materials:
  - COAL_ORE
  - IRON_ORE
  - DIAMOND_ORE
  - EMERALD_ORE

block-limit: 64
```

## 📦 Requirements

- **Server**: Paper or Spigot 1.21.5+
- **Java**: Version 21+
- **Required**: [zMenu](https://www.spigotmc.org/resources/zmenu.109103/)

**Optional**: Jobs, PlaceholderAPI, WorldGuard, SuperiorSkyBlock2, ItemsAdder, Nexo, Oraxen, shop plugins

## 🚀 Quick Start

1. Download **zItems** and **zMenu**
2. Place both JARs in `plugins/` folder
3. Restart server
4. Edit configs in `plugins/zItems/`:
   - `items/*.yml` - Your custom items
   - `effects/*.yml` - Reusable effect templates
   - `messages.yml` - Customize all messages
5. Use `/zitems reload` to apply changes
6. Give items: `/zitems give <player> <item>`

## 💡 Why Choose zItems?

✅ **Easy to Use** - YAML configuration with extensive examples
✅ **Extremely Powerful** - 16+ effects with unlimited combinations
✅ **Well Integrated** - Works with 11 popular plugins automatically
✅ **Performance Optimized** - Modern Java 21 architecture, efficient PDC storage
✅ **Beautiful** - MiniMessage formatting with gradients and hex colors
✅ **Flexible** - Complete control over visibility, applicability, and behavior
✅ **Production Ready** - Folia support, hot reload, actively maintained

## 🎯 Perfect For

- 🏆 **Server Rewards** - Achievement and milestone rewards
- 💎 **Donation Perks** - Exclusive custom tools for supporters
- 🎁 **Crate Items** - Exciting rewards for your crates
- ⚔️ **RPG Servers** - Custom weapons and armor with unique abilities
- 🏭 **Prison Servers** - Progressive mining tools with auto-sell
- 🌾 **Farming Servers** - Advanced farming automation
- 🏝️ **Skyblock Servers** - Custom progression and tools
- 🎮 **Survival Servers** - Enhanced vanilla gameplay

## 🎓 Developer API

Create custom effects with our annotation-driven API:

```java
@AutoEffect("MY_CUSTOM_EFFECT")
public class MyEffect implements EffectHandler.SingleEventEffectHandler<MySettings, BlockBreakEvent> {
    @Override
    public void handle(EffectContext context, MySettings settings, BlockBreakEvent event) {
        // Your effect logic
        context.addDrops(event.getBlock().getDrops());
        context.breakBlock(event.getBlock());
    }
}
```

**Maven Dependency:**
```xml
<dependency>
    <groupId>fr.traqueur</groupId>
    <artifactId>zItems-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🤝 Support & Links

- **Website**: [groupez.dev](https://groupez.dev)
- **Author**: Traqueur_
- **Documentation**: [GitHub Repository](https://github.com/GroupeZ-dev/zItems)
- **Issues**: [GitHub Issues](https://github.com/GroupeZ-dev/zItems/issues)
- **Discord**: Join our community for support

---

**Ready to transform your server's item system?**

Download now and start creating legendary items that your players will love! ⚔️

---

Developed with ❤️ by [Traqueur_](https://groupez.dev)