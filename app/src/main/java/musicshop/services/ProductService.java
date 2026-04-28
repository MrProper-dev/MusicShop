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

    public Page<ProductPreviewDto> getCatalogPage(Integer page, List<Integer> categoryIds, Integer priceFrom, Integer priceTo, String search){
        Pageable pageable = PageRequest.of(page == null ? 0 : page < 0 ? 0 : page  , CATALOG_PAGE_SIZE);
        Page<ProductPreviewDto> productsPage;
        if(search != null && !search.isEmpty()){
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionBySearchName(pageable, search);
            return productsPage;
        }
        if(!(priceFrom != null && priceFrom >= 0)) priceFrom = 0;
        if(!(priceTo != null && priceTo >= 0)) priceTo = Integer.MAX_VALUE;
        if(categoryIds != null && !categoryIds.isEmpty()){
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionByCategoryIdsAndPrice(pageable, categoryIds, priceFrom, priceTo);
        }else{
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionByPrice(pageable, priceFrom, priceTo);
        }
        return productsPage;
    }

    public ProductFullDto getFullDataById(Long id){
        return productMapper.mapToProductFullDto(productRepository.findByIdWithFullData(id));
    }

    public Page<ProductPreviewForAdminDto> getCatalogPageForAdmin(Integer page, List<Integer> categoryIds, Integer priceFrom, Integer priceTo, String search){
        Pageable pageable = PageRequest.of(page == null ? 0 : page < 0 ? 0 : page  , CATALOG_PAGE_SIZE);
        Page<ProductPreviewForAdminDto> productsPage;
        if(search != null && !search.isEmpty()){
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionAndQuantityBySearchName(pageable, search);
            return productsPage;
        }
        if(!(priceFrom != null && priceFrom >= 0)) priceFrom = 0;
        if(!(priceTo != null && priceTo >= 0)) priceTo = Integer.MAX_VALUE;
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

}
