package org.example.buiseness_entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderCreate {
    private List<String> ingredients;
}
