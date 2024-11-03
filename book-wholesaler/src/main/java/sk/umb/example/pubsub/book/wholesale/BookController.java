package sk.umb.example.pubsub.book.wholesale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookEventPublisher bookEventPublisher;

    @Autowired
    public BookController(BookEventPublisher bookEventPublisher) {
        this.bookEventPublisher = bookEventPublisher;
    }

    @PostMapping("/new")
    public String newBook(@RequestParam String bookId, @RequestParam String title, @RequestParam double price) {
        bookEventPublisher.publishNewBookEvent(bookId, title, price);
        return "New book event published!";
    }

    @PostMapping("/price")
    public String changePrice(@RequestParam String bookId, @RequestParam double newPrice) {
        bookEventPublisher.publishPriceChangeEvent(bookId, newPrice);
        return "Price change event published!";
    }
}
