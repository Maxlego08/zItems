package fr.maxlego08.items.api.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BlockAccess {

    boolean hasAccess(Player player, Location location);

}
