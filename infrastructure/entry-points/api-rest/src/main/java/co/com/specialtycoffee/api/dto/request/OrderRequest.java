package co.com.specialtycoffee.api.dto.request;

public record OrderRequest(String beanName, int quantityGrams, String brewMethod) {}
