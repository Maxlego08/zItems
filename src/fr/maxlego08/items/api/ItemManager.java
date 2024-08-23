package fr.maxlego08.items.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface ItemManager {


    void loadItems();

    void loadItem(File file);

    List<Item> getItems();

    Optional<Item> getItem(String name);

    List<String> getItemNames();

    void giveItem(CommandSender sender, Player player, String itemName, int amount);

    void deleteCrafts();
}
