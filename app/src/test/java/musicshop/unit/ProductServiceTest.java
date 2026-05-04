package musicshop.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import musicshop.dto.ProductFullDto;
import musicshop.dto.ProductPreviewDto;
import musicshop.entities.Product;
import musicshop.mappers.ProductMapper;
import musicshop.repositories.ProductRepository;
import musicshop.services.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Должен возвращать страницу каталога по поисковому запросу")
    void getCatalogPage_WithSearch_ReturnsSearchPage() {
        String search = "Guitar";
        Page<ProductPreviewDto> expectedPage = new PageImpl<>(Collections.emptyList());

        when(productRepository.findAllWithoutCategoryAndPicturesAndDescriptionBySearchNameAndPriceNot(
                any(Pageable.class), eq(search), eq(0f)))
            .thenReturn(expectedPage);
        
        Page<ProductPreviewDto> result = productService.getCatalogPage(0, null, null, null, search);

        
        assertEquals(expectedPage, result);
        verify(productRepository, times(1))
            .findAllWithoutCategoryAndPicturesAndDescriptionBySearchNameAndPriceNot(any(), anyString(), anyFloat());
    }

    @Test
    @DisplayName("Должен успешно создавать новый пустой продукт")
    void createProduct_ReturnsSavedProduct() {        
        String name = "New Piano";
        Product productToSave = new Product();
        productToSave.setName(name);
        
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);

        Product result = productService.createProduct(name);
        
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(0, result.getQuantity());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Должен выбрасывать исключение при обновлении, если продукт не найден")
    void updateProduct_ProductNotFound_ThrowsException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(productId, 1L, "Name", "Desc", 100f, 10, null, null, null);
        });
    }

    @Test
    @DisplayName("Должен вызывать удаление продукта через репозиторий")
    void deleteProductById_CallsRepositoryDelete() {
        Long id = 10L;
        
        productService.deleteProductById(id);
        
        verify(productRepository, times(1)).delete(argThat(product -> product.getId().equals(id)));
    }
    
    @Test
    @DisplayName("Должен корректно маппить данные при получении продукта по ID")
    void getFullDataById_ReturnsMappedDto() {
        Long id = 1L;
        Product product = new Product();
        ProductFullDto dto = new ProductFullDto();
        
        when(productRepository.findByIdWithFullData(id)).thenReturn(product);
        when(productMapper.mapToProductFullDto(product)).thenReturn(dto);
        
        ProductFullDto result = productService.getFullDataById(id);
        
        assertEquals(dto, result);
        verify(productRepository).findByIdWithFullData(id);
    }
}