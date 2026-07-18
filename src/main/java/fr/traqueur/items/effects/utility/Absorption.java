package fr.traqueur.items.effects.utility;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.annotations.IncompatibleWith;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.effects.economy.AutoSell;
import fr.traqueur.items.effects.engine.EmptySettings;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AutoEffect(value = "ABSORPTION")
@IncompatibleWith(AutoSell.class)
public class Absorption implements EffectHandler.MultiEventEffectHandler<EmptySettings> {

    @Override
    public Set<Class<? extends Event>> eventTypes() {
        return Set.of(BlockBreakEvent.class, BlockDropItemEvent.class, EntityDropItemEvent.class);
    }

    @Override
    public void handle(EffectContext context, EmptySettings settings) {
        if (context.event() instanceof BlockBreakEvent) {
            List<ItemStack> items = new ArrayList<>(context.drops());
            var notAdd = context.executor().getInventory().addItem(items.toArray(ItemStack[]::new));
            context.drops().clear();
            context.addDrops(notAdd.values());
        } else if (context.event() instanceof BlockDropItemEvent event) {
            List<Item> items = new ArrayList<>(event.getItems());
            var notAdd = context.executor().getInventory().addItem(items.stream().map(Item::getItemStack).toArray(ItemStack[]::new));
            event.getItems().clear();
            for (ItemStack value : notAdd.values()) {
                Item item = event.getBlock().getWorld().spawn(event.getBlock().getLocation(), Item.class);
                item.setItemStack(value);
                event.getItems().add(item);
            }
        } else if (context.event() instanceof EntityDropItemEvent event) {
            var notAdd = context.executor().getInventory().addItem(event.getItemDrop().getItemStack());
            for (ItemStack value : notAdd.values()) {
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), value);
            }
        }

    }

    @Override
    public int priority() {
        return -1;
    }
}
