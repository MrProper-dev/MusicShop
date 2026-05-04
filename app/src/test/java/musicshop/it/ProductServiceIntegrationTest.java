package musicshop.it;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.transaction.Transactional;
import musicshop.config.OrmConfigTest;
import musicshop.config.RootConfigTest;
import musicshop.dto.ProductFullDto;
import musicshop.dto.ProductPreviewDto;
import musicshop.entities.Picture;
import musicshop.entities.Product;
import musicshop.repositories.PictureRepository;
import musicshop.repositories.ProductRepository;
import musicshop.services.ProductService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {OrmConfigTest.class, RootConfigTest.class})
@Transactional
@Sql(scripts = "/data-init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Test
    void getCatalogPage_ShouldReturnFilteredProducts() {
        Page<ProductPreviewDto> page = productService.getCatalogPage(0, List.of(1), 100f, 2000f, null);

        assertNotNull(page);
        assertFalse(page.getContent().isEmpty());
    }

    @Test
    void createProduct_ShouldSaveToRealDb() {
        Product saved = productService.createProduct("Yamaha F310");

        Optional<Product> found = productRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Yamaha F310", found.get().getName());
    }

    @Test
    void getFullDataById_ShouldFetchWithPicturesAndCategory() {
        Long testId = 1L; 
        
        ProductFullDto dto = productService.getFullDataById(testId);
        
        assertNotNull(dto);
        assertNotNull(dto.getName()); 
    }

    @Test
    @DisplayName("Должен возвращать товары только из выбранных категорий")
    void getCatalogPage_WithMultipleCategories_ShouldReturnCorrectProducts() {
        List<Integer> categoryIds = List.of(1, 2);
        Page<ProductPreviewDto> page = productService.getCatalogPage(0, categoryIds, 0f, 10000f, null);

        assertNotNull(page);
        page.getContent().forEach(dto -> {
            Product product = productRepository.findById(dto.getId()).orElseThrow();
            assertTrue(categoryIds.contains(product.getCategory().getId().intValue()), 
                "Продукт " + product.getName() + " имеет неверную категорию");
        });
    }

    @Test
    @DisplayName("При удалении продукта должны удаляться и его фотографии")
    void deleteProductById_ShouldRemoveProductAndItsPictures() {
        Long productId = 1L;
        Long pictureId = 1L;

        assertTrue(productRepository.existsById(productId));

        productService.deleteProductById(productId);

        assertFalse(productRepository.existsById(productId), "Продукт не был удален");
        
        Picture picture = pictureRepository.findById(pictureId).orElse(null);
        assertEquals(null, picture, "Картинки продукта не были удалены каскадно");
    }

    @Test
    @DisplayName("Должен обновлять данные продукта в базе")
    void updateProduct_ShouldUpdateFields() {
        Long productId = 1L;
        String newName = "Updated Guitar Name";
        Float newPrice = 999.99f;

        productService.updateProduct(productId, 1L, newName, "New Description", newPrice, 5, null, null, null);

        Product updatedProduct = productRepository.findById(productId).get();
        assertEquals(newName, updatedProduct.getName());
        assertEquals(newPrice, updatedProduct.getPrice());
        assertEquals("New Description", updatedProduct.getDescription());
    }
}