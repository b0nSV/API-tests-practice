package org.example.buiseness_entities;

import lombok.Data;

@Data
public class Ingredient {
    private String _id;
    private String name;
    private String type;
    private int proteins;
    private int fat;
    private int carbohydrates;
    private int calories;
    private int price;
    private String image;
    private String image_mobile;
    private String image_large;
    private int __v;
// TODO удалить после стабилизации тестов создания заказа
/*    public static String getId(Ingredient ingredient) {
        if (ingredient == null) {
            Assert.fail("Ингредиент не найден");
            return null;
        }
        if (ingredient.get_id() != null) return ingredient.get_id();
        else {
            Assert.fail("У ингредиента нет \"_id\"");
            return null;
        }
    }*/
}
