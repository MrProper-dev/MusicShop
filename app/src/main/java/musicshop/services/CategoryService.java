package musicshop.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import musicshop.entities.Category;
import musicshop.repositories.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public void deleteCategoryById(Long categoryId){
        Category category = new Category();
        category.setId(categoryId);
        categoryRepository.delete(category);
    }

}
