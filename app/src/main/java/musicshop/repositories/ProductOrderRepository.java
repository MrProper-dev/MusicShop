package musicshop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musicshop.entities.ProductOrder;
import musicshop.entities.keys.ProductOrderId;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, ProductOrderId>{

    @EntityGraph(attributePaths = "product.pictures")
    List<ProductOrder> findByOrderId(Long orderId);

    @Modifying
    @Query("DELETE FROM ProductOrder po WHERE po.order.id = :orderId AND po.product.id = :productId")
    int deleteByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);

    @Modifying
    @Query("UPDATE ProductOrder po SET po.quantity = :quantity WHERE po.order.id = :orderId AND po.product.id = :productId")
    int updateQuantityByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

}
