package fr.traqueur.items.effects.entries.settings;

import fr.traqueur.structura.api.Structura;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class KillEntrySettingsParsingTest {

    @Test
    void parsesVanillaAndCustomEntityReferences() {
        String yaml = """
                entities:
                  - entity: "PIG"
                  - entity: "mythicmobs:my_boss"
                """;

        KillEntrySettings settings = Structura.parse(yaml, KillEntrySettings.class);

        assertEquals(2, settings.entities().size());
        assertEquals("PIG", settings.entities().get(0).entity());
        assertEquals("mythicmobs:my_boss", settings.entities().get(1).entity());
    }

    @Test
    void entitiesIsAbsentByDefault() {
        KillEntrySettings settings = Structura.parse("{}", KillEntrySettings.class);

        assertNull(settings.entities());
    }
}
