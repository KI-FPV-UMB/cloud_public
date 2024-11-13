package sk.umb.example.kafka;

@FunctionalInterface
public interface MessageListener {
    void onMessage(String message);
}