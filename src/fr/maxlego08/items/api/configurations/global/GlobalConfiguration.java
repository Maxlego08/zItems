package fr.maxlego08.items.api.configurations.global;

import fr.maxlego08.items.api.utils.TagRegistry;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class GlobalConfiguration {

    private final StripLogConfiguration stripLogConfiguration;

    public GlobalConfiguration(FileConfiguration configuration) {

        this.stripLogConfiguration = new StripLogConfiguration(configuration.getBoolean("strip-log.enable"), configuration.getInt("strip-log.damage"), configuration.getStringList("strip-log.tags").stream().map(TagRegistry::getTag).filter(Objects::nonNull).toList(), configuration.getMapList("strip-log.strips").stream().map(map -> new Strips((String) map.get("from"), (String) map.get("to"))).toList());
    }

    public StripLogConfiguration getStripLogConfiguration() {
        return stripLogConfiguration;
    }
}
