package co.com.specialtycoffee.usecase;

import co.com.specialtycoffee.model.entity.CoffeeBean;
import co.com.specialtycoffee.model.entity.Order;
import co.com.specialtycoffee.model.enums.OrderStatus;
import co.com.specialtycoffee.model.ex.BeanNotFoundException;
import co.com.specialtycoffee.model.ex.InsufficientInventoryException;
import co.com.specialtycoffee.model.gateway.InventoryPort;
import co.com.specialtycoffee.model.gateway.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessCoffeeOrderUseCaseTest {

    public static final String CAPPUCCINO_BEAN = "Cappuccino";
    public static final int AVAILABLE_GRAMS = 100;
    public static final String BREW_METHOD = "V60";
    @Mock
    private InventoryPort inventoryPort;

    @Mock
    private OrderRepositoryPort orderRepository;

    private ProcessCoffeeOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProcessCoffeeOrderUseCase(inventoryPort, orderRepository);
    }

    @Test
    void shouldConfirmOrderWhenStockIsSufficient() {
        //given
        CoffeeBean bean = new CoffeeBean(CAPPUCCINO_BEAN, AVAILABLE_GRAMS);
        when(inventoryPort.findByName(bean.getName())).thenReturn(Optional.of(bean));

        //when
        Order order = useCase.execute(CAPPUCCINO_BEAN, AVAILABLE_GRAMS, BREW_METHOD);

        //assert
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        verify(inventoryPort).findByName(anyString());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowBeanNotFoundExceptionWhenBeanDoesNotExist() {
        //given
        when(inventoryPort.findByName(anyString())).thenReturn(Optional.empty());

        //when && assert
        assertThrows(BeanNotFoundException.class, () -> useCase.execute(CAPPUCCINO_BEAN, AVAILABLE_GRAMS, BREW_METHOD));
        verify(orderRepository, never()).save(any(Order.class));
    }
    @Test
    void shouldThrowInsufficientInventoryExceptionWhenStockIsNotEnough() {
        //given
        CoffeeBean bean = new CoffeeBean(CAPPUCCINO_BEAN, AVAILABLE_GRAMS);
        when(inventoryPort.findByName(bean.getName())).thenReturn(Optional.of(bean));

        //when
        assertThrows(InsufficientInventoryException.class,
                () -> useCase.execute(bean.getName(), AVAILABLE_GRAMS + 1, BREW_METHOD));

        verify(inventoryPort, times(1)).findByName(anyString());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldDeductStockAfterSuccessfulOrder() {
        //given
        CoffeeBean bean = new CoffeeBean(CAPPUCCINO_BEAN, AVAILABLE_GRAMS);
        when(inventoryPort.findByName(bean.getName())).thenReturn(Optional.of(bean));

        //when
        useCase.execute(bean.getName(), AVAILABLE_GRAMS, BREW_METHOD);

        //assert
        verify(inventoryPort, times(1)).updateStock(bean);
        verify(orderRepository, times(1)).save(any(Order.class));
        assertNotEquals(AVAILABLE_GRAMS, bean.getAvailableGrams());
        assertEquals(0, bean.getAvailableGrams());
    }

    @Test
    void shouldNotSaveOrderWhenInventoryIsInsufficient(){
        //given
        CoffeeBean bean = new CoffeeBean(CAPPUCCINO_BEAN, AVAILABLE_GRAMS);
        when(inventoryPort.findByName(bean.getName())).thenReturn(Optional.of(bean));

        //when && assert
        assertThrows(InsufficientInventoryException.class, () -> useCase.execute(bean.getName(), AVAILABLE_GRAMS + 1, BREW_METHOD));
        verify(inventoryPort, never()).updateStock(bean);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldNotSaveOrderWhenBeanNotFound(){
        //given
        when(inventoryPort.findByName(anyString())).thenReturn(Optional.empty());

        //when && assert
        assertThrows(BeanNotFoundException.class, () -> useCase.execute(CAPPUCCINO_BEAN, AVAILABLE_GRAMS, BREW_METHOD));
        verify(inventoryPort, atLeast(1)).findByName(anyString());
        verify(inventoryPort, never()).updateStock(any(CoffeeBean.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
