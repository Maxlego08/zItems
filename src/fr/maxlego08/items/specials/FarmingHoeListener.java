package fr.maxlego08.items.specials;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.configurations.specials.FarmingHoeConfiguration;
import fr.maxlego08.items.api.events.FarmingHoeBlockBreakEvent;
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

public class FarmingHoeListener extends SpecialHelper<FarmingHoeConfiguration> implements Listener {

    public FarmingHoeListener(ItemsPlugin plugin) {
        super(plugin, ItemType.FARMING_HOE);
    }

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

        for (int x = -range; x < range + 1; x++) {
            for (int z = -range; z < range + 1; z++) {

                var block = world.getBlockAt(farmBlock.getX() + x, farmBlock.getY(), farmBlock.getZ() + z);

                if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {

                    if (!allowedBlock.contains(block.getType())) continue;

                    var blockEvent = new FarmingHoeBlockBreakEvent(block, player);
                    blockEvent.callEvent();

                    if (blockEvent.isCancelled()) continue;

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

        if (needToRemoveDamage && farmingHoeConfiguration.damage() >= 1)
            itemStack.damage(farmingHoeConfiguration.damage(), player);
    }

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
        for (int x = -range; x < range + 1; x++) {
            for (int z = -range; z < range + 1; z++) {

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

    private void plantSeeds(Block block, FarmingHoeConfiguration farmingHoeConfiguration, World world, PlayerInteractEvent event, Player player) {

        int range = farmingHoeConfiguration.size() / 2;

        if (block.getType() == Material.FARMLAND) {

            event.setCancelled(true);

            for (int x = -range; x < range + 1; x++) {
                for (int z = -range; z < range + 1; z++) {

                    var currentBlock = world.getBlockAt(block.getX() + x, block.getY(), block.getZ() + z);
                    if (currentBlock.getType() == Material.FARMLAND && currentBlock.getRelative(BlockFace.UP).getType().isAir()) {
                        for (Material seedMaterial : farmingHoeConfiguration.allowedPlantSeeds()) {
                            if (seedMaterial != Material.NETHER_WART) {
                                var material = getCropTypeFromSeed(seedMaterial);
                                if (!material.isAir()) {
                                    ItemStack seed = new ItemStack(seedMaterial, 1);
                                    if (player.getInventory().containsAtLeast(seed, 1)) {
                                        currentBlock.getRelative(BlockFace.UP).setType(material);
                                        player.getInventory().removeItem(seed);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else if (block.getType() == Material.SOUL_SAND) {

            event.setCancelled(true);

            for (int x = -range; x < range + 1; x++) {
                for (int z = -range; z < range + 1; z++) {
                    var currentBlock = world.getBlockAt(block.getX() + x, block.getY(), block.getZ() + z);
                    if (currentBlock.getType() == Material.SOUL_SAND && currentBlock.getRelative(BlockFace.UP).getType().isAir()) {
                        for (Material seedMaterial : farmingHoeConfiguration.allowedPlantSeeds()) {
                            if (seedMaterial == Material.NETHER_WART) {
                                ItemStack seed = new ItemStack(seedMaterial, 1);
                                if (player.getInventory().containsAtLeast(seed, 1)) {
                                    currentBlock.getRelative(BlockFace.UP).setType(seedMaterial);
                                    player.getInventory().removeItem(seed);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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

    private boolean canBecomeSoil(Block block) {
        if (block == null) return false;

        Material type = block.getType();
        return type == Material.DIRT || type == Material.GRASS_BLOCK || type == Material.PODZOL || type == Material.MYCELIUM;
    }

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
