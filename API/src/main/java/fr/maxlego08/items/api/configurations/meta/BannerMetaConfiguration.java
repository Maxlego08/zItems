package fr.maxlego08.items.api.configurations.meta;

import org.bukkit.block.banner.Pattern;

import java.util.List;

public record BannerMetaConfiguration(boolean enable, List<Pattern> patterns) {
}