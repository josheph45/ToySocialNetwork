package app.toysocialnetwork.utils.event;

import app.toysocialnetwork.domain.User;

public class UserEvent implements Event {
    private final EventEnum type;
    private final User newUser;
    private User oldUser;

    public UserEvent(EventEnum type, User newUser) {
        this.type = type;
        this.newUser = newUser;
    }

    public UserEvent(EventEnum type, User newUser, User oldUser) {
        this.type = type;
        this.newUser = newUser;
        this.oldUser = oldUser;
    }

    public EventEnum getType() {
        return type;
    }

    public User getNewUser() {
        return newUser;
    }

    public User getOldUser() {
        return oldUser;
    }
}
