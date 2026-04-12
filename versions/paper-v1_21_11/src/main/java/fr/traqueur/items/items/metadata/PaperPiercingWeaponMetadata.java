package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.annotations.SinceVersion;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@AutoMetadata("piercing-weapon")
@PaperOnly
@SinceVersion("1.21.11")
public record PaperPiercingWeaponMetadata(
        @Options(optional = true) @DefaultBool(false) boolean dealsKnockback,
        @Options(optional = true) @DefaultBool(false) boolean dismounts,
        @Options(optional = true) Sound sound,
        @Options(optional = true) Sound hitSound
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon()
                .dealsKnockback(dealsKnockback)
                .dismounts(dismounts);

        if (sound != null) {
            Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).getKey(sound);
            if (key != null) builder.sound(key);
        }
        if (hitSound != null) {
            Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).getKey(hitSound);
            if (key != null) builder.hitSound(key);
        }

        itemStack.setData(DataComponentTypes.PIERCING_WEAPON, builder.build());
    }
}
