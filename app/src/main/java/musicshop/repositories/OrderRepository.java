package musicshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musicshop.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{

    @Query("select o from Order o join fetch o.productOrders where o.client.id = :clientId and o.status = 'NULL'")
    public Order findBasketByClientId(@Param("clientId") Long clientId);

}
