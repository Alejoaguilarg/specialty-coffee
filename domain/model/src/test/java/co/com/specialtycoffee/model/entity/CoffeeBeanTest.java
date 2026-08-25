package co.com.specialtycoffee.model.entity;

import co.com.specialtycoffee.model.ex.BusinessException;
import co.com.specialtycoffee.model.ex.InsufficientInventoryException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CoffeeBeanTest {

    public static final String CAPPUCCINO_NAME_STRING = "Cappuccino";
    public static final int GRAMS_150 = 150;
    public static final int GRAMS_50 = 50;
    public static final int GRAMS_100 = 100;

    @Test
    void shouldCreateCoffeBeanWhitValidData() {
        //given
        CoffeeBean coffeeBean = new CoffeeBean(CAPPUCCINO_NAME_STRING, GRAMS_100);

        //assert
        assertEquals(CAPPUCCINO_NAME_STRING, coffeeBean.getName());
        assertEquals(GRAMS_100, coffeeBean.getAvailableGrams());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        //given && assert
        assertThrows(BusinessException.class,
                () -> new CoffeeBean(" ", GRAMS_100));
    }

    @Test
    void shouldThrowExceptionWhenStockIsNegative() {
        // given && assert
        assertThrows(BusinessException.class,
                () -> new CoffeeBean(CAPPUCCINO_NAME_STRING, -1));
    }

    @Test
    void shouldDeductQuantityWhenStockIsSufficient() {
        //given
        CoffeeBean coffeeBean = new CoffeeBean(CAPPUCCINO_NAME_STRING, GRAMS_100);

        //when
        coffeeBean.discountGrams(GRAMS_50);

        //assert
        assertEquals(GRAMS_50, coffeeBean.getAvailableGrams());
        assertEquals(CAPPUCCINO_NAME_STRING, coffeeBean.getName());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNotEnogh() {
        // given && assert
        assertThrows(InsufficientInventoryException.class,
                () -> new CoffeeBean(CAPPUCCINO_NAME_STRING, GRAMS_100)
                        .discountGrams(GRAMS_150));
    }

    @Test
    void shouldNotDeductQuantityWhenQuantityFails() {
        //given
        CoffeeBean coffeeBean = new CoffeeBean(CAPPUCCINO_NAME_STRING, GRAMS_100);

        //when && assert
        assertThrows(InsufficientInventoryException.class,
                () -> coffeeBean.discountGrams(GRAMS_150));

        assertEquals(GRAMS_100, coffeeBean.getAvailableGrams());
    }
}
