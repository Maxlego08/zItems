package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class RuneHammerConfiguration extends RuneConfiguration {

    private final int height;
    private final int width;
    private final int depth;
    private final boolean eventBlockBreakEvent;
    private final int damage;
    private final boolean isMaxDamage;

    public RuneHammerConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);

        this.height = configuration.getInt("height");
        this.width = configuration.getInt("width");
        this.depth = configuration.getInt("depth");
        this.eventBlockBreakEvent = configuration.getBoolean("enable-block-break-event");
        String damage = configuration.getString("damage");
        this.isMaxDamage = damage != null && damage.equalsIgnoreCase("max");
        this.damage = configuration.getInt("damage", 0);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isEventBlockBreakEvent() {
        return eventBlockBreakEvent;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isMaxDamage() {
        return isMaxDamage;
    }
}
