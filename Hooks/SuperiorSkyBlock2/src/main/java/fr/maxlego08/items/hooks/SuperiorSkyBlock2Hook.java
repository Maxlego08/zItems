package fr.maxlego08.items.hooks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import fr.maxlego08.items.api.hook.BlockAccess;
import org.bukkit.entity.Player;

public class SuperiorSkyBlock2Hook implements BlockAccess {
    @Override
    public boolean hasAccess(Player player, org.bukkit.Location location) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        if (superiorPlayer == null) {
            return false;
        }
        return SuperiorSkyblockAPI.getIslandAt(location).hasPermission(superiorPlayer, IslandPrivilege.getByName("BREAK"));
    }
}
