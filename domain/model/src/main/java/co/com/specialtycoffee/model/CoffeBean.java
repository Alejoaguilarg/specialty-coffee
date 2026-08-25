package co.com.specialtycoffee.model;

public class CoffeBean {

    private String name;
    private Integer availableGrams;

    public CoffeBean(String name, Integer availableGrams) {
        this.name = name;
        this.availableGrams = availableGrams;
    }

    public String getName() {
        return name;
    }

    public Integer getAvailableGrams() {
        return availableGrams;
    }

    public Integer discountGrams(Integer grams){
        return availableGrams - grams;
    }
}
