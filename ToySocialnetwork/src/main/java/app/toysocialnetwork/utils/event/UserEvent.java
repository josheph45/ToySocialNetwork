package app.toysocialnetwork.utils.event;

import app.toysocialnetwork.domain.User;

public class UserEvent implements Event {
    private final EventEnum type;
    private final User newUser;

    /**
     * Constructor for UserEvent
     * @param type the type of event
     * @param newUser the new user
     */
    public UserEvent(EventEnum type, User newUser) {
        this.type = type;
        this.newUser = newUser;
    }

    public EventEnum getType() {
        return type;
    }

    public User getNewUser() {
        return newUser;
    }
}
