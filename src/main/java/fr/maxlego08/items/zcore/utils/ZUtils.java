package fr.maxlego08.items.zcore.utils;

import fr.maxlego08.items.zcore.ZPlugin;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.nms.NmsVersion;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permissible;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public abstract class ZUtils extends MessageUtils {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile(net.md_5.bungee.api.ChatColor.COLOR_CHAR + "x[a-fA-F0-9-" + net.md_5.bungee.api.ChatColor.COLOR_CHAR + "]{12}");

    // For plugin support from 1.8 to 1.12
    private static Material[] byId;

    static {
        if (!NmsVersion.nmsVersion.isNewMaterial()) {
            byId = new Material[0];
            for (Material material : Material.values()) {
                if (byId.length <= material.getId()) {
                    byId = Arrays.copyOfRange(byId, 0, material.getId() + 2);
                }
                byId[material.getId()] = material;
            }
        }
    }

    /**
     * Checks if the player's inventory is full.
     *
     * @param player the player to check.
     * @return true if the player's inventory is full, false otherwise.
     */
    protected boolean hasInventoryFull(Player player) {
        int slot = 0;
        PlayerInventory inventory = player.getInventory();
        for (int a = 0; a != 36; a++) {
            ItemStack itemStack = inventory.getContents()[a];
            if (itemStack == null) slot++;
        }
        return slot == 0;
    }


    public void runAsync(ZPlugin plugin, Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    protected List<String> color(List<String> messages) {
        return messages.stream().map(this::color).collect(Collectors.toList());
    }

    protected List<String> colorReverse(List<String> messages) {
        return messages.stream().map(this::colorReverse).collect(Collectors.toList());
    }

    /**
     * Formats a long value with a specified grouping separator.
     *
     * @param l the long value to format.
     * @param c the character to use as the grouping separator.
     * @return the formatted string.
     */
    protected String format(long l, char c) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(c);
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(l);
    }

    /**
     * Reverses color codes in a message string.
     *
     * @param message the message string with color codes to reverse.
     * @return the message string with reversed color codes.
     */
    protected String colorReverse(String message) {
        if (message == null) return null;

        // Use StringBuilder for efficient string manipulation
        StringBuilder result = new StringBuilder(message);
        Matcher matcher = HEX_COLOR_PATTERN.matcher(result);

        // Process matches in reverse order to maintain correct indices
        List<int[]> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(new int[]{matcher.start(), matcher.end()});
        }

        // Replace from end to beginning to avoid index shifting
        for (int i = matches.size() - 1; i >= 0; i--) {
            int start = matches.get(i)[0];
            int end = matches.get(i)[1];
            String color = result.substring(start, end);
            String colorReplace = color.replace("§x", "#").replace("§", "");
            result.replace(start, end, colorReplace);
        }

        // Replace all remaining § with &
        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '§') {
                result.setCharAt(i, '&');
            }
        }

        return result.toString();
    }

    /**
     * Gives an item to the player. If the player's inventory is full, the item will be dropped on the ground.
     *
     * @param player the player to receive the item.
     * @param item   the item to give to the player.
     */
    protected void give(Player player, ItemStack item) {
        if (hasInventoryFull(player)) {
            player.getWorld().dropItem(player.getLocation(), item);
        } else {
            player.getInventory().addItem(item);
        }
    }


    /**
     * Checks if a permissible entity has a specific permission.
     *
     * @param permissible the entity to check.
     * @param permission  the permission string to check for.
     * @return true if the entity has the permission, false otherwise.
     */
    protected boolean hasPermission(Permissible permissible, String permission) {
        return permissible.hasPermission(permission);
    }

}