package musicshop.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseForSellerDto {
    private Long id;
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