package fr.traqueur.items.registries;

import fr.traqueur.items.ZItems;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.VersionFilter;
import fr.traqueur.items.api.annotations.AutoBlockDataMeta;
import fr.traqueur.items.api.annotations.AutoBlockStateMeta;
import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.items.BlockDataMeta;
import fr.traqueur.items.api.items.BlockStateMeta;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.api.registries.ItemsRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.items.ZItem;
import fr.traqueur.items.utils.ReflectionsCache;
import fr.traqueur.structura.api.Structura;
import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.registries.PolymorphicRegistry;
import org.reflections.Reflections;

import java.nio.file.Path;
import java.util.*;

public class ZItemsRegistry extends FileBasedRegistry<String, Item> implements ItemsRegistry {

    public ZItemsRegistry(ItemsPlugin plugin) {
        super(plugin, ZItems.ITEMS_FOLDER, "Items Registry");
        Reflections reflections = ReflectionsCache.getInstance().getOrCreate(plugin, "fr.traqueur.items");

        PolymorphicRegistry.create(BlockDataMeta.class, registry -> {
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoBlockDataMeta.class);
            int count = 0;
            Set<String> registeredKeys = new HashSet<>();
            for (Class<?> clazz : annotatedClasses) {
                if (!BlockDataMeta.class.isAssignableFrom(clazz)) {
                    Logger.warning("Class <yellow>{}<reset> is annotated with @AutoBlockDataMeta but does not implement BlockDataMeta. Skipping.",
                            clazz.getSimpleName());
                    continue;
                }
                if (!VersionFilter.passes(clazz, clazz.getSimpleName())) continue;
                try {
                    AutoBlockDataMeta meta = clazz.getAnnotation(AutoBlockDataMeta.class);
                    if (registeredKeys.contains(meta.value())) {
                        Logger.warning("BlockDataMeta key '<yellow>{}<reset>' already registered — skipping <yellow>{}<reset>. Check your version/platform annotations.",
                                meta.value(), clazz.getSimpleName());
                        continue;
                    }
                    //noinspection unchecked
                    registry.register(meta.value(), (Class<? extends BlockDataMeta<?>>) clazz);
                    registeredKeys.add(meta.value());
                    count++;
                    Logger.debug("Registered BlockDataMeta type <aqua>{}<reset> with id <gold>{}<reset>.", clazz.getSimpleName(), meta.value());
                } catch (LinkageError e) {
                    Logger.warning("Skipping BlockDataMeta <yellow>{}<reset> — missing API on this server: {}", clazz.getSimpleName(), e.getMessage());
                }
            }
            Logger.info("Registered <gold>{}<reset> BlockDataMeta type(s).", count);
        });

