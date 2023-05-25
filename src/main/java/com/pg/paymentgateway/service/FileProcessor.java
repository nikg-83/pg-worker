package com.pg.paymentgateway.service;

import java.util.ArrayList;
import java.util.List;

public interface FileProcessor {
    public void processMessage(String jsonString);

    List<FileEventListener> listeners = new ArrayList<>();

    public default void registerListener(FileEventListener listener) {
        listeners.add(listener);
    }

    public default void unregisterListener(FileEventListener listener) {
        listeners.remove(listener);
    }

    public default void onFileComplete(FileEvent event) {
        for (FileEventListener listener : listeners) {
            listener.handleEvent(event);
        }
    }
}
