package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.SinceVersion;
import fr.traqueur.items.api.annotations.SpigotOnly;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.AttackRangeComponent;
import org.jetbrains.annotations.Nullable;

@AutoMetadata("attack-range")
@SpigotOnly
@SinceVersion("1.21.11")
public record SpigotAttackRangeMetadata(
        @Options(optional = true) @DefaultDouble(0.0) double minReach,
        @Options(optional = true) @DefaultDouble(3.0) double maxReach,
        @Options(optional = true) @DefaultDouble(-1.0) double minCreativeReach,
        @Options(optional = true) @DefaultDouble(-1.0) double maxCreativeReach,
        @Options(optional = true) @DefaultDouble(0.0) double hitboxMargin,
        @Options(optional = true) @DefaultDouble(1.0) double mobFactor
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        ItemMeta meta = itemStack.getItemMeta();
        AttackRangeComponent component = meta.getAttackRange();
        component.setMinReach((float) minReach);
        component.setMaxReach((float) maxReach);
        component.setMinCreativeReach((float) minCreativeReach);
        component.setMaxCreativeReach((float) maxCreativeReach);
        component.setHitboxMargin((float) hitboxMargin);
        component.setMobFactor((float) mobFactor);
        meta.setAttackRange(component);
        itemStack.setItemMeta(meta);
    }
}
