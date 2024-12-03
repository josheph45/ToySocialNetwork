package app.toysocialnetwork.utils.event;

import app.toysocialnetwork.domain.Request;

public class RequestEvent implements Event {
    private final EventEnum type;
    private final Request newRequest;

    /**
     * Constructor for RequestEvent
     * @param type the type of event
     * @param newRequest the new request
     */
    public RequestEvent(EventEnum type, Request newRequest) {
        this.type = type;
        this.newRequest = newRequest;
    }

    public EventEnum getType() {
        return type;
    }

    public Request getNewRequest() {
        return newRequest;
    }
}
