package musicshop.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import musicshop.dto.ProductPreviewForClientDto;
import musicshop.repositories.ProductRepository;

@Service
public class ProductService {

    private final Integer CLIENT_CATALOG_PAGE_SIZE = 15;

    @Autowired
    private ProductRepository productRepository;

    public Page<ProductPreviewForClientDto> getCatalogPageForClient(Integer page, List<Integer> categoryIds, Integer priceFrom, Integer priceTo){
        Pageable pageable = PageRequest.of(page == null ? 0 : page < 0 ? 0 : page  , CLIENT_CATALOG_PAGE_SIZE);
        Page<ProductPreviewForClientDto> productsPage;
        if(!(priceFrom != null && priceFrom >= 0)) priceFrom = 0;
        if(!(priceTo != null && priceTo >= 0)) priceTo = Integer.MAX_VALUE;
        if(categoryIds != null && !categoryIds.isEmpty()){
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionByCategoryIdsAndPrice(pageable, categoryIds, priceFrom, priceTo);
        }else{
            productsPage = productRepository.findAllWithoutCategoryAndPicturesAndDescriptionByPrice(pageable, priceFrom, priceTo);
        }
        return productsPage;
    }

}
