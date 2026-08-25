package co.com.specialtycoffee.model.entity;

import co.com.specialtycoffee.model.enums.OrderStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Order {

    private final String id;
    private final String beanName;
    private final int quantityGrams;
    private final String brewMethod;
    private final OrderStatus status;

    private Order(String id, String beanName, int quantityGrams, String brewMethod, OrderStatus status) {
        this.id = id;
        this.beanName = beanName;
        this.quantityGrams = quantityGrams;
        this.brewMethod = brewMethod;
        this.status = status;
    }

    public static Order confirmed(String beanName, int quantityGrams, String brewMethod) {
        return new Order(
                UUID.randomUUID().toString(),
                beanName,
                quantityGrams,
                brewMethod,
                OrderStatus.CONFIRMED
        );
    }
}
