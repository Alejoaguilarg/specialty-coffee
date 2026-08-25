package co.com.specialtycoffee.model.gateway;

import co.com.specialtycoffee.model.entity.Order;

public interface OrderRepositoryPort {
    void save(Order order);
}
