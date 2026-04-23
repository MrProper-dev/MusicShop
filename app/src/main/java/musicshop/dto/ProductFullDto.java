package musicshop.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFullDto {

    private Long id;
    
    private CategoryFullDto category;

    private String name;

    private String description;

    private Float price;

    private Integer quantity;

    private List<PictureFullDto> pictures;

}
