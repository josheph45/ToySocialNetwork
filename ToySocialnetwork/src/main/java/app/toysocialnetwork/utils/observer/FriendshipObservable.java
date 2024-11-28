package app.toysocialnetwork.utils.observer;

import app.toysocialnetwork.utils.event.FriendshipEvent;

public interface FriendshipObservable {
    void addFriendshipObserver(Observer<FriendshipEvent> observer);
    void removeFriendshipObserver(Observer<FriendshipEvent> observer);
    void notifyFriendshipObservers(FriendshipEvent event);
}
