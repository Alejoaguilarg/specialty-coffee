package co.com.specialtycoffee.model.ex;

public class BeanNotFoundException extends BusinessException {

    public BeanNotFoundException(String beanName) {
        super(String.format("Bean '%s' not found in catalog", beanName));
    }
}
