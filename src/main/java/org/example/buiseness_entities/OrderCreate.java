package org.example.buiseness_entities;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreate {
    private List<String> ingredients;
}
