package app.toysocialnetwork.utils.event;

import app.toysocialnetwork.domain.Friendship;

public class FriendshipEvent implements Event {
    private final EventEnum type;
    private final Friendship newFriendship;
    private Friendship oldFriendship;

    public FriendshipEvent(EventEnum type, Friendship newFriendship) {
        this.type = type;
        this.newFriendship = newFriendship;
    }

    public FriendshipEvent(EventEnum type, Friendship newFriendship, Friendship oldFriendship) {
        this.type = type;
        this.newFriendship = newFriendship;
        this.oldFriendship = oldFriendship;
    }

    public EventEnum getType() {
        return type;
    }

    public Friendship getNewFriendship() {
        return newFriendship;
    }

    public Friendship getOldFriendship() {
        return oldFriendship;
    }
}
