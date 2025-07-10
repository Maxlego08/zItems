package fr.maxlego08.items.api.configurations.global;

import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

public record StripLogConfiguration(boolean enable, int damage, List<Tag<Material>> tags, List<Strips> strips) {

}
