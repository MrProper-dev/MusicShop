package musicshop.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PupController {


    @GetMapping("/login")
    public String client(){
        System.out.println("\nCLIENT\n");
        return "client/log_in";
    }

    @GetMapping("/seller/login")
    public String seller(){
        System.out.println("\nSELLER\n");
        return "seller/log_in";
    }

    @GetMapping("/admin/login")
    public String admin(){
        System.out.println("\nADMIN\n");
        return "admin/log_in";
    }

    @GetMapping("/seller/some")
    public String some(){
        System.out.println("\nSELLER PGE\n");
        return "seller/orders";
    }

}