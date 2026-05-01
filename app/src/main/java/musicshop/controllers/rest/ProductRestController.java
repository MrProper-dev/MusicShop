package musicshop.controllers.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import musicshop.dto.PictureDto;
import musicshop.entities.Product;
import musicshop.services.ProductService;

@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") Long productId){
        productService.deleteProductById(productId);
    }

    @PostMapping
    public Long addProduct(@RequestBody Map<String, String> requestBody){
        String productName = requestBody.get("name");
        if(productName == null || productName.isEmpty()){
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Product product = productService.createProduct(productName);
        return product.getId();
    }

    @PutMapping("/{id}")
    public List<PictureDto> updateProduct(
        @PathVariable("id") Long productId, 
        @RequestParam(value = "main-picture", required = false) MultipartFile mainPicture,
        @RequestParam(value = "picture", required = false) List<MultipartFile> pictures,
        @RequestParam("name") String name,
        @RequestParam("categoryId") Long categoryId,
        @RequestParam("price") Float price,
        @RequestParam("quantity") Integer quantity,
        @RequestParam("description") String description,
        @RequestParam(value = "main-picture-id", required = false) Long mainPictureId){
        try{
            return productService.updateProduct(productId, categoryId, name, description, price, quantity, mainPicture, pictures, mainPictureId);
        }catch (RuntimeException exception){
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), exception.getMessage());
        }
    }

}
