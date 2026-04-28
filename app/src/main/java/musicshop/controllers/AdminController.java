package musicshop.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import musicshop.controllers.util.PaginationForThymeleaf;
import musicshop.dto.ProductPreviewForAdminDto;
import musicshop.entities.Category;
import musicshop.services.CategoryService;
import musicshop.services.ProductService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaginationForThymeleaf pagination;

    @GetMapping("/login")
    public String admin(){
        return "admin/log_in";
    }

    @GetMapping("/products")
    public String showProducts(Model model, 
            @RequestParam(name = "page", required = false) Integer page, 
            @RequestParam(name = "cat-id", required = false) List<Integer> categoryIds,
            @RequestParam(name = "price_from", required = false) Integer priceFrom,
            @RequestParam(name = "price_to", required = false) Integer priceTo,
            @RequestParam(name = "search", required = false) String search){
        List<Category> categories = categoryService.getAllCategories();
        Page<ProductPreviewForAdminDto> productsPage = productService.getCatalogPageForAdmin(page, categoryIds, priceFrom, priceTo, search);

        model.addAttribute("categories", categories);
        model.addAttribute("products", productsPage.toList());

        Integer currentPage = productsPage.getNumber()+1;
        Integer lastPage = productsPage.getTotalPages();
        model.addAttribute("firstPage", pagination.getFirstPage(currentPage));
        model.addAttribute("pagesBefore", pagination.getPagesBefore(currentPage));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pagesAfter", pagination.getPagesAfter(currentPage, lastPage));
        model.addAttribute("lastPage", pagination.getLastPage(currentPage, lastPage));

        return "admin/products_list";
    }

}
