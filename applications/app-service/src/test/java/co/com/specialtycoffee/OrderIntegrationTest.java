package co.com.specialtycoffee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderIntegrationTest {

    public static final String BODY_GEISHA = """
        {
            "beanName": "Geisha",
            "quantityGrams": 100,
            "brewMethod": "V60"
        }
        """;

    public static final String BODY_GEISHA_9999 = """
        {
            "beanName": "Geisha",
            "quantityGrams": 9999,
            "brewMethod": "V60"
        }
        """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn201WhenOrderIsConfirmed() throws Exception {

        mockMvc.perform(post("/api/orders")
                .contentType("application/json")
                .content(BODY_GEISHA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.beanName").value("Geisha"))
                .andExpect(jsonPath("$.quantityGrams").value(100))
                .andExpect(jsonPath("$.brewMethod").value("V60"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldReturn404WhenStockIsInsufficient() throws Exception {

        mockMvc.perform(post("/api/orders")
                .contentType("application/json")
                .content(BODY_GEISHA_9999))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn404WhenBeanNotFound() throws Exception {

        mockMvc.perform(post("/api/orders")
                .contentType("application/json")
                .content("""
                        {
                            "beanName": "Dont exist",
                            "quantityGrams": 100,
                            "brewMethod": "V60"
                        }
                """)
        );
    }
}
