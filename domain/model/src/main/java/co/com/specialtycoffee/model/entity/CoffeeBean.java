package co.com.specialtycoffee.model.entity;

import co.com.specialtycoffee.model.ex.BusinessException;
import co.com.specialtycoffee.model.ex.InsufficientInventoryException;
import lombok.Getter;

@Getter
public class CoffeeBean {

    private final String name;
    private int availableGrams;

    public CoffeeBean(String name, Integer availableGrams) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Bean cannot be null or blank");
        }
        if (availableGrams < 0) {
            throw new BusinessException("Stock cannot be negative");
        }
        this.name = name;
        this.availableGrams = availableGrams;
    }

    public void discountGrams(Integer grams) {
        if (grams > this.availableGrams) {
            throw new InsufficientInventoryException(this.name, grams, this. availableGrams);
        }
        this.availableGrams -= grams;
    }

    public int getAvailableGrams() {
        return availableGrams;
    }
}
