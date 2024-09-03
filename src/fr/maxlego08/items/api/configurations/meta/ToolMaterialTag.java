package fr.maxlego08.items.api.configurations.meta;

import org.bukkit.Material;

import java.util.List;

public record ToolMaterialTag(List<Material> materials, float speed, boolean correctForDrops) {
}
