package fr.maxlego08.items.api.runes.applicators;

import java.util.List;

public enum ApplicatorType {

    SMITHING_TABLE("template"),
    ZITEMS_APPLICATOR("inputs", "extra-inputs"),
    ;

    private List<String> parameters;

    ApplicatorType(String... parameters) {
        this.parameters = List.of(parameters);
    }

    public List<String> getParameters() {
        return parameters;
    }
}
