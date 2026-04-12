package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.annotations.SinceVersion;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@AutoMetadata("blocks-attacks")
@PaperOnly
@SinceVersion("1.21.5")
public record BlocksAttacksMetadata(
        @Options(optional = true) @DefaultDouble(0.0) double blockDelaySeconds,
        @Options(optional = true) @DefaultDouble(1.0) double disableCooldownScale,
        @Options(optional = true) Sound blockSound,
        @Options(optional = true) Sound disableSound
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        BlocksAttacks.Builder builder = BlocksAttacks.blocksAttacks()
                .blockDelaySeconds((float) blockDelaySeconds)
                .disableCooldownScale((float) disableCooldownScale);

        if (blockSound != null) {
            Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).getKey(blockSound);
            if (key != null) builder.blockSound(key);
        }
        if (disableSound != null) {
            Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).getKey(disableSound);
            if (key != null) builder.disableSound(key);
        }

        itemStack.setData(DataComponentTypes.BLOCKS_ATTACKS, builder.build());
    }
}
