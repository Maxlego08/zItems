package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.SinceVersion;
import fr.traqueur.items.api.annotations.SpigotOnly;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.KineticWeaponComponent;
import org.jetbrains.annotations.Nullable;

@AutoMetadata("kinetic-weapon")
@SpigotOnly
@SinceVersion("1.21.11")
public record SpigotKineticWeaponMetadata(
        @Options(optional = true) @DefaultInt(5) int contactCooldownTicks,
        @Options(optional = true) @DefaultInt(0) int delayTicks,
        @Options(optional = true) @DefaultDouble(1.0) double damageMultiplier,
        @Options(optional = true) @DefaultDouble(0.5) double forwardMovement,
        @Options(optional = true) Sound sound,
        @Options(optional = true) Sound hitSound
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        ItemMeta meta = itemStack.getItemMeta();
        KineticWeaponComponent component = meta.getKineticWeapon();
        component.setContactCooldownTicks(contactCooldownTicks);
        component.setDelayTicks(delayTicks);
        component.setDamageMultipler((float) damageMultiplier);
        component.setForwardMovement((float) forwardMovement);
        if (sound != null) component.setSound(sound);
        if (hitSound != null) component.setHitSound(hitSound);
        meta.setKineticWeapon(component);
        itemStack.setItemMeta(meta);
    }
}
