package fr.traqueur.items.exit;

import fr.traqueur.items.api.exit.ExitSettings;
import fr.traqueur.items.api.exit.PipelineExit;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.api.Loadable;

public record ZExit(String id, String type,
                     @Options(inline = true) ExitSettings settings) implements PipelineExit, Loadable {
}
