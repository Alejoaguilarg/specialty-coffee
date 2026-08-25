package co.com.specialtycoffee.model.ex;

public class InsufficientInventoryException extends BusinessException {

    public InsufficientInventoryException(String beanName, int requested, int available) {
        super(String.format(
                "Not enough stock for '%s': requested %dg but only %dg available",
                beanName,
                requested,
                available
        ));
    }
}
