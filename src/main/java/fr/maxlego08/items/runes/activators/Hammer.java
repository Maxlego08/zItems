package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneHammerConfiguration;
import fr.maxlego08.items.api.runes.handlers.BreakHandler;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class Hammer implements BreakHandler<RuneHammerConfiguration>, RuneActivator {

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, RuneHammerConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        BlockFace face = getAdjustedBlockFace(player);
        int nbBlocks = processBlocks(plugin, player, runeConfiguration, origin, drops, block, face);
        if (nbBlocks == 0) return new HashSet<>();

        event.setCancelled(true); // Need to cancel the event for don't apply the damage to the item

        int damage = runeConfiguration.isMaxDamage() ? nbBlocks : runeConfiguration.getDamage();
        if (damage > 0) applyDamageToItem(player.getInventory().getItemInMainHand(), damage, player);

        return origin;
    }

    private int processBlocks(ItemPlugin plugin, Player player, RuneHammerConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops, Block block, BlockFace face) {
        int depth = runeConfiguration.getDepth();
        int width = runeConfiguration.getWidth();
        int height = runeConfiguration.getHeight();
        int nbBlocks = 0;

        for (int d = 0; d < depth; d++) {
            for (int h = -height / 2; h <= height / 2; h++) {
                for (int w = -width / 2; w <= width / 2; w++) {
                    Block targetBlock = getRelativeBlock(block, face, player.getLocation().getYaw(), d, h, w);
                    if (targetBlock != null && isValidTargetBlock(plugin, player, targetBlock, origin, runeConfiguration)) {
                        origin.add(targetBlock);
                        nbBlocks++;
                        if (!triggerBlockBreakEvent(runeConfiguration, targetBlock, player)) {
                            addBlockDrops(player, drops, targetBlock);
                        }
                    }
                }
            }
        }
        return nbBlocks;
    }

    private void addBlockDrops(Player player, Map<Location, List<ItemStack>> drops, Block targetBlock) {
        List<ItemStack> dropsAfter = drops.getOrDefault(targetBlock.getLocation(), new ArrayList<>());
        dropsAfter.addAll(targetBlock.getDrops(player.getInventory().getItemInMainHand()));
        drops.put(targetBlock.getLocation(), dropsAfter);
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

        if (result == null) {
            throw new IllegalStateException("Block was too far away");
        }

        // Block was too far away?
        if (result.getHitBlockFace() == null) {
            throw new IllegalStateException("Block was too far away");
        }

        // Got block face
        return result.getHitBlockFace();
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
