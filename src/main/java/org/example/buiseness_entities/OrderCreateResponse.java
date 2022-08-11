package org.example.buiseness_entities;

import lombok.Data;

@Data
public class OrderCreateResponse {
    private String name;
    private OrderNumber order;
    private boolean success;
}
