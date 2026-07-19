package fr.traqueur.items.effects.utility;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.effects.engine.EmptySettings;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

@AutoEffect(value = "INFINITE_BUCKET")
public class InfiniteBucket implements EffectHandler.MultiEventEffectHandler<EmptySettings> {

    private static final List<Material> FORBIDDEN_MATERIALS = List.of(
            Material.MILK_BUCKET,
            Material.POWDER_SNOW_BUCKET
    );

    @Override
    public Set<Class<? extends Event>> eventTypes() {
        return Set.of(PlayerBucketFillEvent.class, PlayerBucketEmptyEvent.class);
    }

    @Override
    public void handle(EffectContext context, EmptySettings settings) {
        if (!eventTypes().contains(context.event().getClass())) {
            throw new IllegalArgumentException("Unsupported event type: " + context.event().getClass());
        }

        if (context.event() instanceof PlayerBucketEmptyEvent emptyEvent) {
            handleBucketEmpty(emptyEvent);
        } else if (context.event() instanceof PlayerBucketFillEvent fillEvent) {
            handleBucketFill(fillEvent);
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    private void handleBucketEmpty(PlayerBucketEmptyEvent event) {
        Material bucketType = event.getBucket();
        if (FORBIDDEN_MATERIALS.contains(bucketType)) {
            return;
        }
        event.setCancelled(true);
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        block.setType(this.getFilledBlockMaterial(bucketType));
    }

    private void handleBucketFill(PlayerBucketFillEvent event) {
        event.setCancelled(true);
        event.getBlockClicked().setType(Material.AIR);
    }

    private @NotNull Material getFilledBlockMaterial(Material bucketType) {
        return switch (bucketType) {
            case LAVA_BUCKET -> Material.LAVA;
            default -> Material.WATER;
        };
    }

}
