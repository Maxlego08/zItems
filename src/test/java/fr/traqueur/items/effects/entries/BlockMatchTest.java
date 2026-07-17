package fr.traqueur.items.effects.entries;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.blocks.CustomBlockProvider;
import fr.traqueur.items.api.registries.CustomBlockProviderRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.registries.ZCustomBlockProviderRegistry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockMatchTest {

    private ServerMock server;
    private WorldMock world;

    @BeforeEach
    void setUp() {
        Logger.init(LoggerFactory.getLogger(BlockMatchTest.class), false);
        server = MockBukkit.mock();
        world = new WorldMock(Material.AIR, 3);
        server.addWorld(world);
        Registry.register(CustomBlockProviderRegistry.class, new ZCustomBlockProviderRegistry());
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void matchesVanillaMaterialCaseInsensitive() {
        Block block = world.getBlockAt(0, 0, 0);
        block.setType(Material.DIAMOND_ORE);

        assertTrue(new BlockMatch("diamond_ore").matches(block));
        assertTrue(new BlockMatch("DIAMOND_ORE").matches(block));
    }

    @Test
    void doesNotMatchDifferentVanillaMaterial() {
        Block block = world.getBlockAt(0, 0, 0);
        block.setType(Material.STONE);

        assertFalse(new BlockMatch("DIAMOND_ORE").matches(block));
    }

    @Test
    void unknownVanillaNameNeverMatches() {
        Block block = world.getBlockAt(0, 0, 0);
        block.setType(Material.STONE);

        assertFalse(new BlockMatch("NOT_A_REAL_MATERIAL").matches(block));
    }

    @Test
    void matchesCustomBlockThroughRegisteredProvider() {
        Block block = world.getBlockAt(0, 0, 0);
        Registry.get(CustomBlockProviderRegistry.class).register("itemsadder", new CustomBlockProvider() {
            @Override
            public Optional<List<ItemStack>> getCustomBlockDrop(Block b, Player player) {
                return Optional.empty();
            }

            @Override
            public Optional<String> getCustomBlockId(Block b) {
                return b.equals(block) ? Optional.of("ruby_ore") : Optional.empty();
            }

            @Override
            public void placeCustomBlock(String itemId, Block b) {
            }
        });

        assertTrue(new BlockMatch("itemsadder:ruby_ore").matches(block));
        assertFalse(new BlockMatch("itemsadder:other_block").matches(block));
    }

    @Test
    void unknownProviderNeverMatches() {
        Block block = world.getBlockAt(0, 0, 0);
        assertFalse(new BlockMatch("nexo:some_block").matches(block));
    }
}
