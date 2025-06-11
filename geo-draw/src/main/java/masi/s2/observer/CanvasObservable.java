package masi.s2.observer;

import java.util.ArrayList;
import java.util.List;

public class CanvasObservable {
    private final List<CanvasObserver> observers = new ArrayList<>();

    public void addObserver(CanvasObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(CanvasObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(CanvasEvent event) {
        for (CanvasObserver observer : observers) {
            observer.onCanvasEvent(event);
        }
    }
} 