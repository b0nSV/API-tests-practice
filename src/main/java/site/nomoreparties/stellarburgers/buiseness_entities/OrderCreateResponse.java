package site.nomoreparties.stellarburgers.buiseness_entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCreateResponse {
    private String name;
    private OrderNumber order;
    private boolean success;
}
