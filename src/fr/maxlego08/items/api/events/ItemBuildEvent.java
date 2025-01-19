package fr.maxlego08.items.api.events;

import fr.maxlego08.items.api.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemBuildEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private final Item item;
    private ItemStack itemStack;
    private boolean parsePlaceholders;

    public ItemBuildEvent(Player who, Item source, ItemStack itemStack, boolean parsePlaceholders) {
        super(who);
        this.item = source;
        this.itemStack = itemStack;
        this.parsePlaceholders = parsePlaceholders;
    }

    public void setParsePlaceholders(boolean parsePlaceholders) {
        this.parsePlaceholders = parsePlaceholders;
    }

    public boolean isParsePlaceholders() {
        return parsePlaceholders;
    }

    public Item getSource() {
        return item;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
