package fr.traqueur.items.effects.farming;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.managers.DurabilityManager;
import fr.traqueur.items.effects.farming.FarmingHoeSettings;
import fr.traqueur.items.utils.EventUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AutoEffect(value = "FARMING_HOE")
public class FarmingHoe implements EffectHandler.MultiEventEffectHandler<FarmingHoeSettings> {

    private static final Map<Material, Material> SEED_TO_CROP = Map.of(
            Material.WHEAT_SEEDS, Material.WHEAT,
            Material.CARROT, Material.CARROTS,
            Material.POTATO, Material.POTATOES,
            Material.BEETROOT_SEEDS, Material.BEETROOTS,
            Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM,
            Material.MELON_SEEDS, Material.MELON_STEM,
            Material.NETHER_WART, Material.NETHER_WART
    );

    @Override
    public Set<Class<? extends Event>> eventTypes() {
        return Set.of(BlockBreakEvent.class, PlayerInteractEvent.class);
    }

    @Override
    public void handle(EffectContext context, FarmingHoeSettings settings) {
        // Valider la taille
        if (!settings.isValidSize()) {
            Logger.warning("Farming hoe size must be odd! Current size: " + settings.size());
            return;
        }

        Event event = context.event();
        if (event instanceof BlockBreakEvent breakEvent) {
            handleHarvest(context, breakEvent, settings);
        } else if (event instanceof PlayerInteractEvent interactEvent) {
            handleInteraction(context, interactEvent, settings);
        }
    }

