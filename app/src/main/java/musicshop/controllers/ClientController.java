package musicshop.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import musicshop.controllers.util.PaginationForThymeleaf;
import musicshop.dto.BasketDto;
import musicshop.dto.OrderPreviewForClientDto;
import musicshop.dto.PictureFullDto;
import musicshop.dto.ProductFullDto;
import musicshop.dto.ProductPreviewForClientDto;
import musicshop.entities.Category;
import musicshop.entities.Client;
import musicshop.services.CategoryService;
import musicshop.services.OrderService;
import musicshop.services.ProductService;

@Controller
public class ClientController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PaginationForThymeleaf pagination;

    @Autowired
    private OrderService orderService;

    @GetMapping("/login")
    public String login(){
        return "client/log_in";
    }

    @GetMapping("/products")
    public String showProducts(Model model, 
            @RequestParam(name = "page", required = false) Integer page, 
            @RequestParam(name = "cat-id", required = false) List<Integer> categoryIds,
            @RequestParam(name = "price_from", required = false) Integer priceFrom,
            @RequestParam(name = "price_to", required = false) Integer priceTo,
            @RequestParam(name = "search", required = false) String search){
        List<Category> categories = categoryService.getAllCategories();
        Page<ProductPreviewForClientDto> productsPage = productService.getCatalogPageForClient(page, categoryIds, priceFrom, priceTo, search);

        model.addAttribute("categories", categories);
        model.addAttribute("products", productsPage.toList());

        Integer currentPage = productsPage.getNumber()+1;
        Integer lastPage = productsPage.getTotalPages();
        model.addAttribute("firstPage", pagination.getFirstPage(currentPage));
        model.addAttribute("pagesBefore", pagination.getPagesBefore(currentPage));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pagesAfter", pagination.getPagesAfter(currentPage, lastPage));
        model.addAttribute("lastPage", pagination.getLastPage(currentPage, lastPage));

        return "client/products_list";
    }

    @GetMapping("/products/{id}")
    public String showProduct(Model model, @PathVariable("id") Long id){
        ProductFullDto product = null;
        try{
            product = productService.getFullDataById(id);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatusCode.valueOf(404));
        }
        
        List<PictureFullDto> pictures = product.getPictures();
        PictureFullDto mainPicture = null;
        for (PictureFullDto picture : pictures) {
            if(picture.getIsMain()){
                mainPicture = picture;
                break;
            }
        }

        model.addAttribute("product", product);
        model.addAttribute("mainPicture", mainPicture);
        return "client/product_card";
    }

    @GetMapping("/orders")
    public String showOrders(
            @RequestParam(name = "status", required = false) String status,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        Client client = (Client) userDetails;
        List<OrderPreviewForClientDto> orders = orderService.getOrdersByClient(client.getId(), status);
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status != null ? status : "ALL");
        return "client/orders";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, @AuthenticationPrincipal UserDetails userDetails){
        Client client = (Client) userDetails;
        
        model.addAttribute("profile", client);
        return "client/profile";
    }

    @GetMapping("/basket")
    public String showBasket(Model model, @AuthenticationPrincipal UserDetails userDetails){
        Client client = (Client) userDetails;
        BasketDto basket = orderService.getBasket(client.getId());
        
        model.addAttribute("basket", basket);
        return "client/basket";
    }
    
}
