package fr.traqueur.items.effects;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.effects.events.PlayerTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

/**
 * Periodically dispatches a synthetic {@link PlayerTickEvent} for every online
 * player's main-hand item, so entries like {@code SNEAKING} can gate a pipeline on
 * an ongoing player state rather than a discrete Bukkit event.
 * <p>
 * Started once in {@code ZItems#onEnable} and cancelled in {@code onDisable}. Cheap
 * to run even for players holding nothing pipeline-relevant: {@code dispatch} bails
 * out immediately when the item carries no effects at all.
 */
public class TickDispatcher {

    /** Every 10 ticks (0.5s): responsive enough for a visual effect without dispatching on every tick. */
    private static final long PERIOD_TICKS = 10L;

    private final ItemsPlugin plugin;
    private BukkitTask task;

    public TickDispatcher(ItemsPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, PERIOD_TICKS, PERIOD_TICKS);
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().isAir()) {
                continue;
            }
            plugin.getDispatcher().dispatch(player, item, new PlayerTickEvent(player));
        }
    }
}