    /**
     * Handles crop harvesting when breaking blocks
     */
    private void handleHarvest(EffectContext context, BlockBreakEvent event, FarmingHoeSettings settings) {
        Block originBlock = event.getBlock();
        Player player = event.getPlayer();
        World world = player.getWorld();

        // Vérifier que c'est une plante mature
        if (!(originBlock.getBlockData() instanceof Ageable ageable)) {
            return;
        }

        if (ageable.getAge() != ageable.getMaximumAge()) {
            return;
        }

        // Vérifier si la plante est autorisée
        if (settings.allowedCrops() != null && !settings.allowedCrops().isEmpty()) {
            if (!settings.allowedCrops().contains(originBlock.getType())) {
                return;
            }
        }

        event.setCancelled(true);
        int range = settings.range();
        boolean damaged = false;

        // Récolter dans la zone
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                Block block = world.getBlockAt(
                        originBlock.getX() + x,
                        originBlock.getY(),
                        originBlock.getZ() + z
                );

                if (harvestBlock(context, block, originBlock, settings, player)) {
                    damaged = true;
                }
            }
        }

        // Appliquer les dégâts si nécessaire
        if (damaged && settings.harvestDamage() > 0) {
            JavaPlugin.getPlugin(ItemsPlugin.class).getManager(DurabilityManager.class).applyDamage(context.itemSource(), settings.harvestDamage(), player);
        }
    }

    /**
     * Harvests a single block if it's a mature crop
     */
    private boolean harvestBlock(EffectContext context, Block block, Block originBlock, FarmingHoeSettings settings, Player player) {
        if (!(block.getBlockData() instanceof Ageable ageable)) {
            return false;
        }

        if (ageable.getAge() != ageable.getMaximumAge()) {
            return false;
        }

        // Check if player has permission to break block at this location
        if (!EventUtil.canBreakBlock(player, block.getLocation())) {
            return false;
        }

        // Vérifier si autorisé
        if (settings.allowedCrops() != null && !settings.allowedCrops().isEmpty()) {
            if (!settings.allowedCrops().contains(block.getType())) {
                return false;
            }
        }

        // Obtenir les drops
        Collection<ItemStack> drops = block.getDrops(context.itemSource(), player);

        // Appliquer la blacklist
        if (settings.dropBlacklist() != null && !settings.dropBlacklist().isEmpty()) {
            drops.removeIf(drop -> settings.dropBlacklist().contains(drop.getType()));
        }

        // Gérer les drops
        if (settings.dropInInventory()) {
            // Ajouter dans l'inventaire
            Map<Integer, ItemStack> overflow = player.getInventory().addItem(drops.toArray(new ItemStack[0]));
            // Les items qui ne rentrent pas sont ajoutés aux drops normaux
            context.addDrops(overflow.values());
        } else {
            // Déterminer la location des drops
            Location dropLoc = getDropLocation(block.getLocation(), originBlock.getLocation(), player.getLocation(), settings);

            // Faire apparaître les drops
            for (ItemStack drop : drops) {
                block.getWorld().dropItemNaturally(dropLoc, drop);
            }
        }

        // Auto-replant ou casser le bloc
        if (settings.autoReplant()) {
            ageable.setAge(0);
            block.setBlockData(ageable);
        } else {
            block.setType(Material.AIR);
        }

        return true;
    }

    /**
     * Handles right-click interactions for tilling and planting
     */
    private void handleInteraction(EffectContext context, PlayerInteractEvent event, FarmingHoeSettings settings) {
        // Vérifier que c'est un clic droit sur un bloc
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        Player player = event.getPlayer();
        World world = player.getWorld();

        // Labour de la terre
        if (canBecomeFarmland(clickedBlock) && settings.harvest()) {
            handleTilling(event, clickedBlock, world, settings, context.itemSource(), player);
        }
        // Plantation de graines
        else if (settings.plantSeeds() && (clickedBlock.getType() == Material.FARMLAND || clickedBlock.getType() == Material.SOUL_SAND)) {
            handlePlanting(event, clickedBlock, world, settings, player);
        }
    }

    /**
     * Handles tilling dirt into farmland
     */
    private void handleTilling(PlayerInteractEvent event, Block block, World world, FarmingHoeSettings settings, ItemStack tool, Player player) {
        event.setCancelled(true);
        int range = settings.range();
        boolean damaged = false;

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                Block targetBlock = world.getBlockAt(
                        block.getX() + x,
                        block.getY(),
                        block.getZ() + z
                );

                // Check if player has permission to modify block at this location
                if (canBecomeFarmland(targetBlock) && EventUtil.canBreakBlock(player, targetBlock.getLocation())) {
                    targetBlock.setType(Material.FARMLAND);
                    damaged = true;
                }
            }
        }

        if (damaged && settings.tillDamage() > 0) {
            JavaPlugin.getPlugin(ItemsPlugin.class).getManager(DurabilityManager.class).applyDamage(tool, settings.tillDamage(), player);
        }
    }

    /**
     * Handles planting seeds in an area
     */
    private void handlePlanting(PlayerInteractEvent event, Block block, World world, FarmingHoeSettings settings, Player player) {
        Material baseBlockType = block.getType();
        event.setCancelled(true);
        int range = settings.range();

        Set<Material> allowedSeeds = settings.allowedSeeds() != null && !settings.allowedSeeds().isEmpty()
                ? new HashSet<>(settings.allowedSeeds())
                : SEED_TO_CROP.keySet();

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                Block targetBlock = world.getBlockAt(
                        block.getX() + x,
                        block.getY(),
                        block.getZ() + z
                );

                // Vérifier que le bloc de base est le bon type et que le dessus est vide
                if (targetBlock.getType() == baseBlockType && targetBlock.getRelative(BlockFace.UP).getType().isAir()) {
                    plantSeed(targetBlock, baseBlockType, allowedSeeds, player);
                }
            }
        }
    }

    /**
     * Plants a seed on a block if the player has it
     */
    private void plantSeed(Block block, Material baseBlockType, Set<Material> allowedSeeds, Player player) {
        for (Material seedMaterial : allowedSeeds) {
            // Vérifier compatibilité (Nether Wart sur Soul Sand, autres sur Farmland)
            if (baseBlockType == Material.SOUL_SAND && seedMaterial != Material.NETHER_WART) {
                continue;
            }
            if (baseBlockType == Material.FARMLAND && seedMaterial == Material.NETHER_WART) {
                continue;
            }

            // Vérifier si le joueur a la graine
            ItemStack seed = new ItemStack(seedMaterial, 1);
            if (player.getInventory().containsAtLeast(seed, 1)) {
                Material cropType = SEED_TO_CROP.get(seedMaterial);
                if (cropType != null && !cropType.isAir()) {
                    block.getRelative(BlockFace.UP).setType(cropType);
                    player.getInventory().removeItem(seed);
                    break;
                }
            }
        }
    }

    /**
     * Determines the drop location based on settings
     */
    private Location getDropLocation(Location blockLoc, Location centerLoc, Location playerLoc, FarmingHoeSettings settings) {
        if (settings.dropLocation() == null) {
            return blockLoc;
        }

        return switch (settings.dropLocation()) {
            case CENTER -> centerLoc;
            case PLAYER -> playerLoc;
            default -> blockLoc;
        };
    }

    /**
     * Checks if a block can be turned into farmland
     */
    private boolean canBecomeFarmland(Block block) {
        if (block == null) return false;
        Material type = block.getType();
        return type == Material.DIRT ||
                type == Material.GRASS_BLOCK ||
                type == Material.PODZOL ||
                type == Material.MYCELIUM ||
                type == Material.ROOTED_DIRT ||
                type == Material.DIRT_PATH;
    }

    @Override
    public int priority() {
        return 0;
    }
}