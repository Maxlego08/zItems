package fr.traqueur.items.effects.entries.settings;

import fr.traqueur.structura.api.Structura;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MiningEntrySettingsParsingTest {

    @Test
    void parsesVanillaAndCustomBlockReferences() {
        String yaml = """
                materials:
                  - block: "DIAMOND_ORE"
                  - block: "itemsadder:ruby_ore"
                """;

        MiningEntrySettings settings = Structura.parse(yaml, MiningEntrySettings.class);

        assertEquals(2, settings.materials().size());
        assertEquals("DIAMOND_ORE", settings.materials().get(0).block());
        assertEquals("itemsadder:ruby_ore", settings.materials().get(1).block());
    }

    @Test
    void materialsIsAbsentByDefault() {
        MiningEntrySettings settings = Structura.parse("{}", MiningEntrySettings.class);

        assertNull(settings.materials());
    }
}
