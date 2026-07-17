package fr.traqueur.items.entries;

import fr.traqueur.items.api.entries.Entry;
import fr.traqueur.items.api.entries.EntrySettings;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.api.Loadable;

public record ZEntry(String id, String type,
                      @Options(inline = true) EntrySettings settings) implements Entry, Loadable {
}
