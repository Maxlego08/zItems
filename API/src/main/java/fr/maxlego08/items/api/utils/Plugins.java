package fr.maxlego08.items.api.utils;

import org.bukkit.Bukkit;

public enum Plugins {

    WORLDGUARD("WorldGuard"),
    JOBS("Jobs"),
    ZJOBS("zJobs"),
    ZESSENTIALS("zEssentials"),
    ITEMSADDER("ItemsAdder"),
    ZSHOP("zShop"),
    SHOPGUIPLUS("ShopGUIPlus"),
    ECONOMYSHPOGUI("EconomyShopGUI"),
    SUPERIORSKYBLOCK2("SuperiorSkyblock2"),
    ;

    private final String name;

    Plugins(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

}
