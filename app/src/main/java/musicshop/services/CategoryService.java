package musicshop.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.dto.CategoryFullDto;
import musicshop.entities.Category;
import musicshop.repositories.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    @Transactional
    public void deleteCategoryById(Long categoryId){
        Category category = new Category();
        category.setId(categoryId);
        categoryRepository.delete(category);
    }

    @Transactional
    public Category createCategory(String categoryName){
        Category category = new Category();
        category.setName(categoryName);
        return categoryRepository.save(category);
    }

    public List<CategoryFullDto> getCategoriesWithoutByProductId(Long productId){
        Long categoryId = categoryRepository.findIdByProductId(productId);
        if(categoryId == null){
            return categoryRepository.findFullDtos();
        }
        return categoryRepository.findByIdNot(categoryId);
    }

}
