package fr.traqueur.items.effects.economy;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.annotations.IncompatibleWith;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.shop.ShopProvider;
import fr.traqueur.items.effects.utility.Absorption;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AutoEffect(value = "AUTO_SELL")
@IncompatibleWith(Absorption.class)
public record AutoSell(ItemsPlugin plugin) implements EffectHandler.MultiEventEffectHandler<AutoSellSettings> {

    @Override
    public Set<Class<? extends Event>> eventTypes() {
        return Set.of(BlockBreakEvent.class, EntityDeathEvent.class);
    }

    @Override
    public void handle(EffectContext context, AutoSellSettings settings) {
        Player player = context.executor();

        List<ItemStack> drops = new ArrayList<>(context.drops());
        switch (context.event()) {
            case BlockBreakEvent blockBreakEvent -> {
                if(drops.isEmpty()) {
                    drops.addAll(blockBreakEvent.getBlock().getDrops(context.itemSource()));
                }
            }
            case EntityDeathEvent entityDeathEvent -> {
               if (drops.isEmpty()) {
                   drops.addAll(entityDeathEvent.getDrops());
               }
            }
            default -> { return; }
        }

        // Récupérer le ShopProvider
        ShopProvider provider;
        try {
            provider = ShopProvider.get();
        } catch (IllegalStateException e) {
            return; // Pas de provider disponible
        }

        // Liste des drops à garder (ceux qui n'ont pas été vendus)
        List<ItemStack> remainingDrops = new ArrayList<>();

        // Vendre chaque drop
        for (ItemStack drop : drops) {
            if (drop == null || drop.getType().isAir()) {
                continue;
            }

            boolean sold = provider.sell(plugin, drop, drop.getAmount(), settings.multiplier(), player);

            // Si l'item n'a pas été vendu, on le garde
            if (!sold) {
                remainingDrops.add(drop);
            }
        }

        // Remplacer la liste des drops par ceux qui n'ont pas été vendus
        context.drops().clear();
        context.drops().addAll(remainingDrops);
    }

    @Override
    public int priority() {
        return -1;
    }
}