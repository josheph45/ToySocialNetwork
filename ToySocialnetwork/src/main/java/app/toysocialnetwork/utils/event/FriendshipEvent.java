package app.toysocialnetwork.utils.event;

import app.toysocialnetwork.domain.Friendship;

public class FriendshipEvent implements Event {
    private final EventEnum type;
    private final Friendship newFriendship;

    /**
     * Constructor for FriendshipEvent
     * @param type the type of event
     * @param newFriendship the new friendship
     */
    public FriendshipEvent(EventEnum type, Friendship newFriendship) {
        this.type = type;
        this.newFriendship = newFriendship;
    }

    public EventEnum getType() {
        return type;
    }

    public Friendship getNewFriendship() {
        return newFriendship;
    }
}
