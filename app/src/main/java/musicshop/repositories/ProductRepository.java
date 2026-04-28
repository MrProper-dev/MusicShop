package musicshop.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musicshop.dto.ProductPreviewDto;
import musicshop.dto.ProductPreviewForAdminDto;
import musicshop.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    
    @Query("select new musicshop.dto.ProductPreviewDto(p.id, p.name, p.price, pic.path) from Product p left join p.pictures pic on pic.isMain = true where p.price between :from and :to")
    Page<ProductPreviewDto> findAllWithoutCategoryAndPicturesAndDescriptionByPrice(Pageable pageable, @Param("from") Integer from, @Param("to") Integer to);

    @Query("select new musicshop.dto.ProductPreviewDto(p.id, p.name, p.price, pic.path) from Product p left join p.pictures pic on pic.isMain = true where p.category.id in :categoryIds and p.price between :from and :to")
    Page<ProductPreviewDto> findAllWithoutCategoryAndPicturesAndDescriptionByCategoryIdsAndPrice(Pageable pageable, @Param("categoryIds") List<Integer> categoryIds, @Param("from") Integer from, @Param("to") Integer to);

    @Query("select new musicshop.dto.ProductPreviewDto(p.id, p.name, p.price, pic.path) from Product p left join p.pictures pic on pic.isMain = true where lower(p.name) like lower(concat('%', :name, '%'))")
    Page<ProductPreviewDto> findAllWithoutCategoryAndPicturesAndDescriptionBySearchName(Pageable pageable, @Param("name") String name);

    @Query("select new musicshop.dto.ProductPreviewForAdminDto(p.id, p.name, p.price, p.quantity, pic.path) from Product p left join p.pictures pic on pic.isMain = true where p.price between :from and :to")
    Page<ProductPreviewForAdminDto> findAllWithoutCategoryAndPicturesAndDescriptionAndQuantityByPrice(Pageable pageable, @Param("from") Integer from, @Param("to") Integer to);

    @Query("select new musicshop.dto.ProductPreviewForAdminDto(p.id, p.name, p.price, p.quantity, pic.path) from Product p left join p.pictures pic on pic.isMain = true where p.category.id in :categoryIds and p.price between :from and :to")
    Page<ProductPreviewForAdminDto> findAllWithoutCategoryAndPicturesAndDescriptionAndQuantityByCategoryIdsAndPrice(Pageable pageable, @Param("categoryIds") List<Integer> categoryIds, @Param("from") Integer from, @Param("to") Integer to);

    @Query("select new musicshop.dto.ProductPreviewForAdminDto(p.id, p.name, p.price, p.quantity, pic.path) from Product p left join p.pictures pic on pic.isMain = true where lower(p.name) like lower(concat('%', :name, '%'))")
    Page<ProductPreviewForAdminDto> findAllWithoutCategoryAndPicturesAndDescriptionAndQuantityBySearchName(Pageable pageable, @Param("name") String name);

    @Query("select p from Product p join fetch p.pictures join fetch p.category where p.id = :id")
    Product findByIdWithFullData(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Product p SET p.quantity = :quantity WHERE p.id = :id")
    int updateQuantityById(@Param("id") Long id, @Param("quantity") Integer quantity);

}
