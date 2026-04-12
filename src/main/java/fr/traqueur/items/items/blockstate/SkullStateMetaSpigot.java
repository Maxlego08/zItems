package fr.traqueur.items.items.blockstate;

import fr.traqueur.items.api.annotations.AutoBlockStateMeta;
import fr.traqueur.items.api.annotations.SpigotOnly;
import fr.traqueur.items.api.items.BlockStateMeta;
import fr.traqueur.structura.annotations.Options;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.util.UUID;

@AutoBlockStateMeta("skull")
@SpigotOnly
public record SkullStateMetaSpigot(
        @Options(optional = true) String playerName,
        @Options(optional = true) UUID playerUuid
) implements BlockStateMeta<Skull> {

    public SkullStateMetaSpigot {
        if ((playerName == null && playerUuid == null)) {
            throw new IllegalArgumentException("At least one of playerName or playerUuid must be set.");
        }
    }

    @Override
    public void apply(Player __, Skull skull) {
        if (playerUuid != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
            skull.setOwningPlayer(player);
        } else if (playerName != null && !playerName.isEmpty()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            skull.setOwningPlayer(player);
        }
    }
}