package musicshop.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import musicshop.dto.BasketDto;
import musicshop.dto.OrderPreviewForClientDto;
import musicshop.dto.OrderPreviewForClientDto.OrderProductDto;
import musicshop.dto.OrderPreviewDto;
import musicshop.entities.Order;
import musicshop.entities.Picture;
import musicshop.entities.ProductOrder;

@Component
public class OrderMapper {

    public OrderPreviewForClientDto mapToOrderPreviewForClientDto(Order order) {
        if (order == null) return null;

        OrderPreviewForClientDto dto = new OrderPreviewForClientDto();
        dto.setId(order.getId());
        dto.setTimestamp(order.getTimestamp());
        dto.setStatus(order.getStatus().name());
        
        List<OrderProductDto> products = order.getProductOrders().stream()
                .map(this::mapToOrderProductDto)
                .collect(Collectors.toList());
        dto.setProducts(products);
        
        double totalPrice = order.getProductOrders().stream()
                .mapToDouble(po -> po.getQuantity() * po.getProduct().getPrice())
                .sum();
        dto.setTotalPrice(totalPrice);
        
        return dto;
    }

    public BasketDto mapToBasketDto(Order order) {
        if (order == null) return new BasketDto(null, List.of(), 0.0f);
        
        List<BasketDto.BasketItemDto> items = order.getProductOrders().stream()
                .map(this::mapToBasketItemDto)
                .collect(Collectors.toList());
        
        Double totalPrice = items.stream()
                .mapToDouble(BasketDto.BasketItemDto::getSubtotal)
                .sum();
        
        return new BasketDto(order.getId(), items, totalPrice.floatValue());
    }

    public OrderPreviewDto mapToOrderPreviewDto(Order order) {
        if (order == null) return null;
        
        OrderPreviewDto dto = new OrderPreviewDto();
        dto.setId(order.getId());
        dto.setTimestamp(order.getTimestamp());
        dto.setStatus(order.getStatus().name());
        dto.setClientName(order.getClient().getFullName());
        dto.setClientPhone(order.getClient().getPhone());
        
        List<OrderPreviewDto.ProductDto> products = order.getProductOrders().stream()
                .map(this::mapToOrderPreviewDtoProductDto)
                .collect(Collectors.toList());
        dto.setProducts(products);
        
        double totalPrice = products.stream()
                .mapToDouble(OrderPreviewDto.ProductDto::getSubtotal)
                .sum();
        dto.setTotalPrice((float) totalPrice);
        
        return dto;
    }

    private OrderPreviewDto.ProductDto mapToOrderPreviewDtoProductDto(ProductOrder productOrder) {
        OrderPreviewDto.ProductDto dto = new OrderPreviewDto.ProductDto();
        dto.setProductId(productOrder.getProduct().getId());
        dto.setProductName(productOrder.getProduct().getName());
        dto.setQuantity(productOrder.getQuantity());
        dto.setPrice(productOrder.getProduct().getPrice());
        dto.setSubtotal(dto.getPrice() * dto.getQuantity());
        return dto;
    }
    
    private BasketDto.BasketItemDto mapToBasketItemDto(ProductOrder productOrder) {
        BasketDto.BasketItemDto dto = new BasketDto.BasketItemDto();
        dto.setProductId(productOrder.getProduct().getId());
        dto.setProductName(productOrder.getProduct().getName());
        
        String imagePath = productOrder.getProduct().getPictures().stream()
                .filter(Picture::getIsMain)
                .findFirst()
                .map(Picture::getPath)
                .get();
        dto.setImagePath(imagePath);
        
        dto.setPrice(productOrder.getProduct().getPrice());
        dto.setQuantity(productOrder.getQuantity());
        dto.setSubtotal(dto.getPrice() * dto.getQuantity());
        
        return dto;
    }

    private OrderProductDto mapToOrderProductDto(ProductOrder productOrder) {
        OrderProductDto dto = new OrderProductDto();
        dto.setName(productOrder.getProduct().getName());
        dto.setQuantity(productOrder.getQuantity());
        return dto;
    }
    
}