        PolymorphicRegistry.create(BlockStateMeta.class, registry -> {
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoBlockStateMeta.class);
            int count = 0;
            Set<String> registeredKeys = new HashSet<>();
            for (Class<?> clazz : annotatedClasses) {
                if (!BlockStateMeta.class.isAssignableFrom(clazz)) {
                    Logger.warning("Class <yellow>{}<reset> is annotated with @AutoBlockStateMeta but does not implement BlockStateMeta. Skipping.",
                            clazz.getSimpleName());
                    continue;
                }
                if (!VersionFilter.passes(clazz, clazz.getSimpleName())) continue;
                try {
                    AutoBlockStateMeta meta = clazz.getAnnotation(AutoBlockStateMeta.class);
                    if (registeredKeys.contains(meta.value())) {
                        Logger.warning("BlockStateMeta key '<yellow>{}<reset>' already registered — skipping <yellow>{}<reset>. Check your version/platform annotations.",
                                meta.value(), clazz.getSimpleName());
                        continue;
                    }
                    //noinspection unchecked
                    registry.register(meta.value(), (Class<? extends BlockStateMeta<?>>) clazz);
                    registeredKeys.add(meta.value());
                    count++;
                    Logger.debug("Registered BlockStateMeta type <aqua>{}<reset> with id <gold>{}<reset>.", clazz.getSimpleName(), meta.value());
                } catch (LinkageError e) {
                    Logger.warning("Skipping BlockStateMeta <yellow>{}<reset> — missing API on this server: {}", clazz.getSimpleName(), e.getMessage());
                }
            }
            Logger.info("Registered <gold>{}<reset> BlockStateMeta type(s).", count);
        });

        PolymorphicRegistry.create(ItemMetadata.class, registry -> {
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoMetadata.class);
            int count = 0;
            Set<String> registeredKeys = new HashSet<>();
            for (Class<?> clazz : annotatedClasses) {
                if (!ItemMetadata.class.isAssignableFrom(clazz)) {
                    Logger.warning("Class <yellow>{}<reset> is annotated with @AutoMetadata but does not implement ItemMetadata. Skipping.",
                            clazz.getSimpleName());
                    continue;
                }
                if (!VersionFilter.passes(clazz, clazz.getSimpleName())) continue;
                try {
                    AutoMetadata meta = clazz.getAnnotation(AutoMetadata.class);
                    if (registeredKeys.contains(meta.value())) {
                        Logger.warning("ItemMetadata key '<yellow>{}<reset>' already registered — skipping <yellow>{}<reset>. Check your version/platform annotations.",
                                meta.value(), clazz.getSimpleName());
                        continue;
                    }
                    //noinspection unchecked
                    registry.register(meta.value(), (Class<? extends ItemMetadata>) clazz);
                    registeredKeys.add(meta.value());
                    count++;
                    Logger.debug("Registered ItemMetadata type <aqua>{}<reset> with id <gold>{}<reset>.", clazz.getSimpleName(), meta.value());
                } catch (LinkageError e) {
                    Logger.warning("Skipping ItemMetadata <yellow>{}<reset> — missing API on this server: {}", clazz.getSimpleName(), e.getMessage());
                }
            }
            Logger.info("Registered <gold>{}<reset> ItemMetadata type(s).", count);
        });

    }

    @Override
    protected Item loadFile(Path file) {
        try {
            Item item = Structura.load(file, ZItem.class);

            // Validate effects compatibility before registering
            if (!validateEffectsCompatibility(item, file)) {
                Logger.severe("Failed to load item {} from file {}: incompatible effects detected",
                        item.id(), file.getFileName());
                return null;
            }

            this.register(item.id(), item);
            Logger.debug("Loaded item: " + item.id() + " from file: " + file.getFileName());
            return item;
        } catch (StructuraException e) {
            Logger.severe("Failed to load item from file: " + file.getFileName(), e);
            return null;
        }
    }

    /**
     * Validates that all effects in the item settings are compatible with each other.
     *
     * @param item the item to validate
     * @param file the file path for logging purposes
     * @return true if all effects are compatible, false otherwise
     */
    private boolean validateEffectsCompatibility(Item item, Path file) {
        List<Effect> effects = item.effects();
        if (effects == null || effects.isEmpty()) {
            return true; // No effects to validate
        }

        HandlersRegistry registry = Registry.get(HandlersRegistry.class);
        List<EffectHandler<?>> handlers = new ArrayList<>();

        // Get all handlers
        for (Effect effect : effects) {
            EffectHandler<?> handler = registry.getById(effect.type());
            if (handler == null) {
                Logger.warning("Handler not found for effect type {} in item {} ({})",
                        effect.type(), item.id(), file.getFileName());
                return false;
            }
            handlers.add(handler);
        }

        // Check each pair of handlers for incompatibility
        for (int i = 0; i < handlers.size(); i++) {
            EffectHandler<?> handler1 = handlers.get(i);
            Set<Class<? extends EffectHandler<?>>> incompatibles1 = handler1.getIncompatibleHandlers();

            for (int j = i + 1; j < handlers.size(); j++) {
                EffectHandler<?> handler2 = handlers.get(j);

                // Check if handler1 is incompatible with handler2
                if (incompatibles1.contains(handler2.getClass())) {
                    Logger.severe("Incompatible effects in item {} ({}): {} is incompatible with {}",
                            item.id(), file.getFileName(),
                            effects.get(i).type(), effects.get(j).type());
                    return false;
                }

                // Check if handler2 is incompatible with handler1 (bidirectional check)
                Set<Class<? extends EffectHandler<?>>> incompatibles2 = handler2.getIncompatibleHandlers();
                if (incompatibles2.contains(handler1.getClass())) {
                    Logger.severe("Incompatible effects in item {} ({}): {} is incompatible with {}",
                            item.id(), file.getFileName(),
                            effects.get(j).type(), effects.get(i).type());
                    return false;
                }
            }
        }

        return true;
    }
}