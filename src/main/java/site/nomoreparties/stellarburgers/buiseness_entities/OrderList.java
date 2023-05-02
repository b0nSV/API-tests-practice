package site.nomoreparties.stellarburgers.buiseness_entities;

import lombok.Data;

import java.util.List;

@Data
public class OrderList {
    private boolean success;
    private List<Order> orders;
    private int total;
    private int totalToday;
}
