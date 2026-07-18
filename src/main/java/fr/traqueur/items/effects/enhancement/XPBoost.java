package fr.traqueur.items.effects.enhancement;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.effects.settings.BoostSettings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.concurrent.ThreadLocalRandom;

@AutoEffect(value = "XP_BOOST")
public class XPBoost implements EffectHandler.SingleEventEffectHandler<BoostSettings, BlockBreakEvent> {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Override
    public void handle(EffectContext context, BoostSettings settings) {
        BlockBreakEvent event = context.getEventAs(this.eventType());

        if (settings.chanceToBoost() != -1 && ThreadLocalRandom.current().nextDouble(0, 100) > settings.chanceToBoost()) {
            return;
        }

        if (context.affectedBlocks().size() != 1) {
            int totalExp = context.affectedBlocks().stream().mapToInt(this::getBlockXP).sum();
            var currentBlock = event.getBlock();
            currentBlock.getWorld().spawn(currentBlock.getLocation(), ExperienceOrb.class, orb -> orb.setExperience(totalExp));
            return;
        }

        event.setExpToDrop((int) (settings.boost() * event.getExpToDrop()));

    }

    private int getBlockXP(Block block) {
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
        return RANDOM.nextInt(minInclusive, maxInclusive + 1);
    }

    @Override
    public int priority() {
        return 0;
    }
}
