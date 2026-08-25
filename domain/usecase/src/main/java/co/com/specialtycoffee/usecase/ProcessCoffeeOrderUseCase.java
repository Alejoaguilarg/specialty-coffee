package co.com.specialtycoffee.usecase;

import co.com.specialtycoffee.model.entity.CoffeeBean;
import co.com.specialtycoffee.model.entity.Order;
import co.com.specialtycoffee.model.ex.BeanNotFoundException;
import co.com.specialtycoffee.model.gateway.InventoryPort;
import co.com.specialtycoffee.model.gateway.OrderRepositoryPort;

public class ProcessCoffeeOrderUseCase {

    private final InventoryPort inventoryPort;
    private final OrderRepositoryPort orderRepository;

    public ProcessCoffeeOrderUseCase(InventoryPort inventoryPort, OrderRepositoryPort orderRepository) {
        this.inventoryPort = inventoryPort;
        this.orderRepository = orderRepository;
    }

    public Order execute(String beanName, int quantityGrams, String brewMethod) {
        CoffeeBean bean = inventoryPort.findByName(beanName)
                .orElseThrow(() -> new BeanNotFoundException(beanName));

        bean.discountGrams(quantityGrams);
        inventoryPort.updateStock(bean);

        Order order = Order.confirmed(beanName, quantityGrams, brewMethod);
        orderRepository.save(order);

        return order;
    }
}
