package musicshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.Delivery;
import musicshop.entities.ProductDelivery;
import musicshop.entities.keys.ProductDeliveryId;

@Repository
public interface ProductDeliveryRepository extends JpaRepository<ProductDelivery, ProductDeliveryId>{

    int deleteByProductIdAndDeliveryAdminIdAndDeliveryStatus(Long productId, Long adminId, Delivery.Status status);

    ProductDelivery findByProductIdAndDeliveryAdminIdAndDeliveryStatus(Long productId, Long adminId, Delivery.Status status);

}
