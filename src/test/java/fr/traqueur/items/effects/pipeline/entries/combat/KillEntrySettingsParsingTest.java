package fr.traqueur.items.effects.pipeline.entries.combat;

import fr.traqueur.structura.api.Structura;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class KillEntrySettingsParsingTest {

    @Test
    void parsesVanillaAndCustomEntityReferences() {
        String yaml = """
                entities:
                  - entity-id: "PIG"
                  - plugin-name: "mythicmobs"
                    entity-id: "my_boss"
                """;

        KillEntrySettings settings = Structura.parse(yaml, KillEntrySettings.class);

        assertEquals(2, settings.entities().size());
        assertNull(settings.entities().get(0).pluginName());
        assertEquals("PIG", settings.entities().get(0).entityId());
        assertEquals("mythicmobs", settings.entities().get(1).pluginName());
        assertEquals("my_boss", settings.entities().get(1).entityId());
    }

    @Test
    void entitiesIsAbsentByDefault() {
        KillEntrySettings settings = Structura.parse("{}", KillEntrySettings.class);

        assertNull(settings.entities());
    }
}
