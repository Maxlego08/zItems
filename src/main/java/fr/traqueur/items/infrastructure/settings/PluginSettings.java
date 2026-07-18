package fr.traqueur.items.infrastructure.settings;

import fr.traqueur.items.api.settings.Settings;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record PluginSettings(
        boolean debug,
        @Options(optional = true) List<String> blockBreakEventPlugins,
        @Options(optional = true) @DefaultInt(-1) int defaultNbEffectsView,
        StripLogs stripLogs
) implements Settings {

    public record StripLog(String from, String to) implements Loadable {
    }

    public record StripLogs(
            @Options(optional = true) @DefaultInt(1) int damage,
            @Options(optional = true) List<Material> materials,
            @Options(optional = true) List<Tag<Material>> tags,
            List<StripLog> strips
    ) implements Loadable {

        public boolean matchesTool(ItemStack itemStack) {
            if (materials != null && materials.contains(itemStack.getType())) {
                return true;
            }

            if (tags != null && tags.stream().anyMatch(tag -> tag.isTagged(itemStack.getType()))) {
                return true;
            }

            return false;
        }
    }

}
