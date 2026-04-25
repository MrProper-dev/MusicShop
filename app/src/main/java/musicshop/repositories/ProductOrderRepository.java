package musicshop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.ProductOrder;
import musicshop.entities.keys.ProductOrderId;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, ProductOrderId>{

    @EntityGraph(attributePaths = "product.pictures")
    List<ProductOrder> findByOrderId(Long orderId);

}
