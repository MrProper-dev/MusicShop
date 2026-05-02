package musicshop.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import musicshop.dto.PurchaseForAdminDto;
import musicshop.dto.PurchaseForSellerDto;
import musicshop.entities.ProductPurchase;
import musicshop.entities.Purchase;

@Component
public class PurchaseMapper {

    public PurchaseForSellerDto mapToPurchaseForSellerDto(Purchase purchase) {
        if (purchase == null) return null;
        
        PurchaseForSellerDto dto = new PurchaseForSellerDto();
        dto.setId(purchase.getId());
        
        List<PurchaseForSellerDto.ProductDto> products = purchase.getProductPurchases().stream()
                .map(this::mapToPurchaseForSellerDtoProductDto)
                .collect(Collectors.toList());
        dto.setProducts(products);
        
        Double totalPrice = products.stream()
                .mapToDouble(PurchaseForSellerDto.ProductDto::getSubtotal)
                .sum();
        dto.setTotalPrice(totalPrice.floatValue());
        
        return dto;
    }
    
    private PurchaseForSellerDto.ProductDto mapToPurchaseForSellerDtoProductDto(ProductPurchase productPurchase) {
        PurchaseForSellerDto.ProductDto dto = new PurchaseForSellerDto.ProductDto();
        dto.setProductId(productPurchase.getProduct().getId());
        dto.setProductName(productPurchase.getProduct().getName());
        dto.setQuantity(productPurchase.getQuantity());
        dto.setPrice(productPurchase.getProduct().getPrice());
        dto.setSubtotal(dto.getPrice() * dto.getQuantity());
        return dto;
    }

    public PurchaseForAdminDto mapToPurchaseForAdminDto(Purchase purchase){
        if(purchase == null) return null;
        PurchaseForAdminDto dto = new PurchaseForAdminDto();
        dto.setId(purchase.getId());
        dto.setSellerName(purchase.getSeller().getFullName());
        dto.setStatus(purchase.getStatus().name());
        dto.setTimestamp(purchase.getTimestamp());
        List<PurchaseForAdminDto.ProductDto> products = purchase.getProductPurchases().stream()
            .map(this::mapToPurchaseForAdminDtoProductDto)
            .toList();
        dto.setProducts(products);
        Double totalPrice = products.stream()
            .mapToDouble(p -> p.getSubtotal())
            .sum();
        dto.setTotalPrice(totalPrice.floatValue());
        return dto;
    }

    private PurchaseForAdminDto.ProductDto mapToPurchaseForAdminDtoProductDto(ProductPurchase productPurchase){
        if(productPurchase == null) return null;
        PurchaseForAdminDto.ProductDto dto = new PurchaseForAdminDto.ProductDto();
        dto.setPrice(productPurchase.getProduct().getPrice());
        dto.setProductId(productPurchase.getProduct().getId());
        dto.setProductName(productPurchase.getProduct().getName());
        dto.setQuantity(productPurchase.getQuantity());
        dto.setSubtotal(dto.getQuantity() * dto.getPrice());
        return dto;
    }


}
