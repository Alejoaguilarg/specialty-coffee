package co.com.specialtycoffee.model.gateway;

import co.com.specialtycoffee.model.entity.CoffeeBean;

import java.util.Optional;

public interface InventoryPort {

    Optional<CoffeeBean> findByName(String beanName);

    void updateStock(CoffeeBean bean);
}
