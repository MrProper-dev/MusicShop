package musicshop.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketDto {
    private Long orderId;
    private List<BasketItemDto> items;
    private Float totalPrice;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasketItemDto {
        private Long productId;
        private String productName;
        private String imagePath;
        private Float price;
        private Integer quantity;
        private Float subtotal;
    }
}
