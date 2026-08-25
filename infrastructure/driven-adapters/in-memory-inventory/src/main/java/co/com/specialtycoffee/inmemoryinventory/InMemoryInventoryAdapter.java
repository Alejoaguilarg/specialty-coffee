package co.com.specialtycoffee.inmemoryinventory;

import co.com.specialtycoffee.model.entity.CoffeeBean;
import co.com.specialtycoffee.model.gateway.InventoryPort;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryInventoryAdapter implements InventoryPort {

    private final Map<String, CoffeeBean> storage = new HashMap<>();

    public InMemoryInventoryAdapter() {
        storage.put("Cappuccino", new CoffeeBean("Cappuccino", 100));
        storage.put("Latte", new CoffeeBean("Latte", 100));
        storage.put("Espresso", new CoffeeBean("Espresso", 100));
        storage.put("Americano", new CoffeeBean("Americano", 100));
        storage.put("Mocha", new CoffeeBean("Mocha", 100));
        storage.put("Geisha", new CoffeeBean("Geisha", 500));
        storage.put("Bourbon Rosado", new CoffeeBean("Bourbon Rosado", 200));
    }

    @Override
    public Optional<CoffeeBean> findByName(String beanName) {
        return Optional.ofNullable(storage.get(beanName));
    }

    @Override
    public void updateStock(CoffeeBean bean) {
        storage.put(bean.getName(), bean);
    }
}
