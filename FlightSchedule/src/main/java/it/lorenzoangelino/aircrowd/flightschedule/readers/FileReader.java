package it.lorenzoangelino.aircrowd.flightschedule.readers;

import java.util.List;

public interface FileReader<T> {
    List<T> read(String path);
}
