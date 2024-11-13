package sk.umb.example.kafka;

public interface MessageConsumerService {
    void registerListener(MessageListener listener);
}