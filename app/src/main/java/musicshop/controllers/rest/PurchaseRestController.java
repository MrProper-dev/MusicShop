package musicshop.controllers.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import musicshop.entities.Seller;
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

    @PostMapping()
    public Long createPurchase(@AuthenticationPrincipal UserDetails userDetails) {
        Seller seller = (Seller) userDetails;
        return purchaseService.createPurchase(seller).getId();
    }

    @DeleteMapping("/{purchaseId}/products/{productId}")
    public void removeProductFromPurchase(
            @PathVariable("purchaseId") Long purchaseId,
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Seller seller = (Seller) userDetails;
        try{
            purchaseService.removeProductFromPurchase(purchaseId, seller.getId(), productId);
        }catch (RuntimeException exception){
            throw new ResponseStatusException(HttpStatusCode.valueOf(409));
        }
    }

    @DeleteMapping("/{id}")
    public void deletePurchase(
            @PathVariable("id") Long purchaseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Seller seller = (Seller) userDetails;
        purchaseService.deletePurchase(purchaseId, seller.getId());
    }

    @PostMapping("/{id}/checkout")
    @ResponseBody
    public void checkoutPurchase(
            @PathVariable("id") Long purchaseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Seller seller = (Seller) userDetails;
        purchaseService.checkoutPurchase(purchaseId, seller.getId());
    }

}
