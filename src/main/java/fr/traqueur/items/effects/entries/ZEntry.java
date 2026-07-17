package fr.traqueur.items.effects.entries;

import fr.traqueur.items.api.effects.entries.Entry;
import fr.traqueur.items.api.effects.entries.EntrySettings;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.api.Loadable;

public record ZEntry(String id, String type,
                      @Options(inline = true) EntrySettings settings) implements Entry, Loadable {
}
