package fr.traqueur.items.effects.mining;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.annotations.IncompatibleWith;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.managers.DurabilityManager;
import fr.traqueur.items.api.registries.CustomBlockProviderRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.effects.mining.HammerSettings;
import fr.traqueur.items.infrastructure.support.EventUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.*;

@AutoEffect(value = "HAMMER")
@IncompatibleWith(VeinMiner.class)
public class Hammer implements EffectHandler.SingleEventEffectHandler<HammerSettings, BlockBreakEvent> {

    @Override
    public void handle(EffectContext context, HammerSettings settings) {
        BlockBreakEvent event = context.getEventAs(BlockBreakEvent.class);
        Block block = event.getBlock();
        Player player = context.executor();

        BlockFace face = getAdjustedBlockFace(player);
        Set<Block> blocksToBreak = new HashSet<>();

        int nbBlocks = processBlocks(player, settings, blocksToBreak, block, face);

        if (nbBlocks == 0) {
            return;
        }

        // Cancel the main event to prevent default block break behavior
        event.setCancelled(true);

        int actuallyBrokenBlocks = 0;

        // Collect drops for all blocks
        for (Block targetBlock : blocksToBreak) {
            // Fire BlockBreakEvent for allowed plugins only
            boolean success = EventUtil.fireEvent(new BlockBreakEvent(targetBlock, player), BlockBreakEvent.getHandlerList());
            if (!success) {
                continue; // Skip this block if event was cancelled
            }

            // Add to affected blocks only if not cancelled
            context.affectedBlocks().add(targetBlock);

            // Check if this is a custom block (zItems, ItemsAdder, Nexo, Oraxen, etc.)
            CustomBlockProviderRegistry providerRegistry = Registry.get(CustomBlockProviderRegistry.class);
            Optional<List<ItemStack>> customDrop = providerRegistry.getCustomBlockDrop(targetBlock, player);

            if (customDrop.isPresent()) {
                // Custom block found - add custom item drop
                context.addDrops(customDrop.get());
            } else {
                // Normal block - use vanilla drops
                Collection<ItemStack> blockDrops = targetBlock.getDrops(context.itemSource());
                context.addDrops(blockDrops);
            }

            actuallyBrokenBlocks++;
        }

        // Apply damage based on actually broken blocks
        int damage = settings.damage() == -1 ? actuallyBrokenBlocks : settings.damage();
        if (damage > 0) {
            JavaPlugin.getPlugin(ItemsPlugin.class).getManager(DurabilityManager.class).applyDamage(context.itemSource(), damage, player);
        }
    }

    private int processBlocks(Player player, HammerSettings settings, Set<Block> blocksToBreak, Block block, BlockFace face) {
        int depth = settings.depth();
        int width = settings.width();
        int height = settings.height();
        int nbBlocks = 0;

        for (int d = 0; d < depth; d++) {
            for (int h = -height / 2; h <= height / 2; h++) {
                for (int w = -width / 2; w <= width / 2; w++) {
                    Block targetBlock = getRelativeBlock(block, face, player.getLocation().getYaw(), d, h, w);
                    if (targetBlock != null && isValidTargetBlock(targetBlock, blocksToBreak, settings, player)) {
                        blocksToBreak.add(targetBlock);
                        nbBlocks++;
                    }
                }
            }
        }
        return nbBlocks;
    }

    private boolean isValidTargetBlock(Block targetBlock, Set<Block> alreadyProcessed, HammerSettings settings, Player player) {
        // Don't process the same block twice
        if (alreadyProcessed.contains(targetBlock)) {
            return false;
        }

        // Check if player has permission to break block at this location
        if (!EventUtil.canBreakBlock(player, targetBlock.getLocation())) {
            return false;
        }

        // Use settings to check if block is breakable (includes air, liquid, and material/tag checks)
        return settings.isBreakable(targetBlock);
    }

    private BlockFace getAdjustedBlockFace(Player player) {
        BlockFace face = getBlockFace(player);
        if (face != BlockFace.UP && face != BlockFace.DOWN) {
            face = player.getFacing();
        }
        return face;
    }

    private Block getRelativeBlock(Block baseBlock, BlockFace face, float yaw, int depth, int height, int width) {
        switch (face) {
            case NORTH -> {
                return baseBlock.getRelative(width, height, -depth);
            }
            case SOUTH -> {
                return baseBlock.getRelative(width, height, depth);
            }
            case EAST -> {
                return baseBlock.getRelative(depth, height, width);
            }
            case WEST -> {
                return baseBlock.getRelative(-depth, height, width);
            }
            case UP, DOWN -> {
                if ((yaw > 45 && yaw <= 135) || (yaw < -45 && yaw >= -135)) {
                    return (face == BlockFace.UP) ? baseBlock.getRelative(height, -depth, width) : baseBlock.getRelative(height, depth, width);
                } else {
                    return (face == BlockFace.UP) ? baseBlock.getRelative(width, -depth, height) : baseBlock.getRelative(width, depth, height);
                }
            }
            default -> {
                return null;
            }
        }
    }

    private BlockFace getBlockFace(Player player) {
        Location eyeLoc = player.getEyeLocation();
        RayTraceResult result = player.getLocation().getWorld().rayTraceBlocks(eyeLoc, eyeLoc.getDirection(), 10, FluidCollisionMode.NEVER);

        if (result == null || result.getHitBlockFace() == null) {
            // Fallback to player facing direction if raytrace fails
            return player.getFacing();
        }

        return result.getHitBlockFace();
    }

    @Override
    public int priority() {
        return 1;
    }
}