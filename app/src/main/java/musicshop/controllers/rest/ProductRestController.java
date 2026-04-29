package musicshop.controllers.rest;

import java.io.FileOutputStream;
import java.io.IOException;
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

import musicshop.App;
import musicshop.entities.Product;
import musicshop.services.ProductService;


//TODO: доделать старницу карточки товара для админа
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
    public void updateProduct(@PathVariable("id") Long productId, @RequestParam(value = "main-picture", required = false) MultipartFile mainPicture){
        try (FileOutputStream fileOutputStream = new FileOutputStream(App.RESOURCES_PATH + "/static/pictures/some.jpg")) {
            fileOutputStream.write(mainPicture.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
