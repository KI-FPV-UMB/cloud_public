## Application start

- docker-compose -up
- start wholesale application
- start bookstore application

Send new book event:
```
http://localhost:8082/books/new?bookId=123&title=SpringBootInAction&price=29.99
```

Send price update event:
```
http://localhost:8082/books/price?bookId=123&newPrice=24.99
```