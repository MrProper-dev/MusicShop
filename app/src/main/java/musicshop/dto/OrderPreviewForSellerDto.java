package musicshop.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPreviewForSellerDto {
    private Long id;
    private LocalDateTime timestamp;
    private String status;
    private String clientName;
    private String clientPhone;
    private Float totalPrice;
    private List<ProductDto> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        private Long productId;
        private String productName;
        private Integer quantity;
        private Float price;
        private Float subtotal;
    }
}