package musicshop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musicshop.dto.CategoryFullDto;
import musicshop.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

    @Query("select p.category.id from Product p where p.id = :productId")
    Long findIdByProductId(@Param("productId") Long productId);

    @Query("select new musicshop.dto.CategoryFullDto(c.id, c.name) from Category c where c.id != :id")
    List<CategoryFullDto> findByIdNot(@Param("id") Long id);

    @Query("select new musicshop.dto.CategoryFullDto(c.id, c.name) from Category c")
    List<CategoryFullDto> findFullDtos();

}
