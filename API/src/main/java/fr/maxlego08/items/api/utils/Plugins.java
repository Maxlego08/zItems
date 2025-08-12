package fr.maxlego08.items.api.utils;

import org.bukkit.Bukkit;

public enum Plugins {

    VAULT("Vault"),
    ESSENTIALS("Essentials"),
    HEADDATABASE("HeadDatabase"),
    PLACEHOLDER("PlaceholderAPI"),
    CITIZENS("Citizens"),
    TRANSLATIONAPI("TranslationAPI"),
    ZTRANSLATOR("zTranslator"),
    WORLDGUARD("WorldGuard"),
    JOBS("Jobs"),
    ZJOBS("zJobs"),
    ZMENU("zMenu"),
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
