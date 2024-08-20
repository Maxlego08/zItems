package fr.maxlego08.items.runes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneManager;
import fr.maxlego08.items.api.runes.RuneType;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneMeltMiningConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneVeinMiningConfiguration;
import fr.maxlego08.items.api.utils.TagRegistry;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.utils.ZUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ZRuneManager extends ZUtils implements RuneManager {

    private final ItemsPlugin plugin;
    private final List<Rune> runes = new ArrayList<>();
    private final NamespacedKey namespacedKey;
    private final RuneDataType runeDataType;

    public ZRuneManager(ItemsPlugin plugin) {
        this.plugin = plugin;
        this.namespacedKey = new NamespacedKey(plugin, "runes");
        this.runeDataType = new RuneDataType(this);
    }

    @Override
    public void loadRunes() {

        File folder = new File(plugin.getDataFolder(), "runes");
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                this.plugin.saveResource("runes/vein-mining.yml", false);
                this.plugin.saveResource("runes/melt-mining.yml", false);
            }
        }

        this.runes.clear();

        try (Stream<Path> stream = Files.walk(folder.toPath())) {
            stream.skip(1).map(Path::toFile).filter(File::isFile).filter(e -> e.getName().endsWith(".yml")).forEach(this::loadRune);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void loadRune(File file) {

        var logger = this.plugin.getLogger();

        try {

            String runeName = file.getName().replace(".yml", "");
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            RuneType runeType = RuneType.valueOf(configuration.getString("type", "ERROR").toUpperCase());
            String displayName = configuration.getString("display-name");
            List<Material> materials = configuration.getStringList("allowed-materials").stream().map(String::toUpperCase).map(Material::valueOf).toList();
            List<Tag<Material>> tags = configuration.getStringList("allowed-tags").stream().map(String::toUpperCase).map(TagRegistry::getTags).toList();

            RuneConfiguration runeConfiguration = switch (runeType) {
                case VEIN_MINING -> RuneVeinMiningConfiguration.loadConfiguration(configuration);
                case MELT_MINING -> RuneMeltMiningConfiguration.loadConfiguration(configuration);
            };

            Rune rune = new ZRune(runeName, displayName, runeType, materials, tags, runeConfiguration);

            this.runes.add(rune);

            logger.info("Loaded rune " + file.getPath());

        } catch (Exception exception) {
            logger.severe("Impossible to load the rune " + file.getPath());
            exception.printStackTrace();
        }
    }

    @Override
    public Optional<Rune> getRune(String name) {
        return this.runes.stream().filter(rune -> rune.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public List<Rune> getRunes() {
        return this.runes;
    }

    @Override
    public List<Rune> getRunes(RuneType runeType) {
        return this.runes.stream().filter(rune -> rune.getType() == runeType).toList();
    }

    @Override
    public void applyRune(Player player, String runeName) {

        var optional = getRune(runeName);
        if (optional.isEmpty()) {
            message(player, Message.COMMAND_RUNE_NOT_FOUND, "%name%", runeName);
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        Rune rune = optional.get();

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        List<Rune> runes = persistentDataContainer.getOrDefault(this.namespacedKey, this.runeDataType, new ArrayList<>());

        if (runes.contains(rune)) {
            message(player, Message.COMMAND_RUNE_ALREADY_APPLIED, "%rune%", rune.getDisplayName());
            return;
        }

        List<String> lore = itemMeta.hasLore() ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();

        if (runes.isEmpty()) {
            lore.addAll(generateRuneLore(rune));
        } else {
            lore.add(color(getMessage(Message.RUNE_LINE, "%rune%", rune.getDisplayName())));
        }

        itemMeta.setLore(lore);

        runes.add(rune);
        persistentDataContainer.set(this.namespacedKey, this.runeDataType, runes);

        itemStack.setItemMeta(itemMeta);
    }

    private List<String> generateRuneLore(Rune rune) {
        List<String> runeLore = Message.RUNE_LORE.getMessages();
        List<String> formattedLore = new ArrayList<>();

        runeLore.forEach(line -> formattedLore.add(color(line)));
        formattedLore.add(color(getMessage(Message.RUNE_LINE, "%name%", rune.getDisplayName())));

        return formattedLore;
    }

    @Override
    public NamespacedKey getKey() {
        return this.namespacedKey;
    }

    @Override
    public PersistentDataType<byte[], List<Rune>> getDataType() {
        return this.runeDataType;
    }
}
