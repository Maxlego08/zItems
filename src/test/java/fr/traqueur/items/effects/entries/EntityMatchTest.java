package fr.traqueur.items.effects.entries;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.entities.CustomEntityProvider;
import fr.traqueur.items.api.registries.CustomEntityProviderRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.registries.ZCustomEntityProviderRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityMatchTest {

    private ServerMock server;
    private WorldMock world;

    @BeforeEach
    void setUp() {
        Logger.init(LoggerFactory.getLogger(EntityMatchTest.class), false);
        server = MockBukkit.mock();
        world = new WorldMock();
        server.addWorld(world);
        Registry.register(CustomEntityProviderRegistry.class, new ZCustomEntityProviderRegistry());
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void matchesVanillaEntityTypeCaseInsensitive() {
        Pig pig = world.spawn(new Location(world, 0, 0, 0), Pig.class);

        assertTrue(new EntityMatch("pig").matches(pig));
        assertTrue(new EntityMatch("PIG").matches(pig));
    }

    @Test
    void doesNotMatchDifferentVanillaEntityType() {
        Zombie zombie = world.spawn(new Location(world, 0, 0, 0), Zombie.class);

        assertFalse(new EntityMatch("PIG").matches(zombie));
    }

    @Test
    void unknownVanillaNameNeverMatches() {
        Pig pig = world.spawn(new Location(world, 0, 0, 0), Pig.class);

        assertFalse(new EntityMatch("NOT_A_REAL_ENTITY_TYPE").matches(pig));
    }

    @Test
    void matchesCustomMobThroughRegisteredProvider() {
        Zombie zombie = world.spawn(new Location(world, 0, 0, 0), Zombie.class);
        Registry.get(CustomEntityProviderRegistry.class).register("mythicmobs", (CustomEntityProvider) entity ->
                entity.equals(zombie) ? Optional.of("my_boss") : Optional.empty());

        assertTrue(new EntityMatch("mythicmobs:my_boss").matches(zombie));
        assertFalse(new EntityMatch("mythicmobs:other_boss").matches(zombie));
    }

    @Test
    void unknownProviderNeverMatches() {
        Pig pig = world.spawn(new Location(world, 0, 0, 0), Pig.class);
        assertFalse(new EntityMatch("mythicmobs:my_boss").matches(pig));
    }
}
