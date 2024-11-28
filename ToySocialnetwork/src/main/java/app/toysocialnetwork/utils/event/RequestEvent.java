package app.toysocialnetwork.utils.event;

import app.toysocialnetwork.domain.Request;

public class RequestEvent implements Event {
    private final EventEnum type;
    private final Request newRequest;
    private Request oldRequest;

    public RequestEvent(EventEnum type, Request newRequest) {
        this.type = type;
        this.newRequest = newRequest;
    }

    public RequestEvent(EventEnum type, Request newRequest, Request oldRequest) {
        this.type = type;
        this.newRequest = newRequest;
        this.oldRequest = oldRequest;
    }

    public EventEnum getType() {
        return type;
    }

    public Request getNewRequest() {
        return newRequest;
    }

    public Request getOldRequest() {
        return oldRequest;
    }
}
