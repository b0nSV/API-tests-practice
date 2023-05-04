package site.nomoreparties.stellarburgers.buiseness_entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderList {
    private boolean success;
    private List<Order> orders;
    private int total;
    private int totalToday;
}
