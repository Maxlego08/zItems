package fr.traqueur.items.registries;

import fr.traqueur.items.ZItems;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.effects.exit.PipelineExit;
import fr.traqueur.items.api.registries.ExitsRegistry;
import fr.traqueur.items.effects.exit.ZExit;
import fr.traqueur.structura.api.Structura;
import fr.traqueur.structura.exceptions.StructuraException;

import java.nio.file.Path;

public class ZExitsRegistry extends FileBasedRegistry<String, PipelineExit> implements ExitsRegistry {

    public ZExitsRegistry(ItemsPlugin plugin) {
        super(plugin, ZItems.EXITS_FOLDER, "Exits Registry");
    }

    @Override
    protected PipelineExit loadFile(Path file) {
        try {
            PipelineExit exit = Structura.load(file, ZExit.class);
            this.register(exit.id(), exit);
            Logger.debug("Loaded exit: " + exit.id() + " from file: " + file.getFileName());
            return exit;
        } catch (StructuraException e) {
            Logger.severe("Failed to load exit from file: " + file.getFileName(), e);
            return null;
        }
    }
}
