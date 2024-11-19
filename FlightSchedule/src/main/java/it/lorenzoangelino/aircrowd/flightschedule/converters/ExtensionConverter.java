package it.lorenzoangelino.aircrowd.flightschedule.converters;

import java.io.File;
import java.util.Optional;

public interface ExtensionConverter {
    Optional<File> convert(String from, String to);
}
