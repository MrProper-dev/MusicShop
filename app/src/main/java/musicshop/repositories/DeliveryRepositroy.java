package musicshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.Delivery;

@Repository
public interface DeliveryRepositroy extends JpaRepository<Delivery, Long>{

    Delivery findByStatus(Delivery.Status status);

}
