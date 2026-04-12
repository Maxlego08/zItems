package fr.traqueur.items.items.blockstate;

import com.destroystokyo.paper.profile.PlayerProfile;
import fr.traqueur.items.api.annotations.AutoBlockStateMeta;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.items.BlockStateMeta;
import fr.traqueur.structura.annotations.Options;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.util.UUID;

@AutoBlockStateMeta("skull")
@PaperOnly
public record SkullStateMetaPaper(
        @Options(optional = true) String playerName,
        @Options(optional = true) UUID playerUuid,
        @Options(optional = true) String texture
) implements BlockStateMeta<Skull> {

    public SkullStateMetaPaper {
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
        if (texture != null && !texture.isEmpty()) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.getProperties().add(new com.destroystokyo.paper.profile.ProfileProperty(
                    "textures",
                    texture
            ));
            skull.setPlayerProfile(profile);
        } else if (playerUuid != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
            skull.setOwningPlayer(player);
        } else if (playerName != null && !playerName.isEmpty()) {
            @SuppressWarnings("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
            skull.setOwningPlayer(player);
        }
    }
}