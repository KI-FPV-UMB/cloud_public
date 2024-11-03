package sk.umb.example.pubsub.book.store;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BookEventSubscriber {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public BookEventSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "bookQueue")
    public void handleBookEvent(Map<String, Object> event) {
        messagingTemplate.convertAndSend("/topic/bookUpdates", event);
    }
}
