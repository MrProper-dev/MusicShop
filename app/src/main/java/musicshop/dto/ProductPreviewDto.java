package musicshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPreviewDto {

    private Long id;
    private String name;
    private Float price;
    private String picturePath;

}
