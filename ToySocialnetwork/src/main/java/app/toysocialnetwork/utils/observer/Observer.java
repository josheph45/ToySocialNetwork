package app.toysocialnetwork.utils.observer;

import app.toysocialnetwork.utils.event.Event;

public interface Observer<E extends Event> {
    void update(E event);
}
