package fr.traqueur.items.menu;

import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.traqueur.items.Messages;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.managers.EffectsManager;
import fr.traqueur.items.api.models.Folder;
import fr.traqueur.items.api.registries.EffectsRegistry;
import fr.traqueur.items.api.registries.ItemsRegistry;
import fr.traqueur.items.api.registries.Registry;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Displays folders, items, and effects (with representation and folders) in one unified inventory.
 *
 * <p>Navigation state is tracked entirely via player metadata — no dependency on zMenu's internal
 * inventory history. The custom {@link ZItemsBackButton} pops from the navigation stack to go back.</p>
 *
 * <p>IMPORTANT: reopen() MUST use openInventoryWithOldInventories (Inventory object overload) and NOT
 * the String-name overload. The String overload calls player.closeInventory() first, which fires
 * onInventoryClose() and wipes all navigation metadata before the new inventory opens.</p>
 */
public class ItemsListButton extends PaginateButton {

    static final String METADATA_KEY_FOLDER = "zitems-current-folder";
    static final String METADATA_KEY_EFFECTS_MODE = "zitems-showing-effects";
    static final String METADATA_KEY_EFFECTS_FOLDER = "zitems-current-effects-folder";
    private static final String METADATA_KEY_NAV_STACK = "zitems-nav-stack";

    private final ItemsPlugin plugin;

