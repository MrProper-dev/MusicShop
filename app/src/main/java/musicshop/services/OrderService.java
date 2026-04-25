package musicshop.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.dto.BasketDto;
import musicshop.dto.OrderPreviewForClientDto;
import musicshop.entities.Client;
import musicshop.entities.Order;
import musicshop.entities.Product;
import musicshop.entities.ProductOrder;
import musicshop.entities.keys.ProductOrderId;
import musicshop.mappers.OrderMapper;
import musicshop.repositories.OrderRepository;
import musicshop.repositories.ProductOrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public void addProductToBasket(Long productId, Long clientId, Integer quantity){
        Order order = orderRepository.findBasketByClientId(clientId);
        if(order == null) {
            order = new Order();
            Client client = new Client();
            client.setId(clientId);
            order.setClient(client);
            order.setTimestamp(LocalDateTime.now());
            order.setStatus(Order.Status.NULL);
            order.setProductOrders(new ArrayList<>());
            orderRepository.save(order);
        }
        List<ProductOrder> productOrders = order.getProductOrders();
        ProductOrder productOrder = null;
        for (ProductOrder po : productOrders) {
            if(po.getProduct().getId() == productId) {
                productOrder = po;
                break;
            }
        }
        if(productOrder == null){
            productOrder = new ProductOrder();
            Product product = new Product();
            ProductOrderId productOrderId = new ProductOrderId();
            productOrderId.setOrderId(order.getId());
            productOrderId.setProductId(productId);
            productOrder.setId(productOrderId);
            product.setId(productId);
            productOrder.setProduct(product);
            productOrder.setOrder(order);
            productOrders.add(productOrder);
        }
        productOrder.setQuantity(quantity);
        orderRepository.save(order);
    }
    
    public List<OrderPreviewForClientDto> getOrdersByClient(Long clientId, String statusFilter) {
        List<Order> orders;
        
        if (statusFilter == null || statusFilter.isEmpty() || "ALL".equals(statusFilter)) {
            orders = orderRepository.findByClientIdAndStatusNotOrderByTimestampDesc(clientId, Order.Status.NULL);
        } else {
            Order.Status status = Order.Status.valueOf(statusFilter);
            orders = orderRepository.findByClientIdAndStatusOrderByTimestampDesc(clientId, status);
        }
        
        return orders.stream()
                .map(orderMapper::mapToOrderPreviewForClientDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean cancelOrder(Long orderId) {
        int updatedRows = orderRepository.cancelOrder(
                orderId, 
                Order.Status.CANCELED, 
                Order.Status.CREATED
        );
        return updatedRows > 0;
    }

    @Transactional
    public BasketDto getBasket(Long clientId) {
        Order basket = orderRepository.findByClientIdAndStatus(clientId, Order.Status.NULL);
        productOrderRepository.findByOrderId(basket.getId());
        return orderMapper.mapToBasketDto(basket);
    }

}
