package co.com.specialtycoffee.model.ex;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
