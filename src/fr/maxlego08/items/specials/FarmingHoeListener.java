package fr.maxlego08.items.specials;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.configurations.specials.FarmingHoeConfiguration;
import fr.maxlego08.items.api.events.FarmingHoeBlockBreakEvent;
import fr.maxlego08.items.zcore.utils.ElapsedTime;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Listener class for handling events related to the Farming Hoe.
 * Extends SpecialHelper with FarmingHoeConfiguration.
 */
public class FarmingHoeListener extends SpecialHelper<FarmingHoeConfiguration> implements Listener {

    /**
     * Constructor for the FarmingHoeListener.
     *
     * @param plugin The plugin instance.
     */
    public FarmingHoeListener(ItemsPlugin plugin) {
        super(plugin, ItemType.FARMING_HOE);
    }

    /**
     * Handles the BlockBreakEvent to implement the special behavior of the Farming Hoe.
     *
     * @param event The BlockBreakEvent triggered by breaking a block.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.isCancelled() || event instanceof FarmingHoeBlockBreakEvent) return;

        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();

        if (!isSpecialItem(itemStack)) return;

        var world = player.getWorld();
        var farmBlock = event.getBlock();

        if (!(farmBlock.getBlockData() instanceof Ageable)) return;

        FarmingHoeConfiguration farmingHoeConfiguration = getSpecialConfiguration(itemStack);
        int range = farmingHoeConfiguration.size() / 2;

        var allowedBlock = farmingHoeConfiguration.allowedCrops();
        if (!allowedBlock.contains(farmBlock.getType())) return;

        event.setCancelled(true);
        boolean needToRemoveDamage = false;

        ElapsedTime elapsedTime = new ElapsedTime("Hoe");
        elapsedTime.start();

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {

                var block = world.getBlockAt(farmBlock.getX() + x, farmBlock.getY(), farmBlock.getZ() + z);

                if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {

                    if (!allowedBlock.contains(block.getType())) continue;

                    if (!this.plugin.hasAccess(player, block.getLocation())) continue;

                    if (farmingHoeConfiguration.eventBlockBreakEvent()) {
                        var blockEvent = new FarmingHoeBlockBreakEvent(block, player);
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

                    dropItem(player, drops, dropLocation, farmingHoeConfiguration);

                    if (farmingHoeConfiguration.autoReplant()) {
                        ageable.setAge(0);
                        block.setBlockData(ageable);
                        block.getState().update();
                    } else {
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        if (needToRemoveDamage && farmingHoeConfiguration.damage() >= 1) {
            itemStack.damage(farmingHoeConfiguration.damage(), player);
        }

        elapsedTime.endDisplay();
    }

    /**
     * Handles the PlayerInteractEvent to implement the special behavior of the Farming Hoe when interacting with blocks.
     *
     * @param event The PlayerInteractEvent triggered by interacting with a block.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {

        if (event.useInteractedBlock() == Event.Result.DENY || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var player = event.getPlayer();
        var itemStack = event.getItem();

        if (itemStack == null || !isSpecialItem(itemStack)) return;

        var world = player.getWorld();
        var block = event.getClickedBlock();
        if (block == null) return;

        FarmingHoeConfiguration farmingHoeConfiguration = getSpecialConfiguration(itemStack);

        if (!canBecomeSoil(block)) {
            if (farmingHoeConfiguration.plantSeeds()) {
                plantSeeds(block, farmingHoeConfiguration, world, event, player);
            }
            return;
        }

        if (!farmingHoeConfiguration.harvest()) return;

        int range = farmingHoeConfiguration.size() / 2;
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

        if (needToRemoveDamage && farmingHoeConfiguration.harvestDamage() >= 1) {
            itemStack.damage(farmingHoeConfiguration.harvestDamage(), player);
        }
    }

    /**
     * Plants seeds in a specified area around a given block.
     *
     * @param block                   The block to plant seeds around.
     * @param farmingHoeConfiguration The configuration for the farming hoe.
     * @param world                   The world in which the event occurs.
     * @param event                   The player interact event.
     * @param player                  The player planting the seeds.
     */
    private void plantSeeds(Block block, FarmingHoeConfiguration farmingHoeConfiguration, World world, PlayerInteractEvent event, Player player) {
        int range = farmingHoeConfiguration.size() / 2;
        Material baseBlockType = block.getType();

        if (baseBlockType == Material.FARMLAND || baseBlockType == Material.SOUL_SAND) {
            event.setCancelled(true);
            Set<Material> allowedSeeds = new HashSet<>(farmingHoeConfiguration.allowedPlantSeeds());

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
     * @param farmingHoeConfiguration The configuration for the farming hoe.
     */
    private void dropItem(Player player, Collection<ItemStack> drops, Location location, FarmingHoeConfiguration farmingHoeConfiguration) {
        World world = location.getWorld();
        drops.removeIf(dropItemStack -> farmingHoeConfiguration.blacklistMaterials().contains(dropItemStack.getType()));

        if (farmingHoeConfiguration.dropItemInInventory()) {

            var inventory = player.getInventory();

            var result = inventory.addItem(drops.toArray(new ItemStack[0]));
            if (result.isEmpty()) return;

            result.values().forEach(itemStackDrop -> world.dropItemNaturally(location, itemStackDrop));

        } else drops.forEach(itemStackDrop -> world.dropItemNaturally(location, itemStackDrop));
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
}
