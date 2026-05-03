package musicshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientPreviewDto {
    private Long id;
    private String fullName;
    private String phone;
    private Long orderQuantity;
    private Double totalCost;
}
