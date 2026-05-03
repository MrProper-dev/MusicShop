package musicshop.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musicshop.dto.ClientPreviewDto;
import musicshop.entities.Client;
import musicshop.entities.Order;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByPhone(String phone);

    @Query("""
            select new musicshop.dto.ClientPreviewDto(c.id, c.fullName, c.phone, COUNT(DISTINCT o.id), COALESCE(SUM(po.quantity * p.price), 0))
            FROM Client c
            LEFT JOIN c.orders o ON o.status NOT IN :statuses
            LEFT JOIN o.productOrders po
            LEFT JOIN po.product p
            where lower(c.fullName) like lower(concat('%', :search, '%')) OR lower(c.phone) like lower(concat('%', :search, '%'))
            GROUP BY c.id, c.fullName, c.phone
            """)
    Page<ClientPreviewDto> findClientPreviewDtoByOrderStatusNotIn(@Param("statuses") List<Order.Status> statuses, @Param("search") String search, Pageable pageable);

    @EntityGraph(attributePaths = "orders.seller")
    Client findWithOrdersWithSellerByIdAndOrdersStatusNotIn(Long id, List<Order.Status> statuses);

}
