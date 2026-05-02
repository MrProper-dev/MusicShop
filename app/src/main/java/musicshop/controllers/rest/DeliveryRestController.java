package musicshop.controllers.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
        deliveryService.putProductToActiveDelivery(productId, requestBody.get("quantity"), admin.getId());
    }

    @DeleteMapping("/product/{id}")
    public void deleteProductFromActiveDelivery(
        @PathVariable("id") Long productId,
        @AuthenticationPrincipal UserDetails userDetails){
        Admin admin = (Admin) userDetails;
        deliveryService.deleteProductFromActiveDelivery(productId, admin.getId());
    }

    @PostMapping("/issue")
    public void takeActiveDelivery(@AuthenticationPrincipal UserDetails userDetails){
        Admin admin = (Admin) userDetails;
        try{
            deliveryService.takeActiveDelivery(admin.getId());
        } catch (IllegalStateException exception){
            throw new ResponseStatusException(HttpStatusCode.valueOf(409));
        }
    }

    @PatchMapping("/deliverier")
    public void updatDeliverier(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody Map<String, String> requestBody){
        Admin admin = (Admin) userDetails;
        deliveryService.updatDeliverier(requestBody.get("deliverier"), admin.getId());
    }

    @PatchMapping("/product/{id}")
    public void updateProductQuantity(
        @PathVariable("id") Long productId,
        @RequestBody Map<String, Integer> requestBody,
        @AuthenticationPrincipal UserDetails userDetails){
        Admin admin = (Admin) userDetails;
        try{
            deliveryService.updateProductQuantity(productId, requestBody.get("quantity"), admin.getId());
        }catch (IllegalArgumentException exception){
            throw new ResponseStatusException(HttpStatusCode.valueOf(409));
        }
    }

}
