package fr.traqueur.items.exit.handlers;

import fr.traqueur.items.api.annotations.AutoExit;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.exit.ExitHandler;
import fr.traqueur.items.exit.settings.NoneExitSettings;

/**
 * Leaves the pipeline's collected drops untouched (vanilla behaviour).
 */
@AutoExit("NONE")
public class NoneExitHandler implements ExitHandler<NoneExitSettings> {

    @Override
    public void resolve(EffectContext context, NoneExitSettings settings) {
        // Intentionally does nothing.
    }
}
