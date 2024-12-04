package app.toysocialnetwork.utils.event;

import app.toysocialnetwork.domain.Message;

public class MessageEvent implements Event {
    private final EventEnum type;
    private final Message newMessage;

    /**
     * Constructor for MessageEvent
     * @param type the type of event
     * @param newMessage the new message
     */
    public MessageEvent(EventEnum type, Message newMessage) {
        this.type = type;
        this.newMessage = newMessage;
    }

    public EventEnum getType() {
        return type;
    }

    public Message getNewMessage() {
        return newMessage;
    }
}
