package musicshop.controllers.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import musicshop.entities.Client;
import musicshop.services.OrderService;
@RestController
@RequestMapping("/api/v1/orders")
//TODO: доделать работу страинцы корзины и сделать страицу регистарици 
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @PutMapping( value = "/product/{id}")
    public void addToBasket(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, Integer> requestBody){
        Client client = (Client) userDetails;
        orderService.addProductToBasket(productId, client.getId(), requestBody.get("quantity"));
    }



    @DeleteMapping("/{orderId}")
    public void cancelOrder(@PathVariable("orderId") Long orderId) {
        boolean cancelled = orderService.cancelOrder(orderId);
        
        if (!cancelled) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "You can't cancel order with current status.");
        }
    }

}
