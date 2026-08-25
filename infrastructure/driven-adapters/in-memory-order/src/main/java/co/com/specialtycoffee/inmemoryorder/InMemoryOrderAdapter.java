package co.com.specialtycoffee.inmemoryorder;

import co.com.specialtycoffee.model.entity.Order;
import co.com.specialtycoffee.model.gateway.OrderRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryOrderAdapter implements OrderRepositoryPort {

    private final List<Order> storage = new ArrayList<>();

    @Override
    public void save(Order order) {
        storage.add(order);
    }
}
