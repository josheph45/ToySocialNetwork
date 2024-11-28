package app.toysocialnetwork.domain;

import java.time.LocalDateTime;

public class Friendship extends Entity<Long> {
    private Long user1Id;
    private Long user2Id;
    private LocalDateTime friendsFrom;

    public Friendship(Long user1Id, Long user2Id, LocalDateTime friendsFrom) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.friendsFrom = friendsFrom;
    }

    public Long getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }

    public Long getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    @Override
    public String toString() {
        return user1Id + " " + user2Id + " " + friendsFrom;
    }
}
