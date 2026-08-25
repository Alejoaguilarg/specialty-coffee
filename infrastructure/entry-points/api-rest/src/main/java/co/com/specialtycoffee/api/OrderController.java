package co.com.specialtycoffee.api;
import co.com.specialtycoffee.api.dto.request.OrderRequest;
import co.com.specialtycoffee.model.entity.Order;
import co.com.specialtycoffee.usecase.ProcessCoffeeOrderUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API Rest controller.
 * 
 * Example of how to declare and use a use case:
 * <pre>
 * private final MyUseCase useCase;
 * 
 * public String commandName() {
 *     return useCase.execute();
 * }
 * </pre>
 */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class OrderController {

    private final ProcessCoffeeOrderUseCase processCoffeeOrderUseCase;

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        Order order =    processCoffeeOrderUseCase.execute(
                request.beanName(),
                request.quantityGrams(),
                request.brewMethod()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
