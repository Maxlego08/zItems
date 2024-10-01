package fr.maxlego08.items.zcore.enums;

public enum Permission {

    ZITEMS_USE,
    ZITEMS_EDIT,
    ZITEMS_RELOAD,
    ZITEMS_GIVE,
    ZITEMS_RUNE_APPLY,

    ZITEMS_EDIT_NAME,
    ZITEMS_EDIT_LORE,
    ZITEMS_EDIT_CUSTOM_MODEL_DATA,
    ZITEMS_EDIT_INFO,
    ;

    private final String permission;

    Permission() {
        this.permission = this.name().toLowerCase().replace("_", ".");
    }

    public String getPermission() {
        return permission;
    }

}
