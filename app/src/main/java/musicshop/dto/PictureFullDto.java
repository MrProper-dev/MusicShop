package musicshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureFullDto {

    private Long id;

    private String path;

    private Boolean isMain;

}
