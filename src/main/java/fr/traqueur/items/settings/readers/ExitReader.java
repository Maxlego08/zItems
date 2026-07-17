package fr.traqueur.items.settings.readers;

import fr.traqueur.items.api.effects.exit.PipelineExit;
import fr.traqueur.items.api.registries.ExitsRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;

public class ExitReader implements Reader<PipelineExit> {
    @Override
    public PipelineExit read(String s) throws StructuraException {
        PipelineExit exit = Registry.get(ExitsRegistry.class).getById(s);
        if (exit == null) {
            throw new StructuraException("Exit " + s + " not found in ExitsRegistry");
        }
        return exit;
    }
}
