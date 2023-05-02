package site.nomoreparties.stellarburgers.buiseness_entities;

import lombok.Data;

@Data
public class OrderCreateResponse {
    private String name;
    private OrderNumber order;
    private boolean success;
}
