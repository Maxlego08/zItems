package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneXPBoostConfiguration;
import fr.maxlego08.items.api.runes.handlers.BreakHandler;
import fr.maxlego08.items.api.runes.handlers.EntityDeathHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class XPBoost implements EntityDeathHandler<RuneXPBoostConfiguration>, BreakHandler<RuneXPBoostConfiguration>, RuneActivator {

    @Override
    public void onEntityDeath(ItemPlugin plugin, EntityDeathEvent event, RuneXPBoostConfiguration runeConfiguration) {
        int exp = event.getDroppedExp();
        event.setDroppedExp((int) (exp * runeConfiguration.getXpBoost()));
    }

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, RuneXPBoostConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {

        // There is only one rune on the item, so we will modify the event
        if (origin.size() == 1) {
            event.setExpToDrop((int) (event.getExpToDrop() * runeConfiguration.getXpBoost()));
            return new HashSet<>();
        }

        int totalExp = origin.stream().mapToInt(this::getBlockXP).sum();
        var currentBlock = event.getBlock();
        currentBlock.getWorld().spawn(currentBlock.getLocation(), ExperienceOrb.class, orb -> orb.setExperience(totalExp));

        return origin;
    }

    @Override
    public int getPriority() {
        return -1;
    }

    public int getBlockXP(Block block) {
        Material type = block.getType();
        return switch (type) {
            case COAL_ORE, NETHER_GOLD_ORE -> randomInt(0, 2);
            case LAPIS_ORE -> randomInt(2, 5);
            case DIAMOND_ORE, EMERALD_ORE -> randomInt(3, 7);
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> randomInt(1, 5);
            case ANCIENT_DEBRIS -> randomInt(2, 6);
            case SPAWNER -> randomInt(15, 42);
            default -> 0;
        };
    }

    private int randomInt(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

}
