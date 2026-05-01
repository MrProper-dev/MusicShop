package musicshop.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import musicshop.dto.request.SellerDto;
import musicshop.services.SellerService;

@RestController
@RequestMapping("/api/v1/sellers")
public class SellerRestController {

    @Autowired
    private SellerService sellerService;

    @DeleteMapping("/{id}")
    public void deleteSeller(@PathVariable("id") Long sellerId){
        sellerService.deleteSellerById(sellerId);
    }

    @PatchMapping("/{id}")
    public void updateSeller(
        @PathVariable("id") Long sellerId,
        @RequestBody SellerDto sellerDto){
        try{
            sellerService.updateSeller(sellerId, sellerDto);
        }catch (DataIntegrityViolationException exception){
            throw new ResponseStatusException(HttpStatusCode.valueOf(409));
        }
    }

    @PostMapping
    public Long createSeller(@RequestBody SellerDto sellerDto){
        try{
            return sellerService.createSeller(sellerDto);
        }catch (DataIntegrityViolationException exception){
            throw new ResponseStatusException(HttpStatusCode.valueOf(409));
        }
    }

}
