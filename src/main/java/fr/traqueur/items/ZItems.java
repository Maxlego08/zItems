package fr.traqueur.items;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.loader.NoneLoader;
import fr.traqueur.commands.spigot.CommandManager;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.effects.EffectsDispatcher;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.managers.EffectsManager;
import fr.traqueur.items.api.managers.ItemsManager;
import fr.traqueur.items.api.registries.*;
import fr.traqueur.items.api.settings.Settings;
import fr.traqueur.items.api.settings.models.AttributeMergeStrategy;
import fr.traqueur.items.api.utils.MessageUtil;
import fr.traqueur.items.blocks.BlockTracker;
import fr.traqueur.items.blocks.BlockTrackerListener;
import fr.traqueur.items.blocks.ZItemsProvider;
import fr.traqueur.items.buttons.ItemsListButton;
import fr.traqueur.items.buttons.applicator.ApplicatorButton;
import fr.traqueur.items.buttons.applicator.ApplicatorOutputButton;
import fr.traqueur.items.commands.CommandsMessageHandler;
import fr.traqueur.items.commands.ZItemsCommand;
import fr.traqueur.items.commands.arguments.EffectArgument;
import fr.traqueur.items.commands.arguments.ItemArgument;
import fr.traqueur.items.effects.ZEffectsDispatcher;
import fr.traqueur.items.effects.ZEffectsManager;
import fr.traqueur.items.effects.ZEventsListener;
import fr.traqueur.items.hooks.recipes.RecipesHook;
import fr.traqueur.items.inventories.ApplicatorMenu;
import fr.traqueur.items.items.ZItemsManager;
import fr.traqueur.items.listeners.*;
import fr.traqueur.items.registries.*;
import fr.traqueur.items.serialization.Keys;
import fr.traqueur.items.serialization.ZEffectDataType;
import fr.traqueur.items.serialization.ZTrackedBlockDataType;
import fr.traqueur.items.settings.PluginSettings;
import fr.traqueur.items.settings.readers.*;
import fr.traqueur.items.shop.ShopProviders;
import fr.traqueur.recipes.api.RecipesAPI;
import fr.traqueur.recipes.api.hook.Hook;
import fr.traqueur.structura.api.Structura;
import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.registries.CustomReaderRegistry;
import fr.traqueur.structura.registries.DefaultValueRegistry;
import fr.traqueur.structura.types.TypeToken;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ZItems extends ItemsPlugin {

    private static final String CONFIG_FILE = "config.yml";
    private static final String MESSAGES_FILE = "messages.yml";
    public static final String ITEMS_FOLDER = "items";
    public static final String EFFECTS_FOLDER = "effects";

    private org.slf4j.Logger logger;

    private RecipesAPI recipesManager;
    private EffectsDispatcher dispatcher;
    private InventoryManager inventoryManager;
    private ButtonManager buttonManager;

    @Override
    public void onLoad() {
        this.registerRegistries();
    }

    @Override
    public void onEnable() {

        long enableTime = System.currentTimeMillis();

        logger = org.slf4j.LoggerFactory.getLogger("zItems");

        this.saveDefaultConfig();
        this.injectReaders();

        PluginSettings settings = this.createSettings(CONFIG_FILE, PluginSettings.class);
        Logger.init(logger, settings.debug());

        Logger.info("<yellow>=== ENABLE START ===");
        Logger.info("<gray>Plugin Version V<red>{}", this.getDescription().getVersion());

        MessageUtil.initialize(this);
        ZEffectDataType.initialize();
        ZTrackedBlockDataType.initialize();
        Keys.initialize(this);

        this.recipesManager = new RecipesAPI(this, settings.debug());
        Hook.addHook(new RecipesHook(this));

        this.reloadConfig();

        if (!ShopProviders.initialize()) {
            Logger.severe("No shop provider found! Disabling plugin.");
            Logger.info("Available shop providers:");
            for (ShopProviders shopProviders : ShopProviders.values()) {
                Logger.info("- <gold>{}", shopProviders.pluginName());
            }
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Logger.info("Shop provider <green>{} <reset>has been found.", ShopProviders.FOUND_PROVIDER.pluginName());

        this.populateRegistries();

        Logger.info("Setting up event dispatching system...");
        this.dispatcher = new ZEffectsDispatcher();
        ZEventsListener eventsListener = new ZEventsListener(this.dispatcher);
        eventsListener.registerDynamicListeners(this);
        Logger.info("<green>Event dispatching system initialized successfully!");

        // Register legacy rune migration listener (zItemsOld backward compatibility)
        if(PlatformType.isPaper()) {
            this.getServer().getPluginManager().registerEvents(new LegacyMigrationListener(), this);
            Logger.info("<gold>Legacy rune migration system enabled - zItemsOld items will be auto-migrated");
        } else {
            Logger.warning("Legacy rune migration system is only possible with paper spigot!");
        }


        this.registerListeners();

        EffectsManager effectsManager = this.registerManager(EffectsManager.class, new ZEffectsManager());
        ItemsManager itemsManager = this.registerManager(ItemsManager.class, new ZItemsManager());
        itemsManager.generateRecipesFromLoadedItems();
        effectsManager.loadRecipes();

        this.getServer().getPluginManager().registerEvents(new BlockTrackerListener(BlockTracker.get(), itemsManager, effectsManager), this);

        this.loadButtons();
        this.loadInventories();

        this.registerCommands(settings);

        Logger.info("<yellow>=== ENABLE DONE <gray>(<gold>" + Math.abs(enableTime - System.currentTimeMillis()) + "ms<gray>) <yellow>===");
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new CommandsListener(), this);
        this.getServer().getPluginManager().registerEvents(new DisableEnchantsListener(), this);
        this.getServer().getPluginManager().registerEvents(new ItemRestrictionsListener(this), this);
        this.getServer().getPluginManager().registerEvents(new AnvilEffectFusionListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SmithingTableListener(this), this);
        this.getServer().getPluginManager().registerEvents(new StripLogListener(), this);
    }

    private void populateRegistries() {
        Registry.get(HooksRegistry.class).scanPackage(this, "fr.traqueur.items");
        Registry.get(HandlersRegistry.class).scanPackage(this, "fr.traqueur.items");
        Registry.get(ExtractorsRegistry.class).scanPackage(this, "fr.traqueur.items");
        Registry.get(HooksRegistry.class).enableAll();
        Registry.get(EffectsRegistry.class).loadFromFolder();
        Registry.get(ItemsRegistry.class).loadFromFolder();
        Registry.get(CustomBlockProviderRegistry.class).register(this.getName().toLowerCase(), new ZItemsProvider());
    }

    private void registerRegistries() {
        Registry.register(LocationAccessRegistry.class, new ZLocationAccessRegistry());
        // Register custom block provider registry
        Registry.register(CustomBlockProviderRegistry.class, new ZCustomBlockProviderRegistry());
        // Register and scan hooks
        Registry.register(HooksRegistry.class, new ZHooksRegistry());
        // Register and scan effect handlers
        Registry.register(HandlersRegistry.class, new ZHandlersRegistry(this));
        // Register and load effects from files
        Registry.register(EffectsRegistry.class, new ZEffectsRegistry(this));
        // Register and load items from files
        Registry.register(ItemsRegistry.class, new ZItemsRegistry(this));
        // Register and scan extractors
        Registry.register(ExtractorsRegistry.class, new ZExtractorsRegistry(this));
        // Register applicators registry
        Registry.register(ApplicatorsRegistry.class, new ZApplicatorsRegistry());
    }

    private void registerCommands(PluginSettings settings) {
        CommandManager<@NotNull ItemsPlugin> commandManager = new CommandManager<>(this);
        commandManager.setLogger(new fr.traqueur.commands.api.logging.Logger() {
            @Override
            public void error(String s) {
                Logger.severe(s);
            }

            @Override
            public void info(String s) {
                Logger.info(s);
            }
        });
        commandManager.setDebug(settings.debug());
        commandManager.setMessageHandler(new CommandsMessageHandler());

        commandManager.registerConverter(Effect.class, new EffectArgument());
        commandManager.registerConverter(Item.class, new ItemArgument());

        commandManager.registerCommand(new ZItemsCommand(this));
    }

    private void loadButtons() {
        // Initialize zMenu ButtonManager
        var buttonProvider = getServer().getServicesManager().getRegistration(ButtonManager.class);
        if (buttonProvider == null) {
            Logger.severe("zMenu ButtonManager not found! Is zMenu installed?");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        buttonManager = buttonProvider.getProvider();

        // Register custom buttons
        buttonManager.unregisters(this);
        buttonManager.register(new NoneLoader(this, ItemsListButton.class, "ZITEMS_ITEMS_LIST"));
        buttonManager.register(new NoneLoader(this, ApplicatorButton.Input.class, "ZITEMS_EFFECT_APPLICATOR_INPUTS"));
        buttonManager.register(new NoneLoader(this, ApplicatorButton.BaseInput.class, "ZITEMS_EFFECT_APPLICATOR_BASE_INPUT"));
        buttonManager.register(new NoneLoader(this, ApplicatorButton.EffectInput.class, "ZITEMS_EFFECT_APPLICATOR_EFFECT_INPUT"));
        buttonManager.register(new NoneLoader(this, ApplicatorOutputButton.class, "ZITEMS_EFFECT_APPLICATOR_OUTPUT"));

        Logger.info("Registered <green>custom zMenu buttons<reset>!");
    }

    private void loadInventories() {
        // Initialize zMenu InventoryManager and ButtonManager
        var inventoryProvider = getServer().getServicesManager().getRegistration(InventoryManager.class);
        if (inventoryProvider == null) {
            Logger.severe("zMenu InventoryManager not found! Is zMenu installed?");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.inventoryManager = inventoryProvider.getProvider();
        Logger.info("zMenu <green>InventoryManager <reset>initialized successfully!");

        if(this.inventoryManager != null && this.buttonManager != null) {
            try {
                this.inventoryManager.deleteInventories(this);
                this.inventoryManager.loadInventoryOrSaveResource(this, "inventories/items_list.yml");
                this.inventoryManager.loadInventoryOrSaveResource(this, "inventories/effect_applicator.yml", ApplicatorMenu.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void injectReaders() {
        CustomReaderRegistry.getInstance().register(EquipmentSlotGroup.class, new EquipmentSlotGroupReader());
        CustomReaderRegistry.getInstance().register(Attribute.class, new AttributeReader());
        CustomReaderRegistry.getInstance().register(Enchantment.class, new EnchantmentReader());
        CustomReaderRegistry.getInstance().register(new TypeToken<>() {}, new TagReader());
        CustomReaderRegistry.getInstance().register(Component.class, new ComponentReader());
        CustomReaderRegistry.getInstance().register(Effect.class, new EffectReader());
        CustomReaderRegistry.getInstance().register(PotionEffectType.class, new PotionEffectTypeReader());
        CustomReaderRegistry.getInstance().register(PotionType.class, new PotionTypeReader());
        CustomReaderRegistry.getInstance().register(Color.class, new ColorReader());
        CustomReaderRegistry.getInstance().register(TrimMaterial.class, new TrimMaterialReader());
        CustomReaderRegistry.getInstance().register(TrimPattern.class, new TrimPatternReader());
        CustomReaderRegistry.getInstance().register(Sound.class, new SoundReader());
        CustomReaderRegistry.getInstance().register(PatternType.class, new PatternTypeReader());
        CustomReaderRegistry.getInstance().register(new TypeToken<>() {}, new DamageTypeReader());

        DefaultValueRegistry.getInstance().register(AttributeMergeStrategy.class, AttributeMergeStrategy.DefaultStrategy.class, AttributeMergeStrategy.DefaultStrategy::value);
    }

    @Override
    public void onDisable() {
        long disableTime = System.currentTimeMillis();
        Logger.info("<yellow>=== DISABLE START ===");
        Logger.info("<gray>Plugin Version V<red>{}", this.getDescription().getVersion());

        BlockTracker.get().clearCache();

        MessageUtil.close();

        Logger.info("<yellow>=== DISABLE DONE <gray>(<gold>" + Math.abs(disableTime - System.currentTimeMillis()) + "ms<gray>) <yellow>===");
    }

    @Override
    public void saveDefaultConfig() {
        this.saveIfNotExits(CONFIG_FILE);
        this.saveIfNotExits(MESSAGES_FILE);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        PluginSettings settings = this.createSettings(CONFIG_FILE, PluginSettings.class);
        Logger.setDebug(settings.debug());
        try {
            Structura.loadEnum(this.getDataFolder().toPath().resolve(MESSAGES_FILE), Messages.class);
        } catch (StructuraException e) {
            this.logger.error("Failed to load messages configuration.", e);
        }

        EffectsRegistry effectsRegistry = Registry.get(EffectsRegistry.class);
        if (effectsRegistry != null) {
            effectsRegistry.loadFromFolder();
        }

        ItemsRegistry registry = Registry.get(ItemsRegistry.class);
        if (registry != null) {
            registry.loadFromFolder();
        }

        ItemsManager itemsManager = this.getManager(ItemsManager.class);
        if (itemsManager != null) {
            itemsManager.generateRecipesFromLoadedItems();
        }

        EffectsManager effectsManager = this.getManager(EffectsManager.class);
        if (effectsManager != null) {
            effectsManager.loadRecipes();
        }

        this.loadInventories();
    }

    private <T extends Settings> T createSettings(String path, Class<T> clazz) {
        File file = new File(this.getDataFolder(), path);
        if (!file.exists()) {
            throw new IllegalArgumentException("File " + path + " does not exist.");
        }
        T instance = Structura.load(file, clazz);
        Settings.register(clazz, instance);
        return instance;
    }

    private void saveIfNotExits(String fileName) {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }
        File file = new File(this.getDataFolder(), fileName);
        if (!file.exists()) {
            this.saveResource(fileName, false);
        }
    }

    @Override
    public RecipesAPI getRecipesManager() {
        return recipesManager;
    }

    @Override
    public EffectsDispatcher getDispatcher() {
        return dispatcher;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
