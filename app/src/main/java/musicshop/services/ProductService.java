package musicshop.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.dto.ProductFullDto;
import musicshop.dto.ProductPreviewDto;
import musicshop.dto.ProductPreviewForAdminDto;
import musicshop.entities.Product;
import musicshop.mappers.ProductMapper;
import musicshop.repositories.ProductRepository;

@Service
public class ProductService {

    private final Integer CATALOG_PAGE_SIZE = 6;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public Page<ProductPreviewDto> getCatalogPage(Integer page, List<Integer> categoryIds, Float priceFrom, Float priceTo, String search){
        Pageable pageable = PageRequest.of(page == null ? 0 : page < 0 ? 0 : page  , CATALOG_PAGE_SIZE);
        Page<ProductPreviewDto> productsPage;
        if(search != null && !search.isEmpty()){
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionBySearchNameAndPriceNot(pageable, search, 0f);
            return productsPage;
        }
        if(!(priceFrom != null && priceFrom >= 0)) priceFrom = 0.01f;
        if(!(priceTo != null && priceTo >= 0)) priceTo = Float.MAX_VALUE;
        if(categoryIds != null && !categoryIds.isEmpty()){
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionByCategoryIdsAndPriceBetweenAndPriceNot(pageable, categoryIds, priceFrom, priceTo, 0f);
        }else{
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionByPriceBetweenAndPriceNot(pageable, priceFrom, priceTo, 0f);
        }
        return productsPage;
    }

    public ProductFullDto getFullDataById(Long id){
        return productMapper.mapToProductFullDto(productRepository.findByIdWithFullData(id));
    }

    public Page<ProductPreviewForAdminDto> getCatalogPageForAdmin(Integer page, List<Integer> categoryIds, Float priceFrom, Float priceTo, String search){
        Pageable pageable = PageRequest.of(page == null ? 0 : page < 0 ? 0 : page  , CATALOG_PAGE_SIZE);
        Page<ProductPreviewForAdminDto> productsPage;
        if(search != null && !search.isEmpty()){
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionAndQuantityBySearchName(pageable, search);
            return productsPage;
        }
        if(!(priceFrom != null && priceFrom >= 0)) priceFrom = 0f;
        if(!(priceTo != null && priceTo >= 0)) priceTo = Float.MAX_VALUE;
        if(categoryIds != null && !categoryIds.isEmpty()){
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionAndQuantityByCategoryIdsAndPrice(pageable, categoryIds, priceFrom, priceTo);
        }else{
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionAndQuantityByPrice(pageable, priceFrom, priceTo);
        }
        return productsPage;
    }

    @Transactional
    public void deleteProductById(Long productId){
        Product product = new Product();
        product.setId(productId);
        productRepository.delete(product);
    }

    @Transactional
    public Product createProduct(String productName){
        Product product = new Product();
        product.setName(productName);
        product.setQuantity(0);
        product.setPrice(0.00f);
        product.setDescription("");
        return productRepository.save(product);
    }

    

}
