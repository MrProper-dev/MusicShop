package musicshop.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import musicshop.App;
import musicshop.dto.PictureDto;
import musicshop.dto.ProductFullDto;
import musicshop.dto.ProductPreviewDto;
import musicshop.dto.ProductPreviewForAdminDto;
import musicshop.entities.Category;
import musicshop.entities.Picture;
import musicshop.entities.Product;
import musicshop.mappers.PictureMapper;
import musicshop.mappers.ProductMapper;
import musicshop.repositories.ProductRepository;

@Service
public class ProductService {

    private final String PICTURES_PATH = App.RESOURCES_PATH + "/static/pictures/";

    private final Integer CATALOG_PAGE_SIZE = 6;

    private final String PREFIX_FILE_NAME = "product_pic_";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    public PictureMapper pictureMapper;

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

    @Transactional
    public List<PictureDto> updateProduct(Long productId, Long categoryId, String name, String description, Float price, Integer quantity, MultipartFile mainPicture, List<MultipartFile> pictures, Long mainPictureId){
        if(productId == null || categoryId == null || name == null || description == null || price == null || quantity == null){
            throw new IllegalArgumentException("Ome of the params is null");
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found")); 
        Category newCategory = new Category();
        newCategory.setId(categoryId);
        product.setCategory(newCategory);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        List<Picture> currentPictures = product.getPictures();
        if(mainPictureId != null){
            for (Picture picture : currentPictures) {
                if(picture.getId() == mainPictureId) picture.setIsMain(true);
            }
        }
        Integer count = (int) currentPictures.stream().mapToLong(p -> p.getId()).max().orElseGet(() -> 0l);
        if(mainPicture != null){
            Picture currentMainPicture = currentPictures.stream().filter(p -> p.getIsMain()).findFirst().orElse(null);
            if(currentMainPicture != null) currentPictures.remove(currentMainPicture);
            String contentType = mainPicture.getContentType();
            if(contentType == null || !contentType.startsWith("image/")){
                throw new RuntimeException("File is not a pciture");
            }
            String originFileName = mainPicture.getOriginalFilename();
            String pictureFormat = null;
            if(originFileName == null){
                pictureFormat = "jpg";
            }else{
                pictureFormat = originFileName.split("\\.")[1];
            }
            String fileName = PREFIX_FILE_NAME + product.getId() + (count++) + "." + pictureFormat;
            Picture newMainPicture = new Picture();
            newMainPicture.setIsMain(true);
            newMainPicture.setPath(fileName);
            newMainPicture.setProduct(product);
            currentPictures.add(newMainPicture);
            try(FileOutputStream fileOS = new FileOutputStream(PICTURES_PATH +  fileName)){
                fileOS.write(mainPicture.getBytes());
            }catch (IOException exception){
                exception.printStackTrace();
                throw new RuntimeException("Something wrong with file load");
            }
        }
        if(pictures != null && !pictures.isEmpty()){
            for(MultipartFile picture : pictures){
                String contentType = picture.getContentType();
                if(contentType == null || !contentType.startsWith("image/")){
                    throw new RuntimeException("File is not a pciture");
                }
                String originFileName = picture.getOriginalFilename();
                String pictureFormat = null;
                if(originFileName == null){
                    pictureFormat = "jpg";
                }else{
                    pictureFormat = originFileName.split("\\.")[1];
                }
                String fileName = PREFIX_FILE_NAME + product.getId() + (count++) + "." + pictureFormat;
                Picture newPicture = new Picture();
                newPicture.setIsMain(false);
                newPicture.setPath(fileName);
                newPicture.setProduct(product);
                currentPictures.add(newPicture);
                try(FileOutputStream fileOS = new FileOutputStream(PICTURES_PATH +  fileName)){
                    fileOS.write(picture.getBytes());
                }catch (IOException exception){
                    exception.printStackTrace();
                    throw new RuntimeException("Something wrong with file load");
                }
            }
        }
        product.setPictures(currentPictures);
        productRepository.save(product);
        List<PictureDto> dtos = new ArrayList<>();
        dtos.add(currentPictures.stream().filter(p -> p.getIsMain()).map(pictureMapper::mapToPictureDto).findFirst().orElse(null));
        currentPictures.stream().filter(p -> !p.getIsMain()).map(pictureMapper::mapToPictureDto).forEach(p -> dtos.add(p));
        return dtos;
    }

}
