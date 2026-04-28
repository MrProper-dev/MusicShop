package musicshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPreviewForAdminDto {

    private Long id;
    private String name;
    private Float price;
    private Integer quantity;
    private String picturePath;

}
