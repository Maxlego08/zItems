package fr.traqueur.items.effects.pipeline.entries.blocks;

import fr.traqueur.structura.api.Structura;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MiningEntrySettingsParsingTest {

    @Test
    void parsesVanillaAndCustomBlockReferences() {
        String yaml = """
                materials:
                  - block-id: "DIAMOND_ORE"
                  - plugin-name: "itemsadder"
                    block-id: "ruby_ore"
                """;

        MiningEntrySettings settings = Structura.parse(yaml, MiningEntrySettings.class);

        assertEquals(2, settings.materials().size());
        assertNull(settings.materials().get(0).pluginName());
        assertEquals("DIAMOND_ORE", settings.materials().get(0).blockId());
        assertEquals("itemsadder", settings.materials().get(1).pluginName());
        assertEquals("ruby_ore", settings.materials().get(1).blockId());
    }

    @Test
    void materialsIsAbsentByDefault() {
        MiningEntrySettings settings = Structura.parse("{}", MiningEntrySettings.class);

        assertNull(settings.materials());
    }
}
