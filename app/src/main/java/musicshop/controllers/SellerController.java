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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import musicshop.controllers.util.PaginationForThymeleaf;
import musicshop.dto.OrderPreviewForSellerDto;
import musicshop.dto.PictureFullDto;
import musicshop.dto.ProductFullDto;
import musicshop.dto.ProductPreviewDto;
import musicshop.dto.PurchaseForSellerDto;
import musicshop.entities.Seller;
import musicshop.services.CategoryService;
import musicshop.services.OrderService;
import musicshop.services.ProductService;
import musicshop.services.PurchaseService;

@Controller
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaginationForThymeleaf pagination;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/login")
    public String seller(){
        return "seller/log_in";
    }

    @GetMapping("/products")
    public String showProducts(Model model, 
            @RequestParam(name = "page", required = false) Integer page, 
            @RequestParam(name = "cat-id", required = false) List<Integer> categoryIds,
            @RequestParam(name = "price_from", required = false) Integer priceFrom,
            @RequestParam(name = "price_to", required = false) Integer priceTo,
            @RequestParam(name = "search", required = false) String search){
        List<musicshop.entities.Category> categories = categoryService.getAllCategories();
        Page<ProductPreviewDto> productsPage = productService.getCatalogPage(page, categoryIds, priceFrom, priceTo, search);

        model.addAttribute("categories", categories);
        model.addAttribute("products", productsPage.toList());

        Integer currentPage = productsPage.getNumber()+1;
        Integer lastPage = productsPage.getTotalPages();
        model.addAttribute("firstPage", pagination.getFirstPage(currentPage));
        model.addAttribute("pagesBefore", pagination.getPagesBefore(currentPage));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pagesAfter", pagination.getPagesAfter(currentPage, lastPage));
        model.addAttribute("lastPage", pagination.getLastPage(currentPage, lastPage));

        return "seller/products_list";
    }

    @GetMapping("/products/{id}")
    public String showProduct(Model model, @PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails){
        Seller seller = (Seller) userDetails;
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

        List<Long> purchasesIds = purchaseService.getActiveBySellerId(seller.getId());

        model.addAttribute("product", product);
        model.addAttribute("mainPicture", mainPicture);
        model.addAttribute("purchasesIds", purchasesIds);
        return "seller/product_card";
    }

    @GetMapping("/orders")
    public String showOrders(
            @RequestParam(value = "status", required = false) String status,
            Model model) {
        
        List<OrderPreviewForSellerDto> orders = orderService.getOrders(status);
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status != null ? status : "ALL");
        return "seller/orders";
    }

    @GetMapping("/purchases")
    public String showPurchases(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        Seller seller = (Seller) userDetails;
        List<PurchaseForSellerDto> purchases = purchaseService.getActivePurchases(seller.getId());
        
        model.addAttribute("purchases", purchases);
        return "seller/purchases";
    }

}
