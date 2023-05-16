package site.nomoreparties.stellarburgers.buiseness_entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum IngredientTypes {
    TYPE_BUN("bun"),
    TYPE_SAUCE("sauce"),
    TYPE_MAIN("main");

    @Getter
    private final String name;
}
