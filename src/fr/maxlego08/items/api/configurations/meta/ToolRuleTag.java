package fr.maxlego08.items.api.configurations.meta;

import org.bukkit.Material;
import org.bukkit.Tag;

public record ToolRuleTag(Tag<Material> tag, float speed, boolean correctForDrops) {
}
