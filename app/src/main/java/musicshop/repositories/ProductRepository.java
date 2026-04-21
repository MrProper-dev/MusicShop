package musicshop.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musicshop.dto.ProductPreviewForClientDto;
import musicshop.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    
    @Query("select new musicshop.dto.ProductPreviewForClientDto(p.id, p.name, p.price, pic.path) from Product p left join p.pictures pic on pic.isMain = true where p.price between :from and :to")
    public Page<ProductPreviewForClientDto> findAllWithoutCategoryAndPicturesAndDescriptionByPrice(Pageable pageable, @Param("from") Integer from, @Param("to") Integer to);

    @Query("select new musicshop.dto.ProductPreviewForClientDto(p.id, p.name, p.price, pic.path) from Product p left join p.pictures pic on pic.isMain = true where p.category.id in :categoryIds and p.price between :from and :to")
    public Page<ProductPreviewForClientDto> findAllWithoutCategoryAndPicturesAndDescriptionByCategoryIdsAndPrice(Pageable pageable, @Param("categoryIds") List<Integer> categoryIds, @Param("from") Integer from, @Param("to") Integer to);

}
