package musicshop.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import musicshop.dto.ProductPreviewForClientDto;
import musicshop.entities.Category;
import musicshop.services.CategoryService;
import musicshop.services.ProductService;

@Controller
// TODO: js скрипт для возврашения выделеных категорий и поиска
// TODO: поиск
// TODO: пагинация 
public class ClientController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/login")
    public String login(){
        return "client/log_in";
    }

    @GetMapping("/products")
    public String productsList(Model model, 
            @RequestParam(name = "page", required = false) Integer page, 
            @RequestParam(name = "cat-id", required = false) List<Integer> categoryIds,
            @RequestParam(name = "price_from", required = false) Integer priceFrom,
            @RequestParam(name = "price_to", required = false) Integer priceTo){
        List<Category> categories = categoryService.getAllCategories();
        Page<ProductPreviewForClientDto> productsPage = productService.getCatalogPageForClient(page, categoryIds, priceFrom, priceTo);

        model.addAttribute("categories", categories);
        model.addAttribute("products", productsPage.toList());
        return "client/products_list";
    }
    
}
