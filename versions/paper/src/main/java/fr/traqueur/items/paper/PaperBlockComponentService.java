package fr.traqueur.items.paper;

import fr.traqueur.items.api.services.BlockComponentService;
import net.kyori.adventure.text.Component;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.EnchantingTable;
import org.bukkit.block.sign.SignSide;

/**
 * Paper implementation of {@link BlockComponentService} using Paper's native Adventure API.
 * Registered via {@code META-INF/services/} and discovered by {@link java.util.ServiceLoader}.
 */
public class PaperBlockComponentService implements BlockComponentService {

    @Override
    public void setName(CommandBlock commandBlock, Component name) {
        commandBlock.name(name);
    }

    @Override
    public void setCustomName(EnchantingTable enchantingTable, Component customName) {
        enchantingTable.customName(customName);
    }

    @Override
    public void setLine(SignSide signSide, int index, Component line) {
        signSide.line(index, line);
    }
}