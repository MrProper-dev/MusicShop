package musicshop.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPreviewForClientDto {
    private Long id;
    private LocalDateTime timestamp;
    private String status;
    private List<OrderProductDto> products;
    private Double totalPrice;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProductDto {
        private String name;
        private Integer quantity;
    }
}
