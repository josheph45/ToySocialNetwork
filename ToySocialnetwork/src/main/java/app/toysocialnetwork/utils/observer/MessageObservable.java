package app.toysocialnetwork.utils.observer;

import app.toysocialnetwork.utils.event.MessageEvent;

public interface MessageObservable {
    void addMessageObserver(Observer<MessageEvent> observer);
    void removeMessageObserver(Observer<MessageEvent> observer);
    void notifyMessageObservers(MessageEvent event);
}
