package musicshop.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.Delivery;

@Repository
public interface DeliveryRepositroy extends JpaRepository<Delivery, Long>{

    Delivery findByAdminIdAndStatus(Long adminId, Delivery.Status status);

    @EntityGraph(attributePaths = {"admin", "productDeliveries.product"})
    Page<Delivery> findWithAdminAndWithProductByStatus(Delivery.Status status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"admin", "productDeliveries.product"})
    Page<Delivery> findWithAdminAndWithProductByStatusAndTimestampAfterAndTimestampBefore(Delivery.Status status, LocalDateTime from, LocalDateTime to, Pageable pageable);

    @EntityGraph(attributePaths = {"admin", "productDeliveries.product"})
    Page<Delivery> findWithAdminAndWithProductByStatusAndTimestampAfter(Delivery.Status status, LocalDateTime from, Pageable pageable);

    @EntityGraph(attributePaths = {"admin", "productDeliveries.product"})
    Page<Delivery> findWithAdminAndWithProductByStatusAndTimestampBefore(Delivery.Status status, LocalDateTime to, Pageable pageable);

    @EntityGraph(attributePaths = {"admin", "productDeliveries.product"})
    Delivery findWithAdminAndWithProductByAdminIdAndStatus(Long adminId, Delivery.Status status);

}
