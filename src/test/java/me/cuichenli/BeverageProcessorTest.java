package me.cuichenli;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.awaitility.Awaitility.await;


@QuarkusTest
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
class BaristaTest {

    @Inject
    InMemoryConnector connector;

    @Test
    void testProcessOrder() {
        InMemorySource<Order> ordersIn = connector.source("orders");
        InMemorySink<Beverage> beveragesOut = connector.sink("beverages");

        Order order = new Order();
        order.setProduct("coffee");
        order.setName("Coffee lover");
        order.setOrderId("1234");

        ordersIn.send(order);

        await().<List<? extends Message<Beverage>>>until(beveragesOut::received, t -> t.size() == 1);

        Beverage queuedBeverage = beveragesOut.received().get(0).getPayload();
    }

}