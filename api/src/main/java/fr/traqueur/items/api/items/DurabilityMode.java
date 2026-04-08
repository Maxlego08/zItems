package fr.traqueur.items.api.items;

/**
 * Defines how an item's durability is tracked and displayed.
 */
public enum DurabilityMode {

    /**
     * Vanilla behavior: Minecraft durability bar is visible and item uses {@code Damageable.setDamage()}.
     * Item breaks when MC damage reaches maxDurability.
     */
    VANILLA,

    /**
     * Custom behavior: item is set as MC-unbreakable (no durability bar shown).
     * Durability is tracked via PersistentDataContainer.
     * Item is removed from inventory when PDC durability reaches 0.
     */
    CUSTOM
}
