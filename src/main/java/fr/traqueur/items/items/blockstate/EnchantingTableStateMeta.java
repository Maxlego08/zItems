package fr.traqueur.items.items.blockstate;

import fr.traqueur.items.api.services.BlockComponentService;
import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.annotations.AutoBlockStateMeta;
import fr.traqueur.items.api.items.BlockStateMeta;
import fr.traqueur.items.paper.PaperBlockComponentService;
import fr.traqueur.structura.annotations.Options;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.block.EnchantingTable;
import org.bukkit.entity.Player;

/**
 * BlockState configuration for enchanting table blocks.
 * Allows setting custom name.
 */
@AutoBlockStateMeta("enchanting-table")
public record EnchantingTableStateMeta(
        @Options(optional = true) Component customName
) implements BlockStateMeta<EnchantingTable> {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final BlockComponentService BLOCK_COMPONENT_SERVICE =
            PlatformType.isPaper() ? new PaperBlockComponentService() : null;

    @Override
    public void apply(Player player, EnchantingTable enchantingTable) {
        if (customName != null) {
            if (BLOCK_COMPONENT_SERVICE != null) {
                BLOCK_COMPONENT_SERVICE.setCustomName(enchantingTable, customName);
            } else {
                enchantingTable.setCustomName(LEGACY_SERIALIZER.serialize(customName));
            }
        }
    }
}