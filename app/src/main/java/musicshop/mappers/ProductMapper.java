package musicshop.mappers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import musicshop.dto.CategoryFullDto;
import musicshop.dto.PictureFullDto;
import musicshop.dto.ProductFullDto;
import musicshop.entities.Category;
import musicshop.entities.Picture;
import musicshop.entities.Product;

@Component
public class ProductMapper {

    public ProductFullDto mapToProductFullDto(Product product) {
        Category category = product.getCategory();
        List<Picture> pictures = product.getPictures();
        ProductFullDto dto = new ProductFullDto();
        dto.setId(product.getId());
        if(category != null){
            dto.setCategory(new CategoryFullDto(category.getId(), category.getName()));
        }
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
