package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.EmptyConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import fr.maxlego08.items.api.runes.handlers.BucketHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Infinite Bucket Rune Activator
 * <p>
 * Makes buckets infinite based on their type:
 * - Empty bucket: Stays empty (infinite empty bucket for picking up fluids)
 * - Water bucket: Never empties (infinite water source)
 * - Lava bucket: Never empties (infinite lava source)
 * - Other filled buckets: Never empty
 */
public class InfiniteBucket implements BucketHandler<EmptyConfiguration>, RuneActivator {

    private static final List<Material> FORBIDDEN_MATERIALS = List.of(
            Material.MILK_BUCKET,
            Material.POWDER_SNOW_BUCKET
    );

    @Override
    public void onBucketEmpty(ItemPlugin plugin, PlayerBucketEmptyEvent event, EmptyConfiguration runeConfiguration) {
        Material bucketType = event.getBucket();
        if(FORBIDDEN_MATERIALS.contains(bucketType)) {
            return;
        }
        event.setCancelled(true);
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        block.setType(this.getFilledBlockMaterial(bucketType));
    }

    private @NotNull Material getFilledBlockMaterial(Material bucketType) {
        return switch (bucketType) {
            case LAVA_BUCKET -> Material.LAVA;
            default -> Material.WATER;
        };
    }

    @Override
    public void onBucketFill(ItemPlugin plugin, PlayerBucketFillEvent event, EmptyConfiguration runeConfiguration) {
        event.setCancelled(true);
        event.getBlockClicked().setType(Material.AIR);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}