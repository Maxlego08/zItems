package fr.maxlego08.items.zcore.enums;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public enum Message {

    PREFIX("&8(&6zItems&8) "),

    INVENTORY_CLONE_NULL("&cThe inventory clone is null!"),
    INVENTORY_OPEN_ERROR("&cAn error occurred with the opening of the inventory &6%id%&c."),
    TIME_DAY("%02d %day% %02d %hour% %02d %minute% %02d %second%"),
    TIME_HOUR("%02d %hour% %02d minute(s) %02d %second%"),
    TIME_MINUTE("%02d %minute% %02d %second%"),
    TIME_SECOND("%02d %second%"),

    FORMAT_SECOND("second"), FORMAT_SECONDS("seconds"),

    FORMAT_MINUTE("minute"), FORMAT_MINUTES("minutes"),

    FORMAT_HOUR("hour"), FORMAT_HOURS("hours"),

    FORMAT_DAY("d"), FORMAT_DAYS("days"),

    COMMAND_SYNTAXE_ERROR("&cYou must execute the command like this&7: &a%syntax%"),
    COMMAND_NO_PERMISSION("&cYou do not have permission to run this command."),
    COMMAND_NO_CONSOLE("&cOnly one player can execute this command."),
    COMMAND_NO_ARG("&cImpossible to find the command with its arguments."),
    COMMAND_SYNTAXE_HELP("&f%syntax% &7» &7%description%"),

    RELOAD("&aYou have just reloaded the configuration files."),

    DESCRIPTION_RELOAD("Reload configuration files"),
    DESCRIPTION_GIVE("Give an item"),
    DESCRIPTION_RUN_APPLY("Apply a rune to an item"),
    DESCRIPTION_VIEW_RUNES("View the runes of an item"),
    DESCRIPTION_ITEM_INFO("Display item informations"),

    ITEM_NOT_FOUND("&cImpossible to find the item &f%name%&c."),
    ITEM_HAVE_NOT_META("&cThis item does not have any metadata."),
    ITEM_GIVE("&aYou just gave &b%name% to the player &f%player%&a."),
    COMMAND_RUNE_NOT_FOUND("&cCannot find rune %rune%."),
    COMMAND_RUNE_ALREADY_APPLIED("&cThis item already contains the rune %rune%."),

    RUNE_LORE("", "&7Runes"),
    RUNE_LINE("&8- %rune%"),

    COMMAND_RUNE_NOT_ALLOWED("&cThis item does not support the rune %rune%."),
    RUNE_MORE("&8- &fAnd More..."),

    DESCRIPTION_ITEM_NAME("Change the item name"),
    DESCRIPTION_ITEM_CUSTOM_MODEL_DATA("Change custom model data"),
    DESCRIPTION_ITEM_LORE("Show commands for the lore"),
    DESCRIPTION_ITEM_LORE_SET("Define a line of a lore"),
    DESCRIPTION_ITEM_LORE_ADD("Add a line to lore"),
    DESCRIPTION_ITEM_LORE_CLEAR("Clear item lore"),


    COMMAND_ITEM_EMPTY("&cYou have no item in your hand."),
    COMMAND_ITEM_CLEAR("&aYou have cleared this item's name"),
    COMMAND_ITEM_SET("&aYou have renamed your held item to &f%name%&a."),
    COMMAND_ITEM_LORE_ADD("&aYou just added the line &f%text%&a."),
    COMMAND_ITEM_LORE_SET("&aYou just set line %line% to &f%text%&a."),
    COMMAND_ITEM_LORE_CLEAR("&aYou just clear the item lore."),
    COMMAND_ITEM_LORE_SET_ERROR("&cCan’t find the line &f%line%&c."),
    COMMAND_ITEM_CUSTOM_MODEL_DATA("&aYou just updated the custom model data to &f%custom-model-data%&a."),
    COMMAND_ITEM_NAME_AND_AMOUNT("&eItem name: &f%item-name%&e, Amount: &f%amount%"),
    COMMAND_ITEM_LORE("&eItem Lore:"),
    COMMAND_ITEM_LORE_LINE("&7- "),
    COMMAND_ITEM_ENCHANTMENTS("&eEnchantments: &f%enchantments%"),
    COMMAND_ITEM_CUSTOM_MODEL_DATA_INFO("&aCustom Model Data: &f%custom-model-data%"),
    COMMAND_ITEM_DURABILITY("&eDurability: &f%durability%&e/&f%max-durability%"),
    COMMAND_ITEM_ATTRIBUTES("&eAttributes: &f%attributes%"),
    COMMAND_ITEM_NBT_DATA("&eNBT Data: &f%nbt-data%"),
    COMMAND_ITEM_FLAGS("&eItem Flags: &f%flags%"),
    COMMAND_ITEM_UNBREAKABLE("&eUnbreakable: &f%unbreakable%"),
    COMMAND_ITEM_RARITY("&eRarity: &f%rarity%"),
    COMMAND_ITEM_OWNER("&eOwner: &f%owner%"),



    ;

    private List<String> messages;
    private String message;
    private Map<String, Object> titles = new HashMap<>();
    private boolean use = true;
    private MessageType type = MessageType.TCHAT;
    private ItemStack itemStack;

    /**
     * Constructs a new Message with the specified message string.
     *
     * @param message the message string.
     */
    Message(String message) {
        this.message = message;
    }

    /**
     * Constructs a new Message with the specified title, subtitle, and timings.
     *
     * @param title     the title string.
     * @param subTitle  the subtitle string.
     * @param a         the start time in ticks.
     * @param b         the display time in ticks.
     * @param c         the end time in ticks.
     */
    Message(String title, String subTitle, int a, int b, int c) {
        this.use = true;
        this.titles.put("title", title);
        this.titles.put("subtitle", subTitle);
        this.titles.put("start", a);
        this.titles.put("time", b);
        this.titles.put("end", c);
        this.titles.put("isUse", true);
        this.type = MessageType.TITLE;
    }

    /**
     * Constructs a new Message with multiple message strings.
     *
     * @param message the array of message strings.
     */
    Message(String... message) {
        this.messages = Arrays.asList(message);
        this.use = true;
    }

    /**
     * Constructs a new Message with a specific type and multiple message strings.
     *
     * @param type    the type of the message.
     * @param message the array of message strings.
     */
    Message(MessageType type, String... message) {
        this.messages = Arrays.asList(message);
        this.use = true;
        this.type = type;
    }

    /**
     * Constructs a new Message with a specific type and a single message string.
     *
     * @param type    the type of the message.
     * @param message the message string.
     */
    Message(MessageType type, String message) {
        this.message = message;
        this.use = true;
        this.type = type;
    }

    /**
     * Constructs a new Message with a single message string and a use flag.
     *
     * @param message the message string.
     * @param use     the use flag.
     */
    Message(String message, boolean use) {
        this.message = message;
        this.use = use;
    }

    /**
     * Gets the message string.
     *
     * @return the message string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Converts the message to a string.
     *
     * @return the message string.
     */
    public String toMsg() {
        return message;
    }

    /**
     * Gets the message string.
     *
     * @return the message string.
     */
    public String msg() {
        return message;
    }

    /**
     * Checks if the message is in use.
     *
     * @return true if the message is in use, false otherwise.
     */
    public boolean isUse() {
        return use;
    }

    /**
     * Gets the list of messages.
     *
     * @return the list of messages.
     */
    public List<String> getMessages() {
        return messages == null ? Collections.singletonList(message) : messages;
    }

    /**
     * Sets the list of messages.
     *
     * @param messages the list of messages.
     */
    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    /**
     * Checks if the message contains multiple parts.
     *
     * @return true if the message contains multiple parts, false otherwise.
     */
    public boolean isMessage() {
        return messages != null && messages.size() > 1;
    }

    /**
     * Sets the message string.
     *
     * @param message the message string.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the title string.
     *
     * @return the title string.
     */
    public String getTitle() {
        return (String) titles.get("title");
    }

    /**
     * Gets the map of titles.
     *
     * @return the map of titles.
     */
    public Map<String, Object> getTitles() {
        return titles;
    }

    /**
     * Sets the map of titles and changes the message type to TITLE.
     *
     * @param titles the map of titles.
     */
    public void setTitles(Map<String, Object> titles) {
        this.titles = titles;
        this.type = MessageType.TITLE;
    }

    /**
     * Gets the subtitle string.
     *
     * @return the subtitle string.
     */
    public String getSubTitle() {
        return (String) titles.get("subtitle");
    }

    /**
     * Checks if the message has a title.
     *
     * @return true if the message has a title, false otherwise.
     */
    public boolean isTitle() {
        return titles.containsKey("title");
    }

    /**
     * Gets the start time in ticks.
     *
     * @return the start time in ticks.
     */
    public int getStart() {
        return ((Number) titles.get("start")).intValue();
    }

    /**
     * Gets the end time in ticks.
     *
     * @return the end time in ticks.
     */
    public int getEnd() {
        return ((Number) titles.get("end")).intValue();
    }

    /**
     * Gets the display time in ticks.
     *
     * @return the display time in ticks.
     */
    public int getTime() {
        return ((Number) titles.get("time")).intValue();
    }

    /**
     * Checks if the title is in use.
     *
     * @return true if the title is in use, false otherwise.
     */
    public boolean isUseTitle() {
        return (boolean) titles.getOrDefault("isUse", "true");
    }

    /**
     * Replaces a substring in the message with another string.
     *
     * @param a the substring to replace.
     * @param b the replacement string.
     * @return the modified message string.
     */
    public String replace(String a, String b) {
        return message.replace(a, b);
    }

    /**
     * Gets the type of the message.
     *
     * @return the type of the message.
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Sets the type of the message.
     *
     * @param type the type of the message.
     */
    public void setType(MessageType type) {
        this.type = type;
    }

    /**
     * Gets the item stack associated with the message.
     *
     * @return the item stack.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Sets the item stack associated with the message.
     *
     * @param itemStack the item stack.
     */
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

}

