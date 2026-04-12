package fr.traqueur.items.listeners;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoListener;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.registries.EffectsRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.serialization.Keys;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * Automatically migrates legacy zItemsOld items to modern zItems format.
 * <p>
 * Uses {@link EntityEquipmentChangedEvent} which is Paper-only.
 */
@AutoListener
@PaperOnly
public class LegacyMigrationListener implements Listener {

    private final NamespacedKey legacyRunesKey;
    private final NamespacedKey legacyItemIdKey;

    public LegacyMigrationListener() {
        this.legacyRunesKey = new NamespacedKey("zitems", "runes");
        this.legacyItemIdKey = new NamespacedKey("zitems", "item-id");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityEquipmentChanged(EntityEquipmentChangedEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        event.getEquipmentChanges().forEach((equipmentSlot, equipmentChange) -> {
            migrateItemIfNeeded(player.getEquipment().getItem(equipmentSlot), player);
        });
    }

    private void migrateItemIfNeeded(ItemStack item, Player player) {
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        boolean migrated = false;

        if (pdc.has(legacyItemIdKey, PersistentDataType.STRING)) {
            String itemId = pdc.get(legacyItemIdKey, PersistentDataType.STRING);
            if (itemId != null && !itemId.isEmpty()) {
                Logger.debug("Migrating legacy item ID '{}' for player {}", itemId, player.getName());
                Keys.ITEM_ID.set(pdc, itemId);
                pdc.remove(legacyItemIdKey);
                migrated = true;
                Logger.debug("Successfully migrated custom item ID: '{}'", itemId);
            }
        }

        if (!pdc.has(legacyRunesKey, PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING))) {
            if (migrated) {
                item.setItemMeta(meta);
            }
            return;
        }

        Logger.info("Migrating legacy rune item for player: {}", player.getName());

        try {
            List<String> runeNames = pdc.get(
                    legacyRunesKey,
                    PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING)
            );

            if (runeNames == null || runeNames.isEmpty()) {
                Logger.debug("No rune names found in legacy data, removing key");
                pdc.remove(legacyRunesKey);
                item.setItemMeta(meta);
                return;
            }

            Logger.debug("Found {} legacy rune names: {}", runeNames.size(), runeNames);

            EffectsRegistry effectsRegistry = Registry.get(EffectsRegistry.class);
            List<Effect> migratedEffects = new ArrayList<>();

            for (String runeName : runeNames) {
                Effect registeredEffect = effectsRegistry.getById(runeName);
                if (registeredEffect == null) {
                    Logger.warning("No effect found for rune name: '{}' (player: {})", runeName, player.getName());
                    Logger.warning("Make sure an effect with id='{}' exists in your effects configurations", runeName);
                    continue;
                }
                migratedEffects.add(registeredEffect);
                Logger.debug("Migrated rune '{}' → effect with type '{}'", runeName, registeredEffect.type());
            }

            if (migratedEffects.isEmpty()) {
                Logger.warning("No effects could be migrated for player {} - no matching effect configs found", player.getName());
            } else {
                Keys.EFFECTS.set(pdc, migratedEffects);
                Logger.info("Successfully migrated {} effect(s) for player {}", migratedEffects.size(), player.getName());
            }

            pdc.remove(legacyRunesKey);
            item.setItemMeta(meta);

        } catch (Exception e) {
            Logger.severe("Error migrating legacy item for player {}: {}", e, player.getName(), e.getMessage());
        }
    }
}