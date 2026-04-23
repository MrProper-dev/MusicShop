package musicshop.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.entities.Client;
import musicshop.entities.Order;
import musicshop.entities.Product;
import musicshop.entities.ProductOrder;
import musicshop.entities.keys.ProductOrderId;
import musicshop.repositories.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void addProductToBasket(Long productId, Long clientId, Integer quantity){
        Order order = orderRepository.findBasketByClientId(clientId);
        if(order == null) {
            order = new Order();
            Client client = new Client();
            client.setId(clientId);
            order.setClient(client);
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

}
