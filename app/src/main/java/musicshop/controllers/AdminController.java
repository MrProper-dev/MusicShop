package musicshop.controllers;

import java.time.LocalDate;
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
import musicshop.dto.CategoryFullDto;
import musicshop.dto.DeliveryDto;
import musicshop.dto.PictureFullDto;
import musicshop.dto.ProductFullDto;
import musicshop.dto.ProductPreviewForAdminDto;
import musicshop.dto.PurchaseForAdminDto;
import musicshop.entities.Admin;
import musicshop.entities.Category;
import musicshop.entities.Seller;
import musicshop.services.CategoryService;
import musicshop.services.DeliveryService;
import musicshop.services.ProductService;
import musicshop.services.PurchaseService;
import musicshop.services.SellerService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaginationForThymeleaf pagination;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/login")
    public String admin(){
        return "admin/log_in";
    }

    @GetMapping("/products")
    public String showProducts(Model model, 
            @RequestParam(name = "page", required = false) Integer page, 
            @RequestParam(name = "cat-id", required = false) List<Integer> categoryIds,
            @RequestParam(name = "price_from", required = false) Float priceFrom,
            @RequestParam(name = "price_to", required = false) Float priceTo,
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

        List<CategoryFullDto> categories = categoryService.getCategoriesWithoutByProductId(id);

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("mainPicture", mainPicture);
        return "admin/product_card";
    }

    @GetMapping("/sellers")
    public String showSellers(Model model){
        List<Seller> sellers = sellerService.findAllSellers();

        model.addAttribute("sellers", sellers);
        return "admin/sellers";
    }

    @GetMapping("/deliveries")
    public String showDeliveries(
        Model model,
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "from", required = false) LocalDate from,
        @RequestParam(name = "to", required = false) LocalDate to){
        Admin admin = (Admin) userDetails;
        Page<DeliveryDto> deliveries = deliveryService.getDeliveriesPage(page, from, to);
        DeliveryDto actualDelivery = deliveryService.getActualDelivery(admin.getId());

        model.addAttribute("deliveries", deliveries);
        model.addAttribute("actualDelivery", actualDelivery);

        Integer currentPage = deliveries.getNumber()+1;
        Integer lastPage = deliveries.getTotalPages();
        model.addAttribute("firstPage", pagination.getFirstPage(currentPage));
        model.addAttribute("pagesBefore", pagination.getPagesBefore(currentPage));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pagesAfter", pagination.getPagesAfter(currentPage, lastPage));
        model.addAttribute("lastPage", pagination.getLastPage(currentPage, lastPage));
        return "admin/deliveries";
    }

    @GetMapping("/purchases")
    public String showPurchases(
        Model model,
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "from", required = false) LocalDate from,
        @RequestParam(name = "to", required = false) LocalDate to){
        Page<PurchaseForAdminDto> purchases = purchaseService.getPurchasesForAdmin(status, from, to, page);

        model.addAttribute("purchases", purchases);
        model.addAttribute("currentStatus", status != null ? status : "ALL");
        
        Integer currentPage = purchases.getNumber()+1;
        Integer lastPage = purchases.getTotalPages();
        model.addAttribute("firstPage", pagination.getFirstPage(currentPage));
        model.addAttribute("pagesBefore", pagination.getPagesBefore(currentPage));
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pagesAfter", pagination.getPagesAfter(currentPage, lastPage));
        model.addAttribute("lastPage", pagination.getLastPage(currentPage, lastPage));
        return "admin/purchases";
    }

}
