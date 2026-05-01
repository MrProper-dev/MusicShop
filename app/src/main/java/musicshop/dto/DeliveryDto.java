package musicshop.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    private Long id;
    private String adminName;
    private String supplierName;
    private LocalDateTime timestamp;
    private Integer totalQuantity;
    private List<ProductDto> products;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto{
        private Long id;
        private String name;
        private Integer quantity;
    }
}
