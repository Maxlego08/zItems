package fr.maxlego08.items.zcore.enums;

public enum Permission {

    ZITEMS_USE, ZITEMS_RELOAD,

    ;

    private final String permission;

    Permission() {
        this.permission = this.name().toLowerCase().replace("_", ".");
    }

    public String getPermission() {
        return permission;
    }

}
