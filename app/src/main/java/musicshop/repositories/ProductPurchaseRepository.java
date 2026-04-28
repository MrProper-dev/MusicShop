package musicshop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.ProductPurchase;
import musicshop.entities.keys.ProductPurchaseId;

@Repository
public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, ProductPurchaseId>{

    @EntityGraph(attributePaths = "product")
    List<ProductPurchase> findWithProductByPurchaseId(Long purchaseId);

}
