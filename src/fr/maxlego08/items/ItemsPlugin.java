package fr.maxlego08.items;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.configurations.ItemConfiguration;
import fr.maxlego08.items.api.configurations.commands.CommandsListener;
import fr.maxlego08.items.api.configurations.global.GlobalConfiguration;
import fr.maxlego08.items.api.enchantments.Enchantments;
import fr.maxlego08.items.api.hook.BlockAccess;
import fr.maxlego08.items.api.hook.HookManager;
import fr.maxlego08.items.api.hook.Hooks;
import fr.maxlego08.items.api.menus.ApplicatorMenu;
import fr.maxlego08.items.api.menus.buttons.*;
import fr.maxlego08.items.api.recipes.ZItemHook;
import fr.maxlego08.items.api.runes.RuneManager;
import fr.maxlego08.items.api.utils.TrimHelper;
import fr.maxlego08.items.command.commands.CommandItem;
import fr.maxlego08.items.components.PaperComponent;
import fr.maxlego08.items.components.SpigotComponent;
import fr.maxlego08.items.enchantments.DisableEnchantsListener;
import fr.maxlego08.items.enchantments.ZEnchantments;
import fr.maxlego08.items.hook.ZHookManager;
import fr.maxlego08.items.hook.jobs.JobsHook;
import fr.maxlego08.items.hook.jobs.ZJobsHook;
import fr.maxlego08.items.hook.packs.ItemsAdderHook;
import fr.maxlego08.items.hook.shops.ShopHooks;
import fr.maxlego08.items.hook.worlds.WorldGuardHook;
import fr.maxlego08.items.listener.GrindstoneListener;
import fr.maxlego08.items.listener.SmithingTableListener;
import fr.maxlego08.items.listener.SpawnerListener;
import fr.maxlego08.items.placeholder.LocalPlaceholder;
import fr.maxlego08.items.runes.RuneListener;
import fr.maxlego08.items.runes.ZRuneManager;
import fr.maxlego08.items.save.Config;
import fr.maxlego08.items.save.MessageLoader;
import fr.maxlego08.items.zcore.ZPlugin;
import fr.maxlego08.items.zcore.utils.builder.CooldownBuilder;
import fr.maxlego08.items.zcore.utils.plugins.Plugins;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.button.loader.NoneLoader;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.folialib.FoliaLib;
import fr.maxlego08.menu.zcore.utils.folialib.impl.PlatformScheduler;
import fr.traqueur.recipes.api.RecipesAPI;
import fr.traqueur.recipes.api.hook.Hook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ItemsPlugin extends ZPlugin implements ItemPlugin {

    private final TrimHelper trimHelper = new TrimHelper();
    private final ItemManager itemManager = new ZItemManager(this);
    private final RuneManager runeManager = new ZRuneManager(this);
    private final HookManager hookManager = new ZHookManager(this);
    private final Enchantments enchantments = new ZEnchantments();
    private final List<BlockAccess> blockAccesses = new ArrayList<>();
    private ItemComponent itemComponent;
    private RuneListener runeListener;
    private PlatformScheduler scheduler;
    private RecipesAPI recipesAPI;
    private GlobalConfiguration globalConfiguration;

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zitems");

        this.preEnable();
        this.saveDefaultConfig();

        this.scheduler = new FoliaLib(this).getScheduler();

        this.getProvider(ButtonManager.class).unregisters(this);
        this.getProvider(ButtonManager.class).register(new NoneLoader(this, ItemsButton.class, "ZITEMS_ITEMS"));
        this.getProvider(ButtonManager.class).register(new NoneLoader(this, ApplicatorInputButton.class, "ZITEMS_RUNE_APPLICATOR_INPUTS"));
        this.getProvider(ButtonManager.class).register(new NoneLoader(this, ApplicatorBaseInputButton.class, "ZITEMS_RUNE_APPLICATOR_BASE_INPUT"));
        this.getProvider(ButtonManager.class).register(new NoneLoader(this, ApplicatorExtraInputButton.class, "ZITEMS_RUNE_APPLICATOR_EXTRA_INPUTS"));
        this.getProvider(ButtonManager.class).register(new NoneLoader(this, ApplicatorOutputButton.class, "ZITEMS_RUNE_APPLICATOR_OUTPUT"));
        this.getProvider(ButtonManager.class).register(new NoneLoader(this, ApplicatorRuneInputButton.class, "ZITEMS_RUNE_APPLICATOR_RUNE_INPUT"));
        this.loadInventories();

        this.enchantments.register();
        this.itemComponent = isPaperVersion() ? new PaperComponent() : new SpigotComponent();

        this.registerCommand("zitems", new CommandItem(this), "items", "zit");

        var servicesManager = this.getServer().getServicesManager();
        servicesManager.register(ItemManager.class, this.itemManager, this, ServicePriority.Highest);
        servicesManager.register(RuneManager.class, this.runeManager, this, ServicePriority.Highest);
        servicesManager.register(ItemPlugin.class, this, this, ServicePriority.Highest);
        servicesManager.register(Enchantments.class, this.enchantments, this, ServicePriority.Highest);

        this.addListener(new DisableEnchantsListener(this.itemManager));
        this.addListener(new CommandsListener(this.itemManager));
        this.addListener(new GrindstoneListener(this.itemManager));
        this.addListener(new SmithingTableListener(this.itemManager, this.runeManager));
        this.addListener(new SpawnerListener());

        this.addSave(Config.getInstance());
        this.addSave(CooldownBuilder.getInstance());
        this.addSave(new MessageLoader(this));

        this.recipesAPI = new RecipesAPI(this, Config.enableDebug);
        Hook.addHook(new ZItemHook(this));

        this.runeManager.loadRunes();
        this.itemManager.loadItems();
        this.itemManager.loadCrafts();
        this.runeManager.loadCraftWithRunes();

        // Rune listener
        this.addListener(this.runeListener = new RuneListener(this, this.runeManager));

        this.loadFiles();

        if (this.isEnable(Plugins.WORLDGUARD)) {
            this.registerBlockAccess(new WorldGuardHook());
        }

        //Register all internal hooks
        List<Hooks> hooksList = new ArrayList<>(List.of(
                new Hooks(Plugins.JOBS, new JobsHook(this.runeManager)),
                new Hooks(Plugins.ZJOBS, new ZJobsHook(this.runeManager)),
                new Hooks(Plugins.ITEMSADDER, new ItemsAdderHook(this))
                // ToDo, add more hook
        ));
        Stream.of(ShopHooks.values()).forEach(shopHooks -> hooksList.add(new Hooks(shopHooks.getPlugin(), shopHooks)));

        hooksList.forEach(hooks -> this.hookManager.registerHook(hooks.plugins(), hooks.hook()));

        this.getServer().getScheduler().runTask(this, () -> {
            //Load one tick later to permit addon to register hooks
            this.hookManager.loadHooks(this::isEnable);
        });

        this.globalConfiguration = new GlobalConfiguration(getConfig());

        this.postEnable();
    }

    @Override
    public void onDisable() {

        this.preDisable();

        this.saveFiles();

        this.postDisable();
    }

    @Override
    public void reloadFiles() {
        super.reloadFiles();
        this.globalConfiguration = new GlobalConfiguration(getConfig());
        this.loadInventories();
    }

    private void loadInventories(){
        try {
            this.getProvider(InventoryManager.class).loadInventoryOrSaveResource(this, "inventories/items_gui.yml");
            this.getProvider(InventoryManager.class).loadInventoryOrSaveResource(this, "inventories/rune_applicator.yml", ApplicatorMenu.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ItemComponent getItemComponent() {
        return itemComponent;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    @Override
    public List<BlockAccess> getBlockAccess() {
        return this.blockAccesses;
    }

    @Override
    public void registerBlockAccess(BlockAccess blockAccess) {
        this.blockAccesses.add(blockAccess);
    }

    @Override
    public boolean hasAccess(Player player, Location location) {
        return this.blockAccesses.isEmpty() || this.blockAccesses.stream().allMatch(blockAccess -> blockAccess.hasAccess(player, location));
    }

    @Override
    public GlobalConfiguration getGlobalConfiguration() {
        return this.globalConfiguration;
    }

    public Enchantments getEnchantments() {
        return enchantments;
    }

    public TrimHelper getTrimHelper() {
        return trimHelper;
    }

    @Override
    public Item createItem(String name, ItemConfiguration itemConfiguration) {
        return new ZItem(this, name, itemConfiguration);
    }

    @Override
    public PlatformScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public RecipesAPI getRecipesAPI() {
        return this.recipesAPI;
    }

    @Override
    public HookManager getHookManager() {
        return hookManager;
    }

    @Override
    public RuneManager getRuneManager() {
        return runeManager;
    }

    public RuneListener getRuneListener() {
        return runeListener;
    }
}
