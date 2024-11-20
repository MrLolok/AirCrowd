package it.lorenzoangelino.aircrowd.common.models.locations;

import it.lorenzoangelino.aircrowd.common.models.IdentifiableModel;

public record GeographicalLocation(String name, double latitude, double longitude) implements IdentifiableModel<String> {
    @Override
    public String getId() {
        return this.name;
    }
}
