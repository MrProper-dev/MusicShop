package musicshop.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.dto.BasketDto;
import musicshop.dto.OrderPreviewForClientDto;
import musicshop.dto.OrderPreviewDto;
import musicshop.entities.Client;
import musicshop.entities.Order;
import musicshop.entities.Product;
import musicshop.entities.ProductOrder;
import musicshop.entities.Seller;
import musicshop.entities.keys.ProductOrderId;
import musicshop.mappers.OrderMapper;
import musicshop.repositories.OrderRepository;
import musicshop.repositories.ProductOrderRepository;

@Service
public class OrderService {

    private final Integer ORDERS_PAGE_SIZE_FOR_ADMIN = 5;

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
    public boolean cancelOrderForClient(Long clientId, Long orderId) {
        int updatedRows = orderRepository.updateStatusByIdAndPastStatusAndClientId(
                orderId, 
                Order.Status.CANCELED, 
                Order.Status.CREATED,
                clientId
        );
        return updatedRows > 0;
    }

    @Transactional
    public BasketDto getBasket(Long clientId) {
        Order basket = orderRepository.findByClientIdAndStatus(clientId, Order.Status.NULL);
        productOrderRepository.findWithProductAndPicturesByOrderId(basket.getId());
        return orderMapper.mapToBasketDto(basket);
    }

    @Transactional
    public void removeFromBasket(Long clientId, Long productId) {
        Long orderId = orderRepository.findIdByClientIdAndStatus(clientId, Order.Status.NULL);
        if(orderId == null) throw new RuntimeException();
        
        productOrderRepository.deleteByOrderIdAndProductId(orderId, productId);
    }

    @Transactional
    public void updateQuantity(Long clientId, Long productId, Integer quantity) {
        Long orderId = orderRepository.findIdByClientIdAndStatus(clientId, Order.Status.NULL);
        if(orderId == null) throw new RuntimeException();
        if(quantity <= 0) throw new RuntimeException();
        productOrderRepository.updateQuantityByOrderIdAndProductId(orderId, productId, quantity);
    }

    @Transactional
    public List<Long> checkout(Long clientId, Long orderId) {
        List<ProductOrder> productOrders = productOrderRepository.findWithProductByOrderId(orderId);
        List<Long> productIds = new ArrayList<>();
        for(ProductOrder po : productOrders){
            if(po.getQuantity() > po.getProduct().getQuantity())
                productIds.add(po.getProduct().getId());
        }
        if(!productIds.isEmpty()) return productIds;
        for(ProductOrder po : productOrders){
            po.getProduct().setQuantity(po.getProduct().getQuantity() - po.getQuantity());
        }
        if(orderRepository.updateStatusByIdAndPastStatusAndClientId(orderId, Order.Status.CREATED, Order.Status.NULL, clientId) == 0){
            throw new RuntimeException("AIL");
        }
        return productIds;
    }

    public List<OrderPreviewDto> getOrdersForSeller(String statusFilter) {
        List<Order> orders;
        
        if (statusFilter == null || statusFilter.isEmpty() || "ALL".equals(statusFilter)) {
            orders = orderRepository.findWithClientAndProductOrdersAndProductByStatusInOrderByTimestampDesc(List.of(Order.Status.CREATED, Order.Status.READY));
        } else {
            try {
                Order.Status status = Order.Status.valueOf(statusFilter);
                orders = orderRepository.findWithClientAndProductOrdersAndProductByStatusInOrderByTimestampDesc(List.of(status));
            } catch (IllegalArgumentException e) {
                orders = orderRepository.findWithClientAndProductOrdersAndProductByStatusInOrderByTimestampDesc(List.of(Order.Status.CREATED, Order.Status.READY));
            }
        }
        
        return orders.stream()
                .map(orderMapper::mapToOrderPreviewDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void collectOrder(Long orderId, Seller seller) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != Order.Status.CREATED) {
            throw new RuntimeException("Order have to be with status 'CREATED'");
        }
        order.setStatus(Order.Status.READY);
        order.setSeller(seller);
    }

    @Transactional
    public void issueOrder(Long orderId) {
        int updated = orderRepository.updateStatusByIdAndPastStatus(orderId, Order.Status.ISSUED, Order.Status.READY);
        if (updated == 0) {
            throw new RuntimeException("MBR");
        }
    }

    public Page<OrderPreviewDto> getOrdersForAdmin(String strStatus, LocalDate from, LocalDate to, Integer page){
        Pageable pageable = PageRequest.of(page == null ? 0 : page < 0 ? 0 : page , ORDERS_PAGE_SIZE_FOR_ADMIN).withSort(Sort.by("timestamp").descending());
        Page<Order> orders = null;

        List<Order.Status> statuses = List.of(Order.Status.CREATED, Order.Status.READY, Order.Status.ISSUED, Order.Status.CANCELED);
        if(strStatus != null && !strStatus.isEmpty()){
            try{
                Order.Status status = Order.Status.valueOf(strStatus);
                statuses = List.of(status);
            }catch (IllegalArgumentException e) {}
        }

        if(from != null && to != null && from.isBefore(to)){
            orders = orderRepository.findWithClientAndWithProductOrdersWithProductByStatusInAndTimestampAfterAndTimestampBefore(statuses, from.atStartOfDay(), to.plusDays(1l).atStartOfDay(), pageable);
        }else if(from != null){
            orders = orderRepository.findWithClientAndWithProductOrdersWithProductByStatusInAndTimestampAfter(statuses, from.atStartOfDay(), pageable);
        }else if(to != null){
            orders = orderRepository.findWithClientAndWithProductOrdersWithProductByStatusInAndTimestampBefore(statuses, to.plusDays(1l).atStartOfDay(), pageable);
        }else{
            orders = orderRepository.findWithClientAndWithProductOrdersWithProductByStatusIn(statuses, pageable);
        }

        List<OrderPreviewDto> dtos = orders.getContent().stream()
            .map(orderMapper::mapToOrderPreviewDto)
            .toList();

        return new PageImpl<>(dtos, pageable, orders.getTotalElements());
    }

    @Transactional
    public void cancelOrderForAdmin(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException());
        if(order.getStatus() == Order.Status.CREATED || order.getStatus() == Order.Status.READY){
            order.setStatus(Order.Status.CANCELED);
        }
    }

}
