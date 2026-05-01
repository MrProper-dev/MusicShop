package musicshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.ProductDelivery;
import musicshop.entities.keys.ProductDeliveryId;

@Repository
public interface ProductDeliveryRepository extends JpaRepository<ProductDelivery, ProductDeliveryId>{

}
