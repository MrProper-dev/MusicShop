package musicshop.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import musicshop.dto.CategoryFullDto;
import musicshop.dto.PictureFullDto;
import musicshop.dto.ProductFullDto;
import musicshop.dto.ProductPreviewForClientDto;
import musicshop.entities.Category;
import musicshop.entities.Picture;
import musicshop.entities.Product;
import musicshop.repositories.ProductRepository;

@Service
public class ProductService {

    private final Integer CLIENT_CATALOG_PAGE_SIZE = 6;

    @Autowired
    private ProductRepository productRepository;

    public Page<ProductPreviewForClientDto> getCatalogPageForClient(Integer page, List<Integer> categoryIds, Integer priceFrom, Integer priceTo, String search){
        Pageable pageable = PageRequest.of(page == null ? 0 : page < 0 ? 0 : page  , CLIENT_CATALOG_PAGE_SIZE);
        Page<ProductPreviewForClientDto> productsPage;
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
        return map(productRepository.findByIdWithFullData(id));
    }

    private ProductFullDto map(Product product) {
        Category category = product.getCategory();
        List<Picture> pictures = product.getPictures();
        ProductFullDto dto = new ProductFullDto();
        dto.setId(product.getId());
        dto.setCategory(new CategoryFullDto(category.getId(), category.getName()));
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        List<PictureFullDto> pictureDtos = new ArrayList<>();
        pictures.forEach(pic -> {
            pictureDtos.add(new PictureFullDto(pic.getId(), pic.getPath(), pic.getIsMain()));
        });
        dto.setPictures(pictureDtos);
        return dto;
    }

}
