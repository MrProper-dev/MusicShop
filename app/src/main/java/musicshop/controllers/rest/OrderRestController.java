package musicshop.controllers.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import musicshop.entities.Client;
import musicshop.entities.Seller;
import musicshop.services.OrderService;
@RestController
@RequestMapping("/api/v1/orders")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    @PutMapping( value = "/product/{id}")
    public void addToBasket(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, Integer> requestBody){
        Client client = (Client) userDetails;
        orderService.addProductToBasket(productId, client.getId(), requestBody.get("quantity"));
    }

    @DeleteMapping("/product/{id}")
    public void removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long productId) {
        Client client = (Client) userDetails;
        orderService.removeFromBasket(client.getId(), productId);
    }

    @PatchMapping("/product/{id}")
    public void updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long productId,
            @RequestBody Map<String, Integer> requestBody) {
        Client client = (Client) userDetails;
        orderService.updateQuantity(client.getId(), productId, requestBody.get("quantity"));
    }

    @DeleteMapping("/{orderId}")
    public void cancelOrder(
        @PathVariable("orderId") Long orderId,
        @AuthenticationPrincipal UserDetails userDetails) {
        Client client = (Client) userDetails;
        boolean cancelled = orderService.cancelOrder(client.getId(), orderId);
        
        if (!cancelled) {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "You can't cancel order with current status.");
        }
    }

    @PostMapping("/{id}/checkout")
    @ResponseBody
    public List<Long> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long orderId) {
        Client client = (Client) userDetails;
        try{
            List<Long> productId = orderService.checkout(client.getId(), orderId);
            return productId;
        }catch (RuntimeException exception){
            if(exception.getMessage().equals("AIL"))
            throw new ResponseStatusException(HttpStatusCode.valueOf(403));
            else throw exception;
        }
    }

    @PatchMapping("/{id}/collect")
    public void collectOrder(
            @PathVariable("id") Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Seller seller = (Seller) userDetails;
        orderService.collectOrder(orderId, seller);
    }

    @PatchMapping("/{id}/issue")
    public void issueOrder(@PathVariable("id") Long orderId) {
        try{
            orderService.issueOrder(orderId);
        }catch (RuntimeException exception){
            if(exception.getMessage().equals("MBR"))
                throw new ResponseStatusException(HttpStatusCode.valueOf(409));
            else
                throw exception;
        }
    }

}
