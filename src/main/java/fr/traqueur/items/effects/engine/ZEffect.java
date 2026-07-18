package fr.traqueur.items.effects.engine;

import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.effects.EffectRepresentation;
import fr.traqueur.items.api.effects.EffectSettings;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.api.Loadable;
import net.kyori.adventure.text.Component;

public record ZEffect(String id, String type,
                      @Options(optional = true) EffectRepresentation representation,
                      @Options(inline = true) EffectSettings settings,
                      @Options(optional = true) Component displayName) implements Effect, Loadable {
}
