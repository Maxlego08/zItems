package fr.traqueur.items.effects.exit;

import fr.traqueur.items.api.effects.exit.ExitSettings;
import fr.traqueur.items.api.effects.exit.PipelineExit;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.api.Loadable;

public record ZExit(String id, String type,
                     @Options(inline = true) ExitSettings settings) implements PipelineExit, Loadable {
}
