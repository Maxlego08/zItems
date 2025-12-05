package fr.traqueur.items.items.blockstate;

import com.destroystokyo.paper.profile.PlayerProfile;
import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.annotations.AutoBlockStateMeta;
import fr.traqueur.items.api.items.BlockStateMeta;
import fr.traqueur.structura.annotations.Options;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * BlockState configuration for skull/head blocks.
 * Allows setting the skull owner and texture.
 */
@AutoBlockStateMeta("skull")
public record SkullStateMeta(
        @Options(optional = true) String playerName,
        @Options(optional = true) UUID playerUuid,
        @Options(optional = true) String texture
) implements BlockStateMeta<Skull> {

    public SkullStateMeta {
        if ((playerName != null && !playerName.isEmpty()) &&
            (playerUuid != null) &&
            (texture != null && !texture.isEmpty())) {
            throw new IllegalArgumentException("Only one of playerName, playerUuid, or texture can be set.");
        }

        if ((playerName == null && playerUuid == null) &&
            (texture == null)) {
            throw new IllegalArgumentException("At least one of playerName, playerUuid, or texture must be set.");
        }

    }

    @Override
    public void apply(Player __, Skull skull) {
        if (texture != null && !texture.isEmpty() && PlatformType.isPaper()) {
            // Set custom texture via player profile
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.getProperties().add(new com.destroystokyo.paper.profile.ProfileProperty(
                    "textures",
                    texture
            ));
            skull.setPlayerProfile(profile);
        } else if (playerUuid != null) {
            // Set by UUID
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
            skull.setOwningPlayer(player);
        } else if (playerName != null && !playerName.isEmpty()) {
            // Set by name
            @SuppressWarnings("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            skull.setOwningPlayer(player);
        }
    }
}