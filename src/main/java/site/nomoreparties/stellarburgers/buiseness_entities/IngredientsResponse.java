package site.nomoreparties.stellarburgers.buiseness_entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class IngredientsResponse {
    private boolean success;
    private List<Ingredient> data;
}
