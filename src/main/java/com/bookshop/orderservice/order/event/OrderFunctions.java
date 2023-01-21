package com.bookshop.orderservice.order.event;

import com.bookshop.orderservice.order.domain.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
public class OrderFunctions {

    private static final Logger log = LoggerFactory.getLogger(OrderFunctions.class);

    @Bean
    public Consumer<Flux<OrderDispatchedMessage>> dispatchedOrder(OrderService orderService) {
        return flux -> orderService.consumeOrderDispatchedEvent(flux) // Update the status in the DB to DISPATCHED
                .doOnNext(order -> log.info("Order {} is dispatched", order.id()))
                .subscribe(); // Subscribe to reactive stream in order to activate it.
    }


}
