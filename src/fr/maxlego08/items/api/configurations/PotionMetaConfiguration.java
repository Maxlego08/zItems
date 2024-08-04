package fr.maxlego08.items.api.configurations;

import org.bukkit.Color;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record PotionMetaConfiguration(boolean enable, Color color, List<CustomPotionEffect> customPotionEffects,
                                      PotionType basePotionType) {

    public static final Map<String, PotionEffectType> potionEffectTypeMap = new HashMap<>();

    static {
        potionEffectTypeMap.put("speed", PotionEffectType.SPEED);
        potionEffectTypeMap.put("slowness", PotionEffectType.SLOWNESS);
        potionEffectTypeMap.put("haste", PotionEffectType.HASTE);
        potionEffectTypeMap.put("mining_fatigue", PotionEffectType.MINING_FATIGUE);
        potionEffectTypeMap.put("strength", PotionEffectType.STRENGTH);
        potionEffectTypeMap.put("instant_health", PotionEffectType.INSTANT_HEALTH);
        potionEffectTypeMap.put("instant_damage", PotionEffectType.INSTANT_DAMAGE);
        potionEffectTypeMap.put("jump_boost", PotionEffectType.JUMP_BOOST);
        potionEffectTypeMap.put("nausea", PotionEffectType.NAUSEA);
        potionEffectTypeMap.put("regeneration", PotionEffectType.REGENERATION);
        potionEffectTypeMap.put("resistance", PotionEffectType.RESISTANCE);
        potionEffectTypeMap.put("fire_resistance", PotionEffectType.FIRE_RESISTANCE);
        potionEffectTypeMap.put("water_breathing", PotionEffectType.WATER_BREATHING);
        potionEffectTypeMap.put("invisibility", PotionEffectType.INVISIBILITY);
        potionEffectTypeMap.put("blindness", PotionEffectType.BLINDNESS);
        potionEffectTypeMap.put("night_vision", PotionEffectType.NIGHT_VISION);
        potionEffectTypeMap.put("hunger", PotionEffectType.HUNGER);
        potionEffectTypeMap.put("weakness", PotionEffectType.WEAKNESS);
        potionEffectTypeMap.put("poison", PotionEffectType.POISON);
        potionEffectTypeMap.put("wither", PotionEffectType.WITHER);
        potionEffectTypeMap.put("health_boost", PotionEffectType.HEALTH_BOOST);
        potionEffectTypeMap.put("absorption", PotionEffectType.ABSORPTION);
        potionEffectTypeMap.put("saturation", PotionEffectType.SATURATION);
        potionEffectTypeMap.put("glowing", PotionEffectType.GLOWING);
        potionEffectTypeMap.put("levitation", PotionEffectType.LEVITATION);
        potionEffectTypeMap.put("luck", PotionEffectType.LUCK);
        potionEffectTypeMap.put("unluck", PotionEffectType.UNLUCK);
        potionEffectTypeMap.put("slow_falling", PotionEffectType.SLOW_FALLING);
        potionEffectTypeMap.put("conduit_power", PotionEffectType.CONDUIT_POWER);
        potionEffectTypeMap.put("dolphins_grace", PotionEffectType.DOLPHINS_GRACE);
        potionEffectTypeMap.put("bad_omen", PotionEffectType.BAD_OMEN);
        potionEffectTypeMap.put("hero_of_the_village", PotionEffectType.HERO_OF_THE_VILLAGE);
        potionEffectTypeMap.put("darkness", PotionEffectType.DARKNESS);
        potionEffectTypeMap.put("trial_omen", PotionEffectType.TRIAL_OMEN);
        potionEffectTypeMap.put("raid_omen", PotionEffectType.RAID_OMEN);
        potionEffectTypeMap.put("wind_charged", PotionEffectType.WIND_CHARGED);
        potionEffectTypeMap.put("weaving", PotionEffectType.WEAVING);
        potionEffectTypeMap.put("oozing", PotionEffectType.OOZING);
        potionEffectTypeMap.put("infested", PotionEffectType.INFESTED);
    }

}
