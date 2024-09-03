package fr.maxlego08.items.zcore.utils.plugins;

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
	
	;

	private final String name;

	private Plugins(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
