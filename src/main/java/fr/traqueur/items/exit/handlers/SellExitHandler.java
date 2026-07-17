package fr.traqueur.items.exit.handlers;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.annotations.AutoExit;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.exit.ExitHandler;
import fr.traqueur.items.api.shop.ShopProvider;
import fr.traqueur.items.exit.settings.SellExitSettings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Sells the pipeline's collected drops through the registered {@link ShopProvider}.
 * <p>
 * Mirrors {@link fr.traqueur.items.effects.handlers.AutoSell}'s logic, applied at the
 * end of a pipeline run instead of as a standalone low-priority effect handler.
 */
@AutoExit("SELL")
public record SellExitHandler(ItemsPlugin plugin) implements ExitHandler<SellExitSettings> {

    @Override
    public void resolve(EffectContext context, SellExitSettings settings) {
        Player player = context.executor();

        List<ItemStack> drops = new ArrayList<>(context.drops());
        if (drops.isEmpty()) {
            switch (context.event()) {
                case BlockBreakEvent blockBreakEvent -> drops.addAll(blockBreakEvent.getBlock().getDrops(context.itemSource()));
                case EntityDeathEvent entityDeathEvent -> drops.addAll(entityDeathEvent.getDrops());
                default -> { return; }
            }
        }

        ShopProvider provider;
        try {
            provider = ShopProvider.get();
        } catch (IllegalStateException e) {
            return;
        }

        List<Material> materials = settings.materials();
        List<ItemStack> remainingDrops = new ArrayList<>();

        for (ItemStack drop : drops) {
            if (drop == null || drop.getType().isAir()) {
                continue;
            }

            boolean matchesFilter = materials == null || materials.isEmpty() || materials.contains(drop.getType());
            boolean sold = matchesFilter && provider.sell(plugin, drop, drop.getAmount(), settings.multiplier(), player);

            if (!sold) {
                remainingDrops.add(drop);
            }
        }

        context.drops().clear();
        context.drops().addAll(remainingDrops);
    }
}
