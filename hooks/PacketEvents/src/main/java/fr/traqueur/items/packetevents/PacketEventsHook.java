package fr.traqueur.items.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoHook;
import fr.traqueur.items.api.hooks.Hook;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;

@AutoHook("packetevents")
public class PacketEventsHook implements Hook {

    @Override
    public void onLoad() {
        ItemsPlugin plugin = JavaPlugin.getPlugin(ItemsPlugin.class);
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().getSettings()
                .bStats(false)
                .checkForUpdates(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new DurabilityPacketListener());
        Logger.info("PacketEvents hook enabled - dynamic durability lore active.");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}