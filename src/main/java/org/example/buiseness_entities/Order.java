package org.example.buiseness_entities;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Order {
    private List<String> ingredients;
    private String _id;
    private String status;
    private int number;
    private Date createdAt;
    private Date updatedAt;
}
