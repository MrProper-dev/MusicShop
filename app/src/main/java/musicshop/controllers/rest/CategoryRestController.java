package musicshop.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import musicshop.services.CategoryService;

//TODO: доделать добавление категории и товара
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryRestController {

    @Autowired
    private CategoryService categoryService;

    @DeleteMapping("/{id}")
    public void deleteCategiry(@PathVariable("id") Long productId){
        categoryService.deleteCategoryById(productId);
    }

}
