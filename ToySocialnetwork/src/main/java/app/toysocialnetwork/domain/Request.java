package app.toysocialnetwork.domain;

public class Request extends Entity<Long> {
    private final Long senderId;
    private final Long receiverId;

    public Request(Long senderId, Long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    @Override
    public String toString() {
        return senderId + " " + receiverId;
    }
}