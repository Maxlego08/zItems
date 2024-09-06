package fr.maxlego08.items;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.configurations.ItemConfiguration;
import fr.maxlego08.items.api.configurations.commands.CommandsListener;
import fr.maxlego08.items.api.configurations.recipes.PrepareCraftListener;
import fr.maxlego08.items.api.enchantments.Enchantments;
import fr.maxlego08.items.api.hook.BlockAccess;
import fr.maxlego08.items.api.hook.HookManager;
import fr.maxlego08.items.api.hook.Hooks;
import fr.maxlego08.items.api.runes.RuneManager;
import fr.maxlego08.items.api.utils.TrimHelper;
import fr.maxlego08.items.command.commands.CommandItem;
import fr.maxlego08.items.components.PaperComponent;
import fr.maxlego08.items.components.SpigotComponent;
import fr.maxlego08.items.enchantments.DisableEnchantsListener;
import fr.maxlego08.items.enchantments.ZEnchantments;
import fr.maxlego08.items.hook.ZHookManager;
import fr.maxlego08.items.hook.jobs.JobsHook;
import fr.maxlego08.items.hook.worlds.WorldGuardHook;
import fr.maxlego08.items.listener.GrindstoneListener;
import fr.maxlego08.items.listener.SpawnerListener;
import fr.maxlego08.items.placeholder.LocalPlaceholder;
import fr.maxlego08.items.runes.RuneListener;
import fr.maxlego08.items.runes.ZRuneManager;
import fr.maxlego08.items.save.Config;
import fr.maxlego08.items.save.MessageLoader;
import fr.maxlego08.items.zcore.ZPlugin;
import fr.maxlego08.items.zcore.utils.builder.CooldownBuilder;
import fr.maxlego08.items.zcore.utils.plugins.Plugins;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemsPlugin extends ZPlugin implements ItemPlugin {

    private final TrimHelper trimHelper = new TrimHelper();
    private final ItemManager itemManager = new ZItemManager(this);
    private final RuneManager runeManager = new ZRuneManager(this);
    private final HookManager hookManager = new ZHookManager(this);
    private final Enchantments enchantments = new ZEnchantments();
    private final List<BlockAccess> blockAccesses = new ArrayList<>();
    private ItemComponent itemComponent;
    private RuneListener runeListener;

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zitems");

        this.preEnable();

        this.enchantments.register();
        this.itemComponent = isPaperVersion() ? new PaperComponent() : new SpigotComponent();

        this.registerCommand("zitems", new CommandItem(this), "items", "zit");

        var servicesManager = this.getServer().getServicesManager();
        servicesManager.register(ItemManager.class, this.itemManager, this, ServicePriority.Highest);
        servicesManager.register(RuneManager.class, this.runeManager, this, ServicePriority.Highest);
        servicesManager.register(ItemPlugin.class, this, this, ServicePriority.Highest);
        servicesManager.register(Enchantments.class, this.enchantments, this, ServicePriority.Highest);

        this.addListener(new PrepareCraftListener(this.runeManager, this.itemManager));
        this.addListener(new DisableEnchantsListener(this.itemManager));
        this.addListener(new CommandsListener(this.itemManager));
        this.addListener(new GrindstoneListener(this.itemManager));
        this.addListener(new SpawnerListener());

        this.addSave(Config.getInstance());
        this.addSave(CooldownBuilder.getInstance());
        this.addSave(new MessageLoader(this));
        this.runeManager.loadRunes();
        this.itemManager.loadItems();
        this.runeManager.loadCraftWithRunes();

        // Rune listener
        this.addListener(this.runeListener = new RuneListener(this, this.runeManager));

        this.loadFiles();

        if (this.isEnable(Plugins.WORLDGUARD)) {
            this.registerBlockAccess(new WorldGuardHook());
        }

        //Register all internal hooks
        List.of(
                new Hooks(Plugins.JOBS, new JobsHook(this.runeManager))
                // ToDo, add more hook
        ).forEach(hooks -> this.hookManager.registerHook(hooks.plugins(), hooks.hook()));

        this.getServer().getScheduler().runTask(this, () -> {
            //Load one tick later to permit addon to register hooks
            this.hookManager.loadHooks(this::isEnable);
        });

        this.postEnable();
    }

    @Override
    public void onDisable() {

        this.preDisable();

        this.saveFiles();

        this.postDisable();
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

    public HookManager getHookManager() {
        return hookManager;
    }

    public RuneManager getRuneManager() {
        return runeManager;
    }

    public RuneListener getRuneListener() {
        return runeListener;
    }
}
