package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.SinceVersion;
import fr.traqueur.items.api.annotations.SpigotOnly;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.PiercingWeaponComponent;
import org.jetbrains.annotations.Nullable;

@AutoMetadata("piercing-weapon")
@SpigotOnly
@SinceVersion("1.21.11")
public record SpigotPiercingWeaponMetadata(
        @Options(optional = true) @DefaultBool(false) boolean dealsKnockback,
        @Options(optional = true) @DefaultBool(false) boolean dismounts,
        @Options(optional = true) Sound sound,
        @Options(optional = true) Sound hitSound
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        ItemMeta meta = itemStack.getItemMeta();
        PiercingWeaponComponent component = meta.getPiercingWeapon();
        component.setDealsKnockback(dealsKnockback);
        component.setDismounts(dismounts);
        if (sound != null) component.setSound(sound);
        if (hitSound != null) component.setHitSound(hitSound);
        meta.setPiercingWeapon(component);
        itemStack.setItemMeta(meta);
    }
}
