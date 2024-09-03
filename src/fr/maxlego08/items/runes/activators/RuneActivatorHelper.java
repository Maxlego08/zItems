package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.events.CustomBlockBreakEvent;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class RuneActivatorHelper<T extends RuneConfiguration> implements RuneActivator<T> {

    @Override
    public void jobsGainMoney(ItemPlugin plugin, JobsPayementEventWrapper event, T runeConfiguration) {}

    @Override
    public void jobsGainExperience(ItemPlugin plugin, JobsExpGainEventWrapper event, T runeConfiguration) {}

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, T runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        return new HashSet<>();
    }

    @Override
    public void interactBlock(ItemPlugin plugin, PlayerInteractEvent listener, T runeConfiguration) {

    }

    @Override
    public void applyOnItems(ItemPlugin plugin, ItemMeta itemMeta, T runeConfiguration) throws Exception {

    }

    protected void applyDamageToItem(ItemStack itemStack, int damage, LivingEntity livingEntity) {
        itemStack.damage(damage, livingEntity);
    }

    protected boolean triggerBlockBreakEvent(T runeConfiguration, Block targetBlock, Player player) {
        if (runeConfiguration.isEventBlockBreakEvent()) {
            CustomBlockBreakEvent customBlockBreakEvent = new CustomBlockBreakEvent(targetBlock, player);
            customBlockBreakEvent.callEvent();
            return customBlockBreakEvent.isCancelled();
        }
        return false;
    }

    protected boolean isValidTargetBlock(ItemPlugin plugin, Player player, Block targetBlock, Set<Block> origin, T configuration) {
        return targetBlock != null && !origin.contains(targetBlock) && !targetBlock.getType().isAir() && plugin.hasAccess(player, targetBlock.getLocation()) && configuration.contains(targetBlock.getType());
    }
}
