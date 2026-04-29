package musicshop.controllers.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import musicshop.entities.Category;
import musicshop.services.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryRestController {

    @Autowired
    private CategoryService categoryService;

    @DeleteMapping("/{id}")
    public void deleteCategiry(@PathVariable("id") Long productId){
        categoryService.deleteCategoryById(productId);
    }

    @PostMapping
    public Long createCategory(@RequestBody Map<String, String> requestBody){
        String categoryName = requestBody.get("name");
        if(categoryName == null || categoryName.isEmpty()){
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Category category = categoryService.createCategory(categoryName);
        return category.getId();
    }

}
