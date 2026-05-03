package musicshop.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientForAdminDto {
    private String name;
    private String phone;
    private Integer ordersQuantity;
    private Float totalOrdersCost;
    private List<OrderDto> orders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDto{
    private Long id;
    private LocalDateTime timestamp;
    private String status;
    private String sellerName;
    private Float totalPrice;
    private List<ProductDto> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        private Long id;
        private String name;
        private Integer quantity;
        private Float price;
        private Float subtotal;
    }
    }
}
