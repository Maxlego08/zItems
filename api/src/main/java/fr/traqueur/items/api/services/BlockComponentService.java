package fr.traqueur.items.api.services;

import net.kyori.adventure.text.Component;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.EnchantingTable;
import org.bukkit.block.sign.SignSide;

/**
 * Service for applying Adventure Component data to block state types using Paper's native API.
 * <p>
 * Loaded via {@link java.util.ServiceLoader} — the {@code versions/paper} module provides
 * the implementation. When absent (Spigot), callers fall back to legacy string serialization.
 */
public interface BlockComponentService {

    void setName(CommandBlock commandBlock, Component name);

    void setCustomName(EnchantingTable enchantingTable, Component customName);

    void setLine(SignSide signSide, int index, Component line);
}
