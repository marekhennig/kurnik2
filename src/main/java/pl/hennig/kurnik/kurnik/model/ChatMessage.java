package pl.hennig.kurnik.kurnik.model;

public class ChatMessage {
    private String from;
    private String message;
    public ChatMessage(String from, String message) {
        this.from = from;
        this.message = message;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return "ChatMessage{" +
                "from='" + from + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}