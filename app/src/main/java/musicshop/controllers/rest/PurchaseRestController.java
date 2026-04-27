package musicshop.controllers.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import musicshop.services.PurchaseService;

@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseRestController {

    @Autowired
    private PurchaseService purchaseService;

    @PutMapping("/product/{id}")
    public void addToPurchase(
        @PathVariable("id") Long productId, 
        @RequestBody Map<String, Long> requestBody){
        try{
            purchaseService.addProductToPurhase(productId, requestBody.get("purchase_id"), requestBody.get("quantity").intValue());
        }catch (IllegalArgumentException exception){
            throw new ResponseStatusException(HttpStatusCode.valueOf(409));
        }
    }

}
