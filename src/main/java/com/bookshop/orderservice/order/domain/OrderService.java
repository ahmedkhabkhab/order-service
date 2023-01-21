package com.bookshop.orderservice.order.domain;

import com.bookshop.orderservice.book.Book;
import com.bookshop.orderservice.book.BookClient;
import com.bookshop.orderservice.order.event.OrderAcceptedMessage;
import com.bookshop.orderservice.order.event.OrderDispatchedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final BookClient bookClient;
    private final StreamBridge streamBridge;

    public OrderService(OrderRepository orderRepository, BookClient bookClient, StreamBridge streamBridge) {
        this.orderRepository = orderRepository;
        this.bookClient = bookClient;
        this.streamBridge = streamBridge;
    }

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> submitOrder(String isbn, int quantity) {
        return bookClient
                .getBookByIsbn(isbn)
                .map(book -> buildAcceptedOrder(book, quantity))
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save);
    }

    public static Order buildRejectedOrder(String isbn, int quantity) {
        return Order.of(
                isbn,
                null,
                null,
                quantity,
                OrderStatus.REJECTED);
    }

    public static Order buildAcceptedOrder(Book book, int quantity) {
        return Order.of(
                book.isbn(),
                book.title() + "-" + book.author(),
                book.price(),
                quantity,
                OrderStatus.ACCEPTED);
    }

    public void publishOrderAcceptedEvent(Order order) {
        if (order.status() != OrderStatus.ACCEPTED) {
            return;
        }

        var orderAcceptedMessage = new OrderAcceptedMessage(order.id());
        log.info("Sending order accepted event for order with id: {}", order.id());
        // acceptOrder binding is created automatically by Spring cloud stream at startup
        var result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage);
        log.info("Result of sending order accepted event for order with id {} is|: ", order.id(), result);

    }

    public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedMessage> flux) {
        return flux
                .flatMap(msg -> orderRepository.findById(msg.orderId()))
                .map(existingOrder -> new Order(
                        existingOrder.id(),
                        existingOrder.bookIsbn(),
                        existingOrder.bookName(),
                        existingOrder.bookPrice(),
                        existingOrder.quantity(),
                        OrderStatus.DISPATCHED,
                        existingOrder.createdDate(),
                        existingOrder.lastModifiedDate(),
                        existingOrder.version()
                ))
                .flatMap(orderRepository::save);
    }
}