    public ItemsListButton(Plugin plugin) {
        this.plugin = (ItemsPlugin) plugin;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {
        if (player.hasMetadata(METADATA_KEY_EFFECTS_MODE)) {
            renderEffects(player, inventory);
        } else {
            renderItems(player, inventory);
        }
    }

    /* -------------------- Items rendering -------------------- */

    private void renderItems(Player player, InventoryEngine inventory) {
        List<Element<Item>> elements = getItemElements(player);

        paginate(elements, inventory, (slot, element) -> {
            if (element.isFolder()) {
                Placeholders placeholders = new Placeholders();
                placeholders.register("name", element.folder().displayName());
                placeholders.register("material", element.folder().displayMaterial().name());
                placeholders.register("model-id", String.valueOf(element.folder().displayModelId()));

                inventory.addItem(slot, getItemStack().build(player, false, placeholders))
                        .setClick(event -> {
                            pushCurrentState(player, plugin);
                            Folder<Effect> effectFolder = Registry.get(EffectsRegistry.class).getRootFolder();
                            if (effectFolder.name().equalsIgnoreCase(element.folder().name())) {
                                player.setMetadata(METADATA_KEY_EFFECTS_MODE, new FixedMetadataValue(plugin, true));
                                player.removeMetadata(METADATA_KEY_FOLDER, plugin);
                                player.removeMetadata(METADATA_KEY_EFFECTS_FOLDER, plugin);
                            } else {
                                player.setMetadata(METADATA_KEY_FOLDER,
                                        new FixedMetadataValue(plugin, element.folder()));
                                player.removeMetadata(METADATA_KEY_EFFECTS_MODE, plugin);
                            }
                            reopen(player);
                        });
            } else {
                try {
                    ItemStack itemStack = element.item().build(player, 1);
                    inventory.addItem(slot, itemStack).setClick(event -> {
                        ItemStack giveItem = element.item().build(player, 1);
                        var rest = player.getInventory().addItem(giveItem);
                        rest.values().forEach(dropped ->
                                player.getWorld().dropItem(player.getLocation(), dropped)
                        );
                        Messages.ITEM_RECEIVED.send(player,
                                Placeholder.parsed("item", element.item().representativeName()),
                                Placeholder.parsed("amount", "1"));
                    });
                } catch (Exception e) {
                    Logger.severe("Failed to build item {}", element.item().id(), e);
                }
            }
        });
    }

    /* -------------------- Effects rendering -------------------- */

    private void renderEffects(Player player, InventoryEngine inventory) {
        List<Element<Effect>> elements = getEffectElements(player);
        EffectsManager effectsManager = plugin.getManager(EffectsManager.class);

        paginate(elements, inventory, (slot, element) -> {
            if (element.isFolder()) {
                Folder<Effect> folder = element.folder();
                Placeholders placeholders = new Placeholders();
                placeholders.register("name", folder.displayName());
                placeholders.register("material", folder.displayMaterial().name());
                placeholders.register("model-id", String.valueOf(folder.displayModelId()));

                inventory.addItem(slot, getItemStack().build(player, false, placeholders))
                        .setClick(event -> {
                            pushCurrentState(player, plugin);
                            player.setMetadata(METADATA_KEY_EFFECTS_FOLDER, new FixedMetadataValue(plugin, folder));
                            reopen(player);
                        });
            } else {
                Effect effect = element.item();
                if (effect.representation() == null) return;

                ItemStack effectItem = effectsManager.createEffectItem(effect, player);
                inventory.addItem(slot, effectItem).setClick(event -> {
                    ItemStack giveItem = effectsManager.createEffectItem(effect, player);
                    var rest = player.getInventory().addItem(giveItem);
                    rest.values().forEach(dropped ->
                            player.getWorld().dropItem(player.getLocation(), dropped)
                    );
                });
            }
        });
    }

    @Override
    public int getPaginationSize(Player player) {
        if (player.hasMetadata(METADATA_KEY_EFFECTS_MODE)) {
            return getEffectElements(player).size();
        }
        return getItemElements(player).size();
    }

    @Override
    public void onInventoryClose(Player player, InventoryEngine inventory) {
        // Intentionally empty.
        // onInventoryClose fires on ALL inventory transitions (page changes, folder navigation,
        // true close). Wiping metadata here would break pagination and folder state.
        // Metadata is cleaned in GuiCommand before a fresh GUI open instead.
    }

    /* -------------------- Navigation stack (package-visible for ZItemsBackButton) -------------------- */

    @SuppressWarnings("unchecked")
    static Deque<NavState> getNavStack(Player player) {
        if (!player.hasMetadata(METADATA_KEY_NAV_STACK)) return new ArrayDeque<>();
        Object value = player.getMetadata(METADATA_KEY_NAV_STACK).getFirst().value();
        return value instanceof Deque<?> ? (Deque<NavState>) value : new ArrayDeque<>();
    }

    static void pushCurrentState(Player player, Plugin plugin) {
        boolean effectsMode = player.hasMetadata(METADATA_KEY_EFFECTS_MODE);
        Folder<Item> itemsFolder = null;
        Folder<Effect> effectsFolder = null;

        if (player.hasMetadata(METADATA_KEY_FOLDER)) {
            Object v = player.getMetadata(METADATA_KEY_FOLDER).getFirst().value();
            if (v instanceof Folder<?> f) {
                //noinspection unchecked
                itemsFolder = (Folder<Item>) f;
            }
        }
        if (player.hasMetadata(METADATA_KEY_EFFECTS_FOLDER)) {
            Object v = player.getMetadata(METADATA_KEY_EFFECTS_FOLDER).getFirst().value();
            if (v instanceof Folder<?> f) {
                //noinspection unchecked
                effectsFolder = (Folder<Effect>) f;
            }
        }

        Deque<NavState> stack = getNavStack(player);
        stack.push(new NavState(effectsMode, itemsFolder, effectsFolder));
        player.setMetadata(METADATA_KEY_NAV_STACK, new FixedMetadataValue(plugin, stack));
    }

    static boolean popAndRestoreState(Player player, Plugin plugin) {
        Deque<NavState> stack = getNavStack(player);
        if (stack.isEmpty()) return false;

        NavState state = stack.pop();
        player.setMetadata(METADATA_KEY_NAV_STACK, new FixedMetadataValue(plugin, stack));

        player.removeMetadata(METADATA_KEY_FOLDER, plugin);
        player.removeMetadata(METADATA_KEY_EFFECTS_MODE, plugin);
        player.removeMetadata(METADATA_KEY_EFFECTS_FOLDER, plugin);

        if (state.effectsMode()) {
            player.setMetadata(METADATA_KEY_EFFECTS_MODE, new FixedMetadataValue(plugin, true));
        }
        if (state.itemsFolder() != null) {
            player.setMetadata(METADATA_KEY_FOLDER, new FixedMetadataValue(plugin, state.itemsFolder()));
        }
        if (state.effectsFolder() != null) {
            player.setMetadata(METADATA_KEY_EFFECTS_FOLDER, new FixedMetadataValue(plugin, state.effectsFolder()));
        }

        return true;
    }

    /* -------------------- Helpers -------------------- */

    private List<Element<Item>> getItemElements(Player player) {
        List<Element<Item>> elements = new ArrayList<>();

        ItemsRegistry registry = Registry.get(ItemsRegistry.class);
        if (registry == null) return elements;

        Folder<Item> currentFolder = registry.getRootFolder();
        if (player.hasMetadata(METADATA_KEY_FOLDER)) {
            Object value = player.getMetadata(METADATA_KEY_FOLDER).getFirst().value();
            if (value instanceof Folder<?> folder) {
                try {
                    //noinspection unchecked
                    currentFolder = (Folder<Item>) folder;
                } catch (ClassCastException e) {
                    Logger.warning("Invalid folder metadata for player {}", player.getName());
                    player.removeMetadata(METADATA_KEY_FOLDER, plugin);
                }
            }
        }

        if ("root".equalsIgnoreCase(currentFolder.name())) {
            Folder<Effect> effectFolder = Registry.get(EffectsRegistry.class).getRootFolder();
            elements.add(new Element<>(new Folder<>(effectFolder.name(), effectFolder.displayName(), effectFolder.displayMaterial(), effectFolder.displayModelId(), List.of(), List.of())));
        }

        if (currentFolder.subFolders() != null) {
            currentFolder.subFolders().forEach(folder -> elements.add(new Element<>(folder)));
        }

        if (currentFolder.elements() != null) {
            currentFolder.elements().forEach(item -> elements.add(new Element<>(item)));
        }

        return elements;
    }

    private List<Element<Effect>> getEffectElements(Player player) {
        List<Element<Effect>> elements = new ArrayList<>();
        EffectsRegistry effectsRegistry = Registry.get(EffectsRegistry.class);
        if (effectsRegistry == null) return elements;

        Folder<Effect> current = effectsRegistry.getRootFolder();
        if (player.hasMetadata(METADATA_KEY_EFFECTS_FOLDER)) {
            Object value = player.getMetadata(METADATA_KEY_EFFECTS_FOLDER).getFirst().value();
            if (value instanceof Folder<?> folder) {
                try {
                    //noinspection unchecked
                    current = (Folder<Effect>) folder;
                } catch (ClassCastException e) {
                    Logger.warning("Invalid folder metadata for player {}", player.getName());
                    player.removeMetadata(METADATA_KEY_EFFECTS_FOLDER, plugin);
                }
            }
        }

        if (current.subFolders() != null) {
            current.subFolders().forEach(folder -> elements.add(new Element<>(folder)));
        }

        if (current.elements() != null) {
            current.elements().stream()
                    .filter(e -> e.representation() != null)
                    .forEach(effect -> elements.add(new Element<>(effect)));
        }

        return elements;
    }

    /**
     * Reopens the items_list inventory at page 1.
     *
     * <p>Uses openInventoryWithOldInventories (Inventory object overload) intentionally.
     * The String-name overload calls player.closeInventory() first, which would fire
     * onInventoryClose() and erase all navigation metadata before the new inventory opens.</p>
     */
    void reopen(Player player) {
        var invManager = plugin.getInventoryManager();
        invManager.getInventory(plugin, "items_list").ifPresentOrElse(
                inv -> invManager.openInventoryWithOldInventories(player, inv, 1),
                () -> Messages.FAILED_TO_OPEN_GUI.send(player)
        );
    }

    /* -------------------- Inner types -------------------- */

    public record NavState(boolean effectsMode,
                           @Nullable Folder<Item> itemsFolder,
                           @Nullable Folder<Effect> effectsFolder) {}

    public static class Element<T> {
        private final T item;
        private final Folder<T> folder;

        public Element(T item) {
            this.item = item;
            this.folder = null;
        }

        public Element(Folder<T> folder) {
            this.item = null;
            this.folder = folder;
        }

        public T item() {
            return item;
        }

        public Folder<T> folder() {
            return folder;
        }

        public boolean isFolder() {
            return folder != null;
        }

        public boolean isItem() {
            return item != null && !(item instanceof Effect);
        }

        public boolean isEffect() {
            return item instanceof Effect;
        }
    }
}
