package fr.traqueur.items.effects.handlers;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.effects.EffectSettings;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.api.effects.entries.EntrySettings;
import fr.traqueur.items.api.registries.EntryHandlersRegistry;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.effects.entries.PipelineEntry;
import fr.traqueur.items.effects.settings.PipelineSettings;
import fr.traqueur.structura.references.Reference;

/**
 * Groups a set of already-existing effects ({@code steps}) behind a shared trigger
 * condition ({@code entry}).
 * <p>
 * There is no dedicated "exit" step: steps run in declaration order against the
 * same {@link EffectContext}, so the final action on the collected drops (sell,
 * or nothing) is just whichever effect is placed last in {@code steps} — e.g. an
 * existing AUTO_SELL effect.
 * <p>
 * Registered as an {@link EffectHandler.AnyEventEffectHandler} because the entry —
 * not this handler — decides whether the pipeline actually runs for a given event;
 * see {@link EntryHandler}. Each step runs against the very same {@link EffectContext}
 * as this pipeline itself, so it composes transparently with any other effect the
 * item might carry.
 */
@AutoEffect("PIPELINE")
public class PipelineEffectHandler implements EffectHandler.AnyEventEffectHandler<PipelineSettings> {

    @Override
    public void handle(EffectContext context, PipelineSettings settings) {
        PipelineEntry entry = settings.entry();
        EntryHandler<?> entryHandler = Registry.get(EntryHandlersRegistry.class).getById(entry.type());
        if (entryHandler == null || !testEntry(entryHandler, context, entry.settings())) {
            return;
        }

        for (Reference<Effect> stepRef : settings.steps()) {
            Effect step = stepRef.element();
            EffectHandler<?> handler = Registry.get(HandlersRegistry.class).getById(step.type());
            if (handler == null || !handler.canApply(context.event())) {
                continue;
            }
            executeStep(handler, context, step.settings());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends EntrySettings> boolean testEntry(EntryHandler<T> handler, EffectContext context, EntrySettings settings) {
        return handler.test(context, (T) settings);
    }

    @SuppressWarnings("unchecked")
    private <T extends EffectSettings> void executeStep(EffectHandler<T> handler, EffectContext context, EffectSettings settings) {
        handler.handle(context, (T) settings);
    }

    @Override
    public int priority() {
        return 0;
    }
}
