package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.events.CustomBlockBreakEvent;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneHammerConfiguration;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;

import java.util.*;

public class Hammer implements RuneActivator<RuneHammerConfiguration> {

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, RuneHammerConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {

        Block block = event.getBlock();
        Player player = event.getPlayer();
        BlockFace face = getBlockFace(player);
        if (face != BlockFace.UP && face != BlockFace.DOWN) {
            face = player.getFacing();
        }
        int depth = runeConfiguration.getDepth();
        int width = runeConfiguration.getWidth();
        int height = runeConfiguration.getHeight();
        Set<Block> blocks = origin;
        int nbBlocks = 0;
        for (int d = 0; d < depth; d++) {
            for (int h = -height / 2; h <= height / 2; h++) {
                for (int w = -width / 2; w <= width / 2; w++) {
                    Block targetBlock = getRelativeBlock(block, face, player.getLocation().getYaw(), d, h, w);
                    if(targetBlock == null || blocks.contains(targetBlock)) continue;
                    blocks.add(targetBlock);
                    nbBlocks++;
                    List<ItemStack> dropsAfter = drops.getOrDefault(targetBlock.getLocation(), new ArrayList<>());
                    dropsAfter.addAll(targetBlock.getDrops(player.getInventory().getItemInMainHand()));
                    drops.put(targetBlock.getLocation(), dropsAfter);
                    if(runeConfiguration.isEventBlockBreakEvent()) {
                        CustomBlockBreakEvent customBlockBreakEvent = new CustomBlockBreakEvent(targetBlock, player);
                        plugin.getServer().getPluginManager().callEvent(customBlockBreakEvent);
                    }
                }
            }
        }

        if(nbBlocks > 0 && runeConfiguration.isDamage()) {
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if(meta != null && meta instanceof Damageable damageable) {
                damageable.setDamage(damageable.getDamage() + getDamageToRemove(item, nbBlocks+1));
                item.setItemMeta(meta);
            }
        }

       return blocks;
    }

    private int getDamageToRemove(ItemStack itemStack, int nbBlocks) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return nbBlocks;
        }

        int levelUnbreaking = meta.getEnchantLevel(Enchantment.UNBREAKING);
        if (levelUnbreaking == 0) {
            return nbBlocks;
        }

        Random random = new Random();
        int newDamage = 0;
        for (int i = 0; i < nbBlocks; i++) {
            if (random.nextInt(100) < 100 / (levelUnbreaking + 1)) {
                newDamage++;
            }
        }

        return newDamage;
    }

    private Block getRelativeBlock(Block baseBlock, BlockFace face, float yaw, int depth, int height, int width) {
        switch (face) {
            case NORTH:
                return baseBlock.getRelative(width, height, -depth);
            case SOUTH:
                return baseBlock.getRelative(width, height, depth);
            case EAST:
                return baseBlock.getRelative(depth, height, width);
            case WEST:
                return baseBlock.getRelative(-depth, height, width);
            case UP, DOWN:

                if ((yaw > 45 && yaw <= 135) || (yaw < -45 && yaw >= -135)) {
                    return (face == BlockFace.UP)
                            ? baseBlock.getRelative(height, -depth, width)
                            : baseBlock.getRelative(height, depth, width);
                } else {
                    return (face == BlockFace.UP)
                            ? baseBlock.getRelative(width, -depth, height)
                            : baseBlock.getRelative(width, depth, height);
                }
            default:
                return null;
        }
    }


    private BlockFace getBlockFace(Player player) {
        Location eyeLoc = player.getEyeLocation();
        RayTraceResult result = player.getLocation().getWorld().rayTraceBlocks(eyeLoc,eyeLoc.getDirection(),10, FluidCollisionMode.NEVER);

        if(result == null) {
            throw new IllegalStateException("Block was too far away");
        }

        // Block was too far away?
        if(result.getHitBlockFace() == null) {
            throw new IllegalStateException("Block was too far away");
        }

        // Got block face
        return result.getHitBlockFace();
    }

    @Override
    public void interactBlock(ItemPlugin plugin, PlayerInteractEvent event, RuneHammerConfiguration runeConfiguration) {}

    @Override
    public void applyOnItems(ItemPlugin plugin, ItemMeta itemMeta, RuneHammerConfiguration runeConfiguration) throws Exception {}

    @Override
    public int getPriority() {
        return 0;
    }
}
