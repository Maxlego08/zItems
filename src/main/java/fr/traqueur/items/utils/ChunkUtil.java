package fr.traqueur.items.utils;

import org.bukkit.Chunk;

public class ChunkUtil {

    /**
     * Utility Class for chunk key
     */
    private ChunkUtil() {
        //Utility Class
    }

    public static long getChunkKey(Chunk chunk) {
        return (long) chunk.getX() & 0xffffffffL | ((long) chunk.getZ() & 0xffffffffL) << 32;
    }

}
