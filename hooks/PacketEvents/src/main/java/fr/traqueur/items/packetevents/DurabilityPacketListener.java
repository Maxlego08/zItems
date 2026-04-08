package fr.traqueur.items.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.items.DurabilityMode;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DurabilityPacketListener extends PacketListenerAbstract {

    private static final String PLACEHOLDER_DURABILITY = "<durability>";
    private static final String PLACEHOLDER_MAX_DURABILITY = "<max_durability>";

    private final String durabilityModeKey;
    private final String customDurabilityKey;
    private final String customMaxDurabilityKey;

    public DurabilityPacketListener() {
        super(PacketListenerPriority.NORMAL);
        ItemsPlugin plugin = JavaPlugin.getPlugin(ItemsPlugin.class);
        // NamespacedKey.toString() → "namespace:key" which is the PDC key in PublicBukkitValues NBT
        this.durabilityModeKey = new NamespacedKey(plugin, "durability_mode").toString();
        this.customDurabilityKey = new NamespacedKey(plugin, "custom_durability").toString();
        this.customMaxDurabilityKey = new NamespacedKey(plugin, "custom_max_durability").toString();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(event);
            ItemStack resolved = resolveItem(wrapper.getItem());
            if (resolved != null) {
                wrapper.setItem(resolved);
                event.markForReEncode(true);
            }

        } else if (event.getPacketType() == PacketType.Play.Server.SET_CURSOR_ITEM) {
            // Since 1.21.2, the cursor (mouse-held) item uses its own packet — not SET_SLOT windowId=-1
            WrapperPlayServerSetCursorItem wrapper = new WrapperPlayServerSetCursorItem(event);
            ItemStack resolved = resolveItem(wrapper.getStack());
            if (resolved != null) {
                wrapper.setStack(resolved);
                event.markForReEncode(true);
            }

        } else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(event);
            List<ItemStack> items = wrapper.getItems();
            List<ItemStack> newItems = new ArrayList<>(items.size());
            boolean changed = false;
            for (ItemStack item : items) {
                ItemStack resolved = resolveItem(item);
                if (resolved != null) {
                    newItems.add(resolved);
                    changed = true;
                } else {
                    newItems.add(item);
                }
            }
            Optional<ItemStack> carried = wrapper.getCarriedItem();
            ItemStack resolvedCarried = carried.isPresent() ? resolveItem(carried.get()) : null;
            if (resolvedCarried != null) changed = true;

            if (changed) {
                // Cancel the original packet to avoid markForReEncode writing back to NMS items,
                // then send a fresh packet built from our resolved copies.
                event.setCancelled(true);
                ItemStack finalCarried = resolvedCarried != null ? resolvedCarried : carried.orElse(null);
                WrapperPlayServerWindowItems newWrapper = new WrapperPlayServerWindowItems(
                        wrapper.getWindowId(), wrapper.getStateId(), newItems, finalCarried
                );
                PacketEvents.getAPI().getPlayerManager().sendPacket(event.getPlayer(), newWrapper);
            }
        }
    }

    /**
     * Reads PDC directly from the packet item's {@code minecraft:custom_data} NBT component
     * and resolves durability placeholders in a copy of the item's lore, leaving the
     * server-side NMS item untouched.
     *
     * @return a new independent {@link ItemStack} with resolved lore, or {@code null} if unchanged
     */
    private ItemStack resolveItem(ItemStack item) {
        if (item == null || item.isEmpty()) return null;

        // Read custom_data component (where Paper stores PDC / PublicBukkitValues)
        NBTCompound customData = (NBTCompound) item.getComponents().getPatches()
                .getOrDefault(ComponentTypes.CUSTOM_DATA, Optional.empty()).orElse(null);
        if (customData == null) return null;

        NBTCompound pbv = customData.getCompoundTagOrNull("PublicBukkitValues");
        if (pbv == null) return null;

        String modeStr = pbv.getStringTagValueOrNull(durabilityModeKey);
        if (modeStr == null) return null;

        int current;
        int max;

        if (DurabilityMode.CUSTOM.name().equals(modeStr)) {
            Map<String, NBT> tags = pbv.getTags();
            NBT currentNbt = tags.get(customDurabilityKey);
            NBT maxNbt = tags.get(customMaxDurabilityKey);
            if (!(currentNbt instanceof NBTInt currentInt) || !(maxNbt instanceof NBTInt maxInt)) return null;
            current = currentInt.getAsInt();
            max = maxInt.getAsInt();
        } else {
            // VANILLA: damage components are reliable in the packet
            int damage = item.getComponentOr(ComponentTypes.DAMAGE, 0);
            int maxDamage = item.getComponentOr(ComponentTypes.MAX_DAMAGE, 0);
            if (maxDamage <= 0) return null;
            max = maxDamage;
            current = maxDamage - damage;
        }

        ItemLore lore = item.getComponentOr(ComponentTypes.LORE, ItemLore.EMPTY);
        List<Component> lines = lore.getLines();
        if (lines.isEmpty()) return null;
        if (lines.stream().noneMatch(this::containsPlaceholder)) return null;

        List<Component> resolvedLines = lines.stream()
                .map(line -> replacePlaceholders(line, current, max))
                .toList();

        // Create an independent copy via Bukkit clone so that setComponent only touches the
        // packet copy, leaving the server-side NMS inventory item and its lore intact.
        org.bukkit.inventory.ItemStack cloned = SpigotConversionUtil.toBukkitItemStack(item).clone();
        ItemStack copy = SpigotConversionUtil.fromBukkitItemStack(cloned);
        copy.setComponent(ComponentTypes.LORE, new ItemLore(resolvedLines));
        return copy;
    }

    private boolean containsPlaceholder(Component component) {
        String plain = PlainTextComponentSerializer.plainText().serialize(component);
        return plain.contains(PLACEHOLDER_DURABILITY) || plain.contains(PLACEHOLDER_MAX_DURABILITY);
    }

    private Component replacePlaceholders(Component line, int current, int max) {
        TextReplacementConfig durConfig = TextReplacementConfig.builder()
                .matchLiteral(PLACEHOLDER_DURABILITY)
                .replacement(Component.text(current))
                .build();
        TextReplacementConfig maxConfig = TextReplacementConfig.builder()
                .matchLiteral(PLACEHOLDER_MAX_DURABILITY)
                .replacement(Component.text(max))
                .build();
        return line.replaceText(durConfig).replaceText(maxConfig);
    }
}