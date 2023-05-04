package site.nomoreparties.stellarburgers.buiseness_entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private List<String> ingredients;
    private String _id;
    private String status;
    private int number;
    private Date createdAt;
    private Date updatedAt;
}
