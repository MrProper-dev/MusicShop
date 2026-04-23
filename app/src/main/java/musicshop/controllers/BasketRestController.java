package musicshop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import musicshop.entities.Client;
import musicshop.services.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
//TODO: отправлять товар в корхину черех js (иначе переходит на старницу APi)
//TODO: проверить работу 
public class BasketRestController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/product/{id}")
    public void addToBasket(@PathVariable("id") Long productId, @AuthenticationPrincipal UserDetails userDetails, @RequestParam("quantity") Integer quantity){
        Client client = (Client) userDetails;
        orderService.addProductToBasket(productId, client.getId(), quantity);
    }

}
