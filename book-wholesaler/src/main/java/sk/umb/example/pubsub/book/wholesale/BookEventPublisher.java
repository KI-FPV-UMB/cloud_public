package sk.umb.example.pubsub.book.wholesale;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public BookEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishNewBookEvent(String bookId, String title, double price) {
        Map<String, Object> bookEvent = new HashMap<>();
        bookEvent.put("type", "NEW_BOOK");
        bookEvent.put("bookId", bookId);
        bookEvent.put("title", title);
        bookEvent.put("price", price);

        rabbitTemplate.convertAndSend("bookExchange", "book.new", bookEvent);
    }

    public void publishPriceChangeEvent(String bookId, double newPrice) {
        Map<String, Object> priceEvent = new HashMap<>();
        priceEvent.put("type", "PRICE_CHANGE");
        priceEvent.put("bookId", bookId);
        priceEvent.put("newPrice", newPrice);

        rabbitTemplate.convertAndSend("bookExchange", "book.price.change", priceEvent);
    }
}
