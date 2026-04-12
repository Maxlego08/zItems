package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.annotations.SinceVersion;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@AutoMetadata("kinetic-weapon")
@PaperOnly
@SinceVersion("1.21.11")
public record PaperKineticWeaponMetadata(
        @Options(optional = true) @DefaultInt(5) int contactCooldownTicks,
        @Options(optional = true) @DefaultInt(0) int delayTicks,
        @Options(optional = true) @DefaultDouble(1.0) double damageMultiplier,
        @Options(optional = true) @DefaultDouble(0.5) double forwardMovement,
        @Options(optional = true) Sound sound,
        @Options(optional = true) Sound hitSound
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon()
                .contactCooldownTicks(contactCooldownTicks)
                .delayTicks(delayTicks)
                .damageMultiplier((float) damageMultiplier)
                .forwardMovement((float) forwardMovement);

        if (sound != null) {
            Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).getKey(sound);
            if (key != null) builder.sound(key);
        }
        if (hitSound != null) {
            Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).getKey(hitSound);
            if (key != null) builder.hitSound(key);
        }

        itemStack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
    }
}
