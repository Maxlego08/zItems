package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.annotations.SinceVersion;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@AutoMetadata("attack-range")
@PaperOnly
@SinceVersion("1.21.11")
public record PaperAttackRangeMetadata(
        @Options(optional = true) @DefaultDouble(0.0) double minReach,
        @Options(optional = true) @DefaultDouble(3.0) double maxReach,
        @Options(optional = true) @DefaultDouble(-1.0) double minCreativeReach,
        @Options(optional = true) @DefaultDouble(-1.0) double maxCreativeReach,
        @Options(optional = true) @DefaultDouble(0.0) double hitboxMargin,
        @Options(optional = true) @DefaultDouble(1.0) double mobFactor
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        AttackRange component = AttackRange.attackRange()
                .minReach((float) minReach)
                .maxReach((float) maxReach)
                .minCreativeReach((float) minCreativeReach)
                .maxCreativeReach((float) maxCreativeReach)
                .hitboxMargin((float) hitboxMargin)
                .mobFactor((float) mobFactor)
                .build();
        itemStack.setData(DataComponentTypes.ATTACK_RANGE, component);
    }
}
