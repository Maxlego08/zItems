package fr.maxlego08.items.runes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.configurations.recipes.ItemRecipe;
import fr.maxlego08.items.api.configurations.recipes.RecipeType;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneManager;
import fr.maxlego08.items.api.runes.RuneType;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import fr.maxlego08.items.api.runes.exceptions.*;
import fr.maxlego08.items.api.utils.Helper;
import fr.maxlego08.items.api.utils.TagRegistry;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.utils.ZUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class ZRuneManager extends ZUtils implements RuneManager {

    private final ItemsPlugin plugin;
    private final List<Rune> runes = new ArrayList<>();
    private final NamespacedKey namespacedKey;
    private final NamespacedKey runeNamespacedKey;
    private final PersistentDataType<String, Rune> runeDataType;
    private final Map<NamespacedKey, ItemRecipe> recipesUseRunes = new HashMap<>();


    public ZRuneManager(ItemsPlugin plugin) {
        this.plugin = plugin;
        this.namespacedKey = new NamespacedKey(plugin, "runes");
        this.runeNamespacedKey = new NamespacedKey(plugin, "rune-represent");
        this.runeDataType = new RuneDataType(this);
    }

    @Override
    public void loadRunes() {

        File folder = new File(plugin.getDataFolder(), "runes");
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                this.plugin.saveResource("runes/vein-mining.yml", false);
                this.plugin.saveResource("runes/melt-mining.yml", false);
                this.plugin.saveResource("runes/farming-hoe.yml", false);
                this.plugin.saveResource("runes/protection.yml", false);
                this.plugin.saveResource("runes/unbreakable.yml", false);
                this.plugin.saveResource("runes/hammer.yml", false);
                this.plugin.saveResource("runes/silk-spawner.yml", false);
                this.plugin.saveResource("runes/absorption.yml", false);
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
    public void loadCraftWithRunes() {
        this.plugin.getItemManager().getItems()
                .stream()
                .filter(item -> item.getConfiguration().getItemType() == ItemType.RUNE)
                .filter(item -> item.getConfiguration().getItemRuneConfiguration().enableCrafting())
                .forEach(this::createRecipeWithRuneItem);
    }

    @Override
    public void loadRune(File file) {

        var logger = this.plugin.getLogger();

        try {

            String runeName = file.getName().replace(".yml", "");
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

            RuneType runeType = RuneType.getRuneType(configuration.getString("type", "ERROR").toUpperCase()).orElseThrow();
            String displayName = configuration.getString("display-name");
            List<Material> materials = configuration.getStringList("allowed-materials").stream().map(String::toUpperCase).map(Material::valueOf).toList();
            List<Tag<Material>> tags = configuration.getStringList("allowed-tags").stream().map(String::toUpperCase).map(TagRegistry::getTag).filter(Objects::nonNull).toList();

            RuneConfiguration runeConfiguration = runeType.getConfiguration(plugin, configuration, runeName);

            Rune rune = new ZRune(runeName, displayName, runeType, materials, tags, runeConfiguration);

            this.runes.add(rune);

            logger.info("Loaded rune " + file.getPath());

        } catch (Exception exception) {
            logger.severe("Unable to load the rune " + file.getPath());
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
            message(player, Message.COMMAND_RUNE_NOT_FOUND, "%rune%", runeName);
            return;
        }

        var rune = optional.get();

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        try {
            this.applyRune(itemStack, rune);
        } catch (RuneException e) {
            switch (e) {
                case NoMetaException ignored -> message(player, Message.ITEM_HAVE_NOT_META);
                case ItemContainsAlreadyRuneException ignored -> message(player, Message.COMMAND_RUNE_ALREADY_APPLIED, "%rune%", rune.getDisplayName());
                case RuneNotAllowedException ignored -> message(player, Message.COMMAND_RUNE_NOT_ALLOWED, "%rune%", rune.getDisplayName());
                case RuneAppliedException ignored -> message(player, Message.COMMAND_RUNE_NOT_ALLOWED, "%rune%", rune.getDisplayName());
                default -> throw new IllegalStateException("Unexpected value: " + e);
            }
        }
    }

    @Override
    public void applyRune(ItemStack itemStack, Rune rune) throws RuneException {
        if (itemStack.isEmpty()) {
            throw new NoMetaException();
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

        if (persistentDataContainer.has(Item.ITEM_KEY, PersistentDataType.STRING)) {
            Optional<Item> itemOptional = plugin.getItemManager().getItem(persistentDataContainer.get(Item.ITEM_KEY, PersistentDataType.STRING));
            if (itemOptional.isPresent()) {
                Item item = itemOptional.get();
                if(item.getConfiguration().getDisableRunes().contains(rune)) {
                    throw new RuneNotAllowedException();
                }
            }
        }

        List<Rune> runes = persistentDataContainer.getOrDefault(this.namespacedKey, PersistentDataType.LIST.listTypeFrom(this.runeDataType), new ArrayList<>());
        runes = new ArrayList<>(runes);

        if (runes.contains(rune)) {
            throw new ItemContainsAlreadyRuneException();
        }

        if(runes.stream().anyMatch(r -> r.getType().getIncompatibles().contains(rune.getType()))) {
            throw new RuneNotAllowedException();
        }

        if(!rune.isAllowed(itemStack.getType())) {
           throw new RuneNotAllowedException();
        }

        try {
            rune.getType().getActivator().applyOnItems(plugin, itemMeta, rune.getConfiguration());
        } catch (Exception e) {
            throw new RuneAppliedException();
        }


        List<String> lore = itemMeta.hasLore() ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();

        if (runes.isEmpty()) {
            lore.addAll(generateRuneLore(rune));
        } else {
            lore.add(color(getMessage(Message.RUNE_LINE, "%rune%", rune.getDisplayName())));
        }

        itemMeta.setLore(lore);

        runes.add(rune);
        persistentDataContainer.set(this.namespacedKey, PersistentDataType.LIST.listTypeFrom(this.runeDataType), runes);

        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public NamespacedKey getKey() {
        return this.namespacedKey;
    }

    @Override
    public NamespacedKey getRuneRepresentKey() {
        return this.runeNamespacedKey;
    }

    @Override
    public PersistentDataType<String, Rune> getDataType() {
        return this.runeDataType;
    }

    @Override
    public void deleteCrafts() {
        for (NamespacedKey key : this.recipesUseRunes.keySet()) {
            this.plugin.getServer().removeRecipe(key);
        }
    }

    @Override
    public Map<NamespacedKey, ItemRecipe> getRecipesUseRunes() {
        return recipesUseRunes;
    }

    private List<String> generateRuneLore(Rune rune) {
        List<String> runeLore = Message.RUNE_LORE.getMessages();
        List<String> formattedLore = new ArrayList<>();

        runeLore.forEach(line -> formattedLore.add(color(line)));
        formattedLore.add(color(getMessage(Message.RUNE_LINE, "%rune%", rune.getDisplayName())));

        return formattedLore;
    }


    private void createRecipeWithRuneItem(Item runeItem) {
        Rune rune = runeItem.getConfiguration().getItemRuneConfiguration().rune();
        String template = runeItem.getConfiguration().getItemRuneConfiguration().template();
        Set<Material> materials = new HashSet<>(rune.getMaterials());
        rune.getTags().forEach(tag -> materials.addAll(tag.getValues()));
        RecipeChoice addition = new RecipeChoice.MaterialChoice(runeItem.build(null, 1).getType());
        ItemRecipe.Ingredient[] ingredients = new ItemRecipe.Ingredient[3];
        ingredients[0] = new ItemRecipe.Ingredient(Helper.getRecipeChoiceFromString(this.plugin, "item|" + template), template, '-');
        ingredients[2] = new ItemRecipe.Ingredient(addition, "zitems:" + runeItem.getName(), '-');
        materials.forEach(material -> {
            ingredients[1] = new ItemRecipe.Ingredient(new RecipeChoice.MaterialChoice(material), "minecraft: " + material.name().toLowerCase(), '-');
            ItemStack result = new ItemStack(material);
            try {
                this.plugin.getRuneManager().applyRune(result, rune);
            } catch (RuneException e) {
                throw new RuntimeException(e);
            }
            NamespacedKey runeKey = this.getRuneKey(rune, material);
            ItemRecipe itemRecipe = new ItemRecipe("", "", RecipeType.SMITHING_TRANSFORM, 1, ingredients, new String[0], 0, 0);
            this.recipesUseRunes.put(runeKey, itemRecipe);
            this.plugin.getServer().addRecipe(itemRecipe.toBukkitRecipe(runeKey, result));
        });

    }

    private NamespacedKey getRuneKey(Rune rune, Material material) {
        return new NamespacedKey(this.plugin, "rune_" + rune.getName().toLowerCase() + "_" + material.name().toLowerCase() + "_smithing_craft");
    }
}
