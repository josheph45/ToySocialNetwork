package app.toysocialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    private Long from;
    private Long to;
    private String text;
    private LocalDateTime date;

    public Message(Long from, Long to, String text, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.date = date;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(from, message.from)
                && Objects.equals(to, message.to)
                && Objects.equals(text, message.text)
                && Objects.equals(date, message.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, text, date);
    }

    @Override
    public String toString() {
        return from + " " + to + " " + text + " " + date;
    }
}
