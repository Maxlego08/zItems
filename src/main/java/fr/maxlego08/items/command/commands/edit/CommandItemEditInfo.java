package fr.maxlego08.items.command.commands.edit;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class CommandItemEditInfo extends VCommand {

    public CommandItemEditInfo(ItemsPlugin plugin) {
        super(plugin);
        this.addSubCommand("info");
        this.setPermission(Permission.ZITEMS_EDIT_INFO);
        this.setDescription(Message.DESCRIPTION_ITEM_INFO);
        this.onlyPlayers();
    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {

        ItemStack itemStack = this.player.getInventory().getItemInMainHand();
        if (itemStack.getType().isAir()) {
            message(sender, Message.COMMAND_ITEM_EMPTY);
            return CommandType.DEFAULT;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        message(sender, Message.COMMAND_ITEM_NAME_AND_AMOUNT, "%item-name%", itemStack.getType().toString(), "%amount%", String.valueOf(itemStack.getAmount()));

        if (itemMeta.hasLore()) {
            plugin.getItemComponent().sendItemLore(player, itemMeta);
        }

        if (itemMeta.hasEnchants()) {
            StringBuilder enchantsBuilder = new StringBuilder();
            for (Map.Entry<Enchantment, Integer> enchant : itemMeta.getEnchants().entrySet()) {
                enchantsBuilder.append(enchant.getKey().getKey().getKey())
                        .append(" : ").append(enchant.getValue()).append(", ");
            }
            String enchantments = enchantsBuilder.length() > 0 ? enchantsBuilder.substring(0, enchantsBuilder.length() - 2) : "None";
            message(sender, Message.COMMAND_ITEM_ENCHANTMENTS, "%enchantments%", enchantments);
        }

        if (itemMeta.hasCustomModelData()) {
            message(sender, Message.COMMAND_ITEM_CUSTOM_MODEL_DATA_INFO, "%custom-model-data%", String.valueOf(itemMeta.getCustomModelData()));
        }

        if (itemStack.getType().getMaxDurability() > 0 && itemStack.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable) {
            int durability = ((org.bukkit.inventory.meta.Damageable) itemMeta).getDamage();
            message(sender, Message.COMMAND_ITEM_DURABILITY, "%durability%", String.valueOf(itemStack.getType().getMaxDurability() - durability), "%max-durability%", String.valueOf(itemStack.getType().getMaxDurability()));
        }

        if (!itemMeta.getPersistentDataContainer().isEmpty()) {
            StringBuilder dataBuilder = new StringBuilder();
            itemMeta.getPersistentDataContainer().getKeys().forEach(key -> {
                Object value = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (value != null) {
                    dataBuilder.append(key.getKey()).append(" : ").append(value).append(", ");
                }
            });
            String dataString = dataBuilder.length() > 0 ? dataBuilder.substring(0, dataBuilder.length() - 2) : "None";
            message(sender, Message.COMMAND_ITEM_NBT_DATA, "%nbt-data%", dataString);
        }

        if (!itemMeta.getItemFlags().isEmpty()) {
            StringBuilder flagsBuilder = new StringBuilder();
            for (ItemFlag flag : itemMeta.getItemFlags()) {
                flagsBuilder.append(flag.name()).append(", ");
            }
            String flagsString = flagsBuilder.length() > 0 ? flagsBuilder.substring(0, flagsBuilder.length() - 2) : "None";
            message(sender, Message.COMMAND_ITEM_FLAGS, "%flags%", flagsString);
        }

        String unbreakable = itemMeta.isUnbreakable() ? "Unbreakable ✅" : "Unbreakable ❌";
        message(sender, Message.COMMAND_ITEM_UNBREAKABLE, "%unbreakable%", unbreakable);

        return CommandType.SUCCESS;
    }
}
