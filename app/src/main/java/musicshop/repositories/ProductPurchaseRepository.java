package musicshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.ProductPurchase;
import musicshop.entities.keys.ProductPurchaseId;

@Repository
public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, ProductPurchaseId>{

}
