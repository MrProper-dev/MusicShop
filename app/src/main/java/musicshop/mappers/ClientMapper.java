package musicshop.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import musicshop.dto.ClientForAdminDto;
import musicshop.entities.Client;
import musicshop.entities.Order;
import musicshop.entities.Product;
import musicshop.entities.ProductOrder;

@Component
public class ClientMapper {

    public ClientForAdminDto mapToClientForAdminDto(Client client){
        if(client == null) return null;
        ClientForAdminDto dto = new ClientForAdminDto();
        dto.setName(client.getFullName());
        dto.setPhone(client.getPhone());
        List<ClientForAdminDto.OrderDto> orders = client.getOrders().stream()
            .map(this::mapToClientForAdminDtoOrderDto)
            .toList();
        dto.setOrders(orders);
        Double totalOrdersCost = orders.stream()
            .mapToDouble(o -> o.getTotalPrice())
            .sum();
        dto.setTotalOrdersCost(totalOrdersCost.floatValue());
        dto.setOrdersQuantity(orders.size());
        return dto;
    }

    private ClientForAdminDto.OrderDto mapToClientForAdminDtoOrderDto(Order order){
        if(order == null) return null;
        ClientForAdminDto.OrderDto dto = new ClientForAdminDto.OrderDto();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().name());
        dto.setTimestamp(order.getTimestamp());
        dto.setSellerName(order.getSeller().getFullName());
        List<ClientForAdminDto.OrderDto.ProductDto> products = order.getProductOrders().stream()
            .map(this::mapToClientForAdminDtoOrderDtoProductDto)
            .toList();
        dto.setProducts(products);
        Double totalPrice = products.stream()
            .mapToDouble(p -> p.getSubtotal())
            .sum();
        dto.setTotalPrice(totalPrice.floatValue());
        return dto;
    }

    private ClientForAdminDto.OrderDto.ProductDto mapToClientForAdminDtoOrderDtoProductDto(ProductOrder productOrder){
        if(productOrder == null) return null;
        ClientForAdminDto.OrderDto.ProductDto dto = new ClientForAdminDto.OrderDto.ProductDto();
        Product product = productOrder.getProduct();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(productOrder.getQuantity());
        dto.setSubtotal(dto.getPrice() * dto.getQuantity());
        return dto;
    }

}
