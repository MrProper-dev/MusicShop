package musicshop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller")
public class SellerController {

    @GetMapping("/login")
    public String seller(){
        return "seller/log_in";
    }

    @GetMapping("/products")
    public String showOrders(){
        return "seller/orders";
    }

}
