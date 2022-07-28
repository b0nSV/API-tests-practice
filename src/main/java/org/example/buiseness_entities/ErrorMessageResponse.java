package org.example.buiseness_entities;

import lombok.Data;

@Data
public class ErrorMessageResponse {
    private boolean success;
    private String message;
}
