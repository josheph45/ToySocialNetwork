package app.toysocialnetwork.utils.observer;

import app.toysocialnetwork.utils.event.RequestEvent;

public interface RequestObservable {
    void addRequestObserver(Observer<RequestEvent> observer);
    void removeRequestObserver(Observer<RequestEvent> observer);
    void notifyRequestObservers(RequestEvent event);
}
