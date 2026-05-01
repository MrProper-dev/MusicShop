package musicshop.controllers.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import musicshop.entities.Admin;
import musicshop.services.DeliveryService;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryRestController {

    @Autowired
    private DeliveryService deliveryService;

    @PutMapping("/product/{id}")
    public void addProductToActiveDelivery(
        @PathVariable("id") Long productId, 
        @RequestBody Map<String, Integer> requestBody, 
        @AuthenticationPrincipal UserDetails userDetails){
        Admin admin = (Admin) userDetails;
        try{
            deliveryService.putProductToActiveDelivery(productId, requestBody.get("quantity"), admin.getId());
        }catch (IllegalStateException exception){
            throw new ResponseStatusException(HttpStatusCode.valueOf(409));
        }
    }

}
