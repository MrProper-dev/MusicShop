package musicshop.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musicshop.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{

    @Query("select o from Order o left join fetch o.productOrders where o.client.id = :clientId and o.status = 'NULL'")
    Order findBasketByClientId(@Param("clientId") Long clientId);

    @EntityGraph(attributePaths = "productOrders.product")
    List<Order> findByClientIdAndStatusNotOrderByTimestampDesc(Long clientId, Order.Status status);
    
    @EntityGraph(attributePaths = "productOrders.product")
    List<Order> findByClientIdAndStatusOrderByTimestampDesc(Long clientId, Order.Status status);
    
    @Modifying
    @Query("UPDATE Order o SET o.status = :newStatus WHERE o.id = :orderId AND o.status = :expectedStatus AND o.client.id = :clientId")
    int updateStatusByIdAndPastStatusAndClientId(@Param("orderId") Long orderId, 
                    @Param("newStatus") Order.Status newStatus, 
                    @Param("expectedStatus") Order.Status expectedStatus,
                    @Param("clientId") Long clientId);

    @EntityGraph(attributePaths = "productOrders")
    Order findByClientIdAndStatus(Long clientId, Order.Status status);

    @Query("SELECT o.id FROM Order o WHERE o.client.id = :clientId AND o.status = :status")
    Long findIdByClientIdAndStatus(@Param("clientId") Long clientId, @Param("status") Order.Status status);

    @EntityGraph(attributePaths = {"productOrders.product", "client"})
    List<Order> findWithClientAndProductOrdersAndProductByStatusInOrderByTimestampDesc(List<Order.Status> statuses);
    
    @Modifying
    @Query("UPDATE Order o SET o.status = :newStatus WHERE o.id = :orderId AND o.status = :expectedStatus")
    int updateStatusByIdAndPastStatus(@Param("orderId") Long orderId, 
                          @Param("newStatus") Order.Status newStatus, 
                          @Param("expectedStatus") Order.Status expectedStatus);

    @EntityGraph(attributePaths = {"productOrders.product", "client"})
    Page<Order> findWithClientAndProductOrdersWithProductByStatusIn(List<Order.Status> statuses, Pageable pageable);

    @EntityGraph(attributePaths = {"productOrders.product", "client"})
    Page<Order> findWithClientAndProductOrdersWithProductByStatusInAndTimestampAfter(List<Order.Status> statuses, LocalDateTime from, Pageable pageable);

    @EntityGraph(attributePaths = {"productOrders.product", "client"})
    Page<Order> findWithClientAndProductOrdersWithProductByStatusInAndTimestampBefore(List<Order.Status> statuses, LocalDateTime to, Pageable pageable);

    @EntityGraph(attributePaths = {"productOrders.product", "client"})
    Page<Order> findWithClientAndProductOrdersWithProductByStatusInAndTimestampAfterAndTimestampBefore(List<Order.Status> statuses, LocalDateTime from, LocalDateTime to, Pageable pageable);

}
