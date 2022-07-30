package org.example.buiseness_entities;

import lombok.Data;

@Data
public class PatchUserResponse {
    private boolean success;
    private User user;
}
