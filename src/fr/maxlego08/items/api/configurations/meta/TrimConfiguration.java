package fr.maxlego08.items.api.configurations.meta;

import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public record TrimConfiguration(boolean enable, TrimMaterial material, TrimPattern pattern) {
}
