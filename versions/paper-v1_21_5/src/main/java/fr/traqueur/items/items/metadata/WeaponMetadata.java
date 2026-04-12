package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.annotations.SinceVersion;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Weapon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@AutoMetadata("weapon")
@PaperOnly
@SinceVersion("1.21.5")
public record WeaponMetadata(
        @Options(optional = true) @DefaultInt(1) int itemDamagePerAttack,
        @Options(optional = true) @DefaultDouble(0.0) double disableBlockingForSeconds
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        Weapon weapon = Weapon.weapon()
                .itemDamagePerAttack(itemDamagePerAttack)
                .disableBlockingForSeconds((float) disableBlockingForSeconds)
                .build();
        itemStack.setData(DataComponentTypes.WEAPON, weapon);
    }
}
