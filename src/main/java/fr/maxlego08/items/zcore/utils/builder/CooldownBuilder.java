package fr.maxlego08.items.zcore.utils.builder;

import fr.maxlego08.items.zcore.utils.storage.Persist;
import fr.maxlego08.items.zcore.utils.storage.Savable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownBuilder implements Savable {

    private static final CooldownBuilder i = new CooldownBuilder();
    public static Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    public static Map<UUID, Long> getCooldownMap(String key) {
        return cooldowns.getOrDefault(key, null);
    }

    /**
     *
     */
    public static void clear() {
        cooldowns.clear();
    }

    public static void createCooldown(String key) {
        cooldowns.putIfAbsent(key, new HashMap<>());
    }

    public static void removeCooldown(String key, UUID uuid) {

        createCooldown(key);

        getCooldownMap(key).remove(uuid);
    }

    public static void removeCooldown(String key, Player player) {
        removeCooldown(key, player.getUniqueId());
    }

    public static void addCooldown(String key, UUID uuid, long milliseconds) {

        createCooldown(key);

        long next = System.currentTimeMillis() + milliseconds;
        getCooldownMap(key).put(uuid, next);
    }

    public static void addCooldown(String key, Player player, long millisecondes) {
        addCooldown(key, player.getUniqueId(), millisecondes);
    }

    public static boolean isCooldown(String key, UUID uuid) {

        createCooldown(key);
        Map<UUID, Long> map = cooldowns.get(key);

        return (map.containsKey(uuid)) && (System.currentTimeMillis() <= map.get(uuid));
    }

    public static boolean isCooldown(String key, Player player) {
        return isCooldown(key, player.getUniqueId());
    }

    public static long getCooldown(String key, UUID uuid) {

        createCooldown(key);
        Map<UUID, Long> map = cooldowns.get(key);

        return map.getOrDefault(uuid, 0L).longValue() - System.currentTimeMillis();
    }

    public static long getCooldownPlayer(String key, Player player) {
        return getCooldown(key, player.getUniqueId());
    }

    public static String getCooldownAsString(String key, UUID player) {
        return TimerBuilder.getStringTime(getCooldown(key, player) / 1000);
    }

    public static CooldownBuilder getInstance() {
        return i;
    }

    @Override
    public void save(Persist persist) {
        persist.save(i, "cooldowns");
    }

    @Override
    public void load(Persist persist) {
        persist.loadOrSaveDefault(i, CooldownBuilder.class, "cooldowns");
    }
}
