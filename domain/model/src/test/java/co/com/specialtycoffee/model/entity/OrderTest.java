package co.com.specialtycoffee.model.entity;

import co.com.specialtycoffee.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {

    public static final String BEAN_NAME_STRING = "marulo";
    public static final int QUANTITY_GRAMS = 100;

    @Test
    void shouldCreateConfirmedOrderWithValidaData(){
        //given
        Order order = Order.confirmed(BEAN_NAME_STRING, QUANTITY_GRAMS, "toast");

        //when && assert
        assertEquals(BEAN_NAME_STRING, order.getBeanName());
        assertEquals(QUANTITY_GRAMS, order.getQuantityGrams());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }
}
