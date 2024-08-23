package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.events.CustomBlockBreakEvent;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneFarmingHoeConfiguration;
import fr.maxlego08.items.zcore.utils.ElapsedTime;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FarmingHoe implements RuneActivator<RuneFarmingHoeConfiguration> {
    
    /**
     * Plants seeds in a specified area around a given block.
     *
     * @param block                   The block to plant seeds around.
     * @param runeFarmingHoeConfiguration The configuration for the farming hoe.
     * @param world                   The world in which the event occurs.
     * @param event                   The player interact event.
     * @param player                  The player planting the seeds.
     */
    private void plantSeeds(Block block, RuneFarmingHoeConfiguration runeFarmingHoeConfiguration, World world, PlayerInteractEvent event, Player player) {
        int range = runeFarmingHoeConfiguration.size() / 2;
        Material baseBlockType = block.getType();

        if (baseBlockType == Material.FARMLAND || baseBlockType == Material.SOUL_SAND) {
            event.setCancelled(true);
            Set<Material> allowedSeeds = new HashSet<>(runeFarmingHoeConfiguration.allowedPlantSeeds());

            for (int x = -range; x <= range; x++) {
                for (int z = -range; z <= range; z++) {
                    Block currentBlock = world.getBlockAt(block.getX() + x, block.getY(), block.getZ() + z);
                    if (isValidBlock(currentBlock, baseBlockType)) {
                        plantSeedIfPossible(currentBlock, allowedSeeds, player, baseBlockType);
                    }
                }
            }
        }
    }

    /**
     * Checks if a block is valid for planting seeds.
     *
     * @param block         The block to check.
     * @param baseBlockType The base block type for comparison.
     * @return True if the block is valid for planting, false otherwise.
     */
    private boolean isValidBlock(Block block, Material baseBlockType) {
        return block.getType() == baseBlockType && block.getRelative(BlockFace.UP).getType().isAir();
    }

    /**
     * Plants a seed on a given block if possible.
     *
     * @param block         The block to plant the seed on.
     * @param allowedSeeds  The set of allowed seeds.
     * @param player        The player planting the seed.
     * @param baseBlockType The base block type.
     */
    private void plantSeedIfPossible(Block block, Set<Material> allowedSeeds, Player player, Material baseBlockType) {
        for (Material seedMaterial : allowedSeeds) {
            if (isMatchingSeed(seedMaterial, baseBlockType)) {
                ItemStack seed = new ItemStack(seedMaterial, 1);
                if (player.getInventory().containsAtLeast(seed, 1)) {
                    Material cropType = getCropTypeFromSeed(seedMaterial);
                    if (!cropType.isAir()) {
                        block.getRelative(BlockFace.UP).setType(cropType);
                        player.getInventory().removeItem(seed);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Checks if a seed matches the given base block type.
     *
     * @param seedMaterial  The seed material.
     * @param baseBlockType The base block type.
     * @return True if the seed matches the base block type, false otherwise.
     */
    private boolean isMatchingSeed(Material seedMaterial, Material baseBlockType) {
        if (baseBlockType == Material.FARMLAND) {
            return seedMaterial != Material.NETHER_WART;
        } else if (baseBlockType == Material.SOUL_SAND) {
            return seedMaterial == Material.NETHER_WART;
        }
        return false;
    }

    /**
     * Drops items at a specified location.
     *
     * @param player                  The player causing the drops.
     * @param drops                   The collection of items to drop.
     * @param location                The location to drop the items at.
     * @param runeFarmingHoeConfiguration The configuration for the farming hoe.
     */
    private Collection<ItemStack> dropItem(Player player, Collection<ItemStack> drops, Location location, RuneFarmingHoeConfiguration runeFarmingHoeConfiguration) {
        World world = location.getWorld();
        drops.removeIf(dropItemStack -> runeFarmingHoeConfiguration.blacklistMaterials().contains(dropItemStack.getType()));

        if (runeFarmingHoeConfiguration.dropItemInInventory()) {
            var inventory = player.getInventory();
            @NotNull HashMap<Integer, ItemStack> result = inventory.addItem(drops.toArray(new ItemStack[0]));
            return result.values();
        }
        return drops;
    }

    /**
     * Checks if a block can become soil.
     *
     * @param block The block to check.
     * @return True if the block can become soil, false otherwise.
     */
    private boolean canBecomeSoil(Block block) {
        if (block == null) return false;

        Material type = block.getType();
        return type == Material.DIRT || type == Material.GRASS_BLOCK || type == Material.PODZOL || type == Material.MYCELIUM;
    }

    /**
     * Gets the crop type corresponding to a seed material.
     *
     * @param seed The seed material.
     * @return The corresponding crop type, or Material.AIR if not found.
     */
    private Material getCropTypeFromSeed(Material seed) {
        if (seed == null) {
            return Material.AIR;
        }

        return switch (seed) {
            case WHEAT_SEEDS -> Material.WHEAT;
            case CARROT -> Material.CARROTS;
            case POTATO -> Material.POTATOES;
            case BEETROOT_SEEDS -> Material.BEETROOTS;
            case PUMPKIN_SEEDS -> Material.PUMPKIN_STEM;
            case MELON_SEEDS -> Material.MELON_STEM;
            default -> Material.AIR;
        };
    }

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, RuneFarmingHoeConfiguration farmingHoeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> dropsOrigin) {
        Set<Block> blocks = new HashSet<>();
        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();

        var world = player.getWorld();

        for (Block farmBlock : origin) {
            if (!(farmBlock.getBlockData() instanceof Ageable)) continue;

            int range = farmingHoeConfiguration.size() / 2;

            var allowedBlock = farmingHoeConfiguration.allowedCrops();
            if (!allowedBlock.contains(farmBlock.getType())) continue;

            event.setCancelled(true);
            boolean needToRemoveDamage = false;

            ElapsedTime elapsedTime = new ElapsedTime("Hoe");
            elapsedTime.start();

            for (int x = -range; x <= range; x++) {
                for (int z = -range; z <= range; z++) {

                    var block = world.getBlockAt(farmBlock.getX() + x, farmBlock.getY(), farmBlock.getZ() + z);

                    if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {

                        if (!allowedBlock.contains(block.getType())) continue;

                        if (!plugin.hasAccess(player, block.getLocation())) continue;

                        if (farmingHoeConfiguration.eventBlockBreakEvent()) {
                            var blockEvent = new CustomBlockBreakEvent(block, player);
                            blockEvent.callEvent();

                            if (blockEvent.isCancelled()) continue;
                        }

                        needToRemoveDamage = true;
                        var drops = block.getDrops(itemStack, player);

                        var dropLocation = switch (farmingHoeConfiguration.dropItemType()) {
                            case BLOCK -> block.getLocation();
                            case CENTER -> farmBlock.getLocation();
                            case PLAYER -> player.getLocation();
                        };

                        var dropsAfter = dropItem(player, drops, dropLocation, farmingHoeConfiguration);
                        dropsOrigin.put(dropLocation, new ArrayList<>(dropsAfter));

                        if (farmingHoeConfiguration.autoReplant()) {
                            ageable.setAge(0);
                            block.setBlockData(ageable);
                            block.getState().update();
                        } else {
                            blocks.add(block);
                        }
                    }
                }
            }

            if (needToRemoveDamage && farmingHoeConfiguration.damage() >= 1) {
                itemStack.damage(farmingHoeConfiguration.damage(), player);
            }

            elapsedTime.endDisplay();
        }

        return blocks;
    }

    @Override
    public void interactBlock(ItemPlugin plugin, PlayerInteractEvent event, RuneFarmingHoeConfiguration runeFarmingHoeConfiguration) {
        var player = event.getPlayer();
        var world = player.getWorld();
        var itemStack = player.getInventory().getItemInMainHand();
        var block = event.getClickedBlock();
        if (block == null) return;

        if (!canBecomeSoil(block)) {
            if (runeFarmingHoeConfiguration.plantSeeds()) {
                plantSeeds(block, runeFarmingHoeConfiguration, world, event, player);
            }
            return;
        }

        if (!runeFarmingHoeConfiguration.harvest()) return;

        int range = runeFarmingHoeConfiguration.size() / 2;
        event.setCancelled(true);
        boolean needToRemoveDamage = false;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {

                var soilBlock = world.getBlockAt(block.getX() + x, block.getY(), block.getZ() + z);
                if (canBecomeSoil(soilBlock)) {
                    needToRemoveDamage = true;
                    soilBlock.setType(Material.FARMLAND);
                }
            }
        }

        if (needToRemoveDamage && runeFarmingHoeConfiguration.harvestDamage() >= 1) {
            itemStack.damage(runeFarmingHoeConfiguration.harvestDamage(), player);
        }
    }

    @Override
    public void applyOnItems(ItemsPlugin plugin, ItemStack itemStack, RuneFarmingHoeConfiguration runeConfiguration) {}

    @Override
    public int getPriority() {
        return 0;
    }
}
