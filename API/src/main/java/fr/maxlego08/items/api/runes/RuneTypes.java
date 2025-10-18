package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.EmptyConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneAttributeConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneEnchantApplicatorConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneFarmingHoeConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneHammerConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneMoneyBoostConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneSellingConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneVeinMiningConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneXPBoostConfiguration;
import fr.maxlego08.items.api.runes.configurations.SlotChangeConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public enum RuneTypes implements RuneType {

    VEIN_MINING("VeinMiner", RuneVeinMiningConfiguration.class) {
        @Override
        public List<RuneType> getIncompatibles() {
            return List.of(HAMMER);
        }
    },
    MELT_MINING("MeltMining", EmptyConfiguration.class),
    FARMING_HOE("FarmingHoe", RuneFarmingHoeConfiguration.class),
    ENCHANT_APPLICATOR("EnchantApplicator", RuneEnchantApplicatorConfiguration.class),
    UNBREAKABLE("Unbreakable", EmptyConfiguration.class),
    HAMMER("Hammer", RuneHammerConfiguration.class) {
        @Override
        public List<RuneType> getIncompatibles() {
            return List.of(VEIN_MINING);
        }
    },
    SILK_SPAWNER("SilkSpawner", EmptyConfiguration.class),
    ABSORPTION("Absorption", EmptyConfiguration.class) {
        @Override
        public List<RuneType> getIncompatibles() {
            return List.of(SELLER);
        }
    },
    XP_BOOST("XPBoost", RuneXPBoostConfiguration.class),
    JOB_XP_BOOST("JobXPBoost", RuneXPBoostConfiguration.class),
    JOB_MONEY_BOOST("JobMoneyBoost", RuneMoneyBoostConfiguration.class),
    ATTRIBUTE_APPLICATOR("AttributeApplicator", RuneAttributeConfiguration.class),
    SELLER("Seller", RuneSellingConfiguration.class) {
        @Override
        public List<RuneType> getIncompatibles() {
            return List.of(ABSORPTION);
        }
    },
    SELL_STICK("SellStick", RuneSellingConfiguration.class),
    SLOT_CHANGE("SlotChange", SlotChangeConfiguration.class),
    INFINITE_BUCKET("InfiniteBucket", EmptyConfiguration.class),
    EMPTY("Empty", EmptyConfiguration.class),
    ;

    private final RuneActivator activator;
    private final Class<? extends RuneConfiguration> configuration;

    RuneTypes(String className, Class<? extends RuneConfiguration> configuration) {
        try {
            this.activator = (RuneActivator) Class.forName("fr.maxlego08.items.runes.activators." + className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
        this.configuration = configuration;
    }

    @Override
    public List<RuneType> getIncompatibles() {
        return List.of();
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public RuneActivator getActivator() {
        return this.activator;
    }

    @Override
    public RuneConfiguration getConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        try {
            return this.configuration.getConstructor(ItemPlugin.class, YamlConfiguration.class, String.class).newInstance(plugin, configuration, runeName);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
