package musicshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.Seller;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Seller findByLogin(String login);

    Boolean existsByLogin(String login);

}
