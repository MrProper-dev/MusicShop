package musicshop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import musicshop.entities.Product;
import musicshop.services.ProductService;


@RestController
public class PupController {

    @Autowired
    private ProductService productService;

    @GetMapping("/pup")
    public String getMethod(){
        System.out.println("\nPUP\n");
        Product product = productService.getFirstProductToString();
        StringBuilder sb = new StringBuilder();
        sb.append(product.getId());
        sb.append(" \n");
        System.out.println("\nSOME\n");
        sb.append(product.getCategory().getId());
        sb.append(" \n");
        sb.append(product.getName());
        sb.append(" \n");
        sb.append(product.getDescription());
        sb.append(" \n");
        sb.append(product.getPrice());
        sb.append(" \n");
        sb.append(product.getQuantity());
        sb.append(" \n");
        return sb.toString();
    }

}