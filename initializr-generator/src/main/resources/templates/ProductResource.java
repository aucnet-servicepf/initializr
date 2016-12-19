package ${packageName}.api.v1;

import lombok.Data;

@Data
public class ProductResource {

    private Long id;
    private String name;
    private Integer price;

}
