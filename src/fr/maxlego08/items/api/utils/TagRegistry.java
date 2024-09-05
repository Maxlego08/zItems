package fr.maxlego08.items.api.utils;

import org.bukkit.Material;
import org.bukkit.Tag;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TagRegistry {

    private static final Map<String, Tag<Material>> tagMap = new HashMap<>();

    static {
        for (Field field : Tag.class.getDeclaredFields()) {
            if (Tag.class.isAssignableFrom(field.getType())) {
                try {
                    Class<?> genericType = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    if (Material.class.isAssignableFrom(genericType)) {
                        register(field.getName(), (Tag<Material>) field.get(null));
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public static void register(String key, Tag<Material> tag) {
        tagMap.put(key, tag);
    }

    public static Tag<Material> getTag(String key) {
        return tagMap.get(key.toUpperCase());
    }
}