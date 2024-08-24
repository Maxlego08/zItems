package fr.maxlego08.items;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.configurations.ItemConfiguration;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.utils.ZUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ZItemManager extends ZUtils implements ItemManager {

    private final ItemsPlugin plugin;
    private final List<Item> items = new ArrayList<>();

    public ZItemManager(ItemsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadItems() {
        File folder = new File(plugin.getDataFolder(), "items");
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                this.plugin.saveResource("items/example.yml", false);
                this.plugin.saveResource("items/armor-trim.yml", false);
                this.plugin.saveResource("items/custom_seed.yml", false);
                this.plugin.saveResource("items/empty_item.yml", false);
                this.plugin.saveResource("items/food.yml", false);
                this.plugin.saveResource("items/glass-breaker.yml", false);
                this.plugin.saveResource("items/loot-chest.yml", false);
                this.plugin.saveResource("items/multitools.yml", false);
                this.plugin.saveResource("items/strength-potion.yml", false);
                this.plugin.saveResource("items/hoe.yml", false);
                this.plugin.saveResource("items/hammer.yml", false);
            }
        }

        this.deleteCrafts();

        this.items.clear();

        try (Stream<Path> stream = Files.walk(folder.toPath())) {
            stream.skip(1).map(Path::toFile).filter(File::isFile).filter(e -> e.getName().endsWith(".yml")).forEach(this::loadItem);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Must create a recipe after all registrations to get custom items from the List when ingredient is custom
        this.items.forEach(item -> item.getConfiguration().createRecipe(item, this.plugin));
    }

    @Override
    public void loadItem(File file) {

        try {

            String itemName = file.getName().replace(".yml", "");
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            ItemConfiguration itemConfiguration = new ItemConfiguration(plugin, configuration, file.getPath(), "");
            Item item = new ZItem(this.plugin, itemName, itemConfiguration);

            this.items.add(item);

            plugin.getLogger().info("Loaded item " + file.getPath());

        } catch (Exception exception) {
            plugin.getLogger().severe("Impossible to load the item " + file.getPath());
            exception.printStackTrace();
        }
    }

    @Override
    public List<Item> getItems() {
        return this.items;
    }

    @Override
    public Optional<Item> getItem(String name) {
        return this.items.stream().filter(item -> item.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public List<String> getItemNames() {
        return this.items.stream().map(Item::getName).toList();
    }

    @Override
    public void giveItem(CommandSender sender, Player player, String itemName, int amount) {

        var optional = this.getItem(itemName);
        if (optional.isEmpty()) {
            message(sender, Message.ITEM_NOT_FOUND, "%name%", itemName);
            return;
        }

        var item = optional.get();
        ItemStack itemStack = item.build(player, amount);
        give(player, itemStack);

        message(sender, Message.ITEM_GIVE, "%name%", itemName, "%player%", player.getName());
    }

    @Override
    public void deleteCrafts() {
        this.items.forEach(item -> item.getConfiguration().deleteRecipe(item, this.plugin));
    }
}