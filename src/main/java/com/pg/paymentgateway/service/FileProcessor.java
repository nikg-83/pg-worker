package com.pg.paymentgateway.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface FileProcessor {
    public void processMessage(String jsonString);

    List<FileEventListener> listeners = new CopyOnWriteArrayList<>();

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
