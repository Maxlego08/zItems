package fr.maxlego08.items.api.configurations.state;

import fr.maxlego08.items.api.Item;
import org.bukkit.entity.Player;

public interface ItemSlot {

        int slot();

        Item item(Player player);

        int amount();

    }