package app.toysocialnetwork.utils.observer;

import app.toysocialnetwork.utils.event.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> observer);
    void removeObserver(Observer<E> observer);
    void notifyObservers(E event);
}
