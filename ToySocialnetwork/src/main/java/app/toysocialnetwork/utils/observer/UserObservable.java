package app.toysocialnetwork.utils.observer;

import app.toysocialnetwork.utils.event.UserEvent;

public interface UserObservable {
    void addUserObserver(Observer<UserEvent> observer);
    void removeUserObserver(Observer<UserEvent> observer);
    void notifyUserObservers(UserEvent event);
}
