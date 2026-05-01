package musicshop.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import musicshop.dto.DeliveryDto;
import musicshop.entities.Delivery;
import musicshop.entities.ProductDelivery;

@Component
public class DeliveryMapper {

    public DeliveryDto mapToDeliveryDto(Delivery delivery){
        if(delivery == null) return null;
        DeliveryDto dto = new DeliveryDto();
        dto.setId(delivery.getId());
        dto.setSupplierName(delivery.getSupplierName());
        dto.setAdminName(delivery.getAdmin().getFullName());
        dto.setTimestamp(delivery.getTimestamp());
        List<DeliveryDto.ProductDto> products = delivery.getProductDeliveries().stream()
            .map(this::mapToDeliveryDtoProductDto)
            .toList();
        dto.setProducts(products);
        Integer totalQuantity = products.stream().mapToInt(p -> p.getQuantity()).sum();
        dto.setTotalQuantity(totalQuantity);
        return dto;
    }

    private DeliveryDto.ProductDto mapToDeliveryDtoProductDto(ProductDelivery productdDelivery){
        if(productdDelivery == null) return null;
        DeliveryDto.ProductDto dto = new DeliveryDto.ProductDto();
        dto.setId(productdDelivery.getProduct().getId());
        dto.setName(productdDelivery.getProduct().getName());
        dto.setQuantity(productdDelivery.getQuantity());
        return dto;
    }

}
