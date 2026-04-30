package musicshop.controllers.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import musicshop.services.DeliveryService;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryRestController {

    @Autowired
    private DeliveryService deliveryService;

    @PutMapping("/product/{id}")
    public void addProductToActiveDelivery(
        @PathVariable("id") Long productId, @RequestBody Map<String, Integer> requestBody){
        
    }

}
