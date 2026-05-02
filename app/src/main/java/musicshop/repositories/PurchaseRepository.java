package musicshop.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musicshop.entities.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>{

    @Query("select p.id from Purchase p where p.seller.id = :sellerId and p.status = :status")
    List<Long> findIdsBySellerIdAndStatus(@Param("sellerId") Long sellerId, @Param("status") Purchase.Status status, Sort sort);

    @EntityGraph(attributePaths = "productPurchases.product")
    List<Purchase> findWithProductPurchasesWithProductBySellerIdAndStatus(Long id, Purchase.Status status, Sort sort);

    @EntityGraph(attributePaths = {"productPurchases.product", "seller"})
    Page<Purchase> findWithProductPurchasesWithProductAndWithSellerByStatusIn(List<Purchase.Status> statuses, Pageable pageable);
    
    @EntityGraph(attributePaths = {"productPurchases.product", "seller"})
    Page<Purchase> findWithProductPurchasesWithProductAndWithSellerByStatusInAndTimestampAfter(List<Purchase.Status> statuses, LocalDateTime from, Pageable pageable);

    @EntityGraph(attributePaths = {"productPurchases.product", "seller"})
    Page<Purchase> findWithProductPurchasesWithProductAndWithSellerByStatusInAndTimestampBefore(List<Purchase.Status> statuses, LocalDateTime to, Pageable pageable);

    @EntityGraph(attributePaths = {"productPurchases.product", "seller"})
    Page<Purchase> findWithProductPurchasesWithProductAndWithSellerByStatusInAndTimestampAfterAndTimestampBefore(List<Purchase.Status> statuses, LocalDateTime from, LocalDateTime to, Pageable pageable);
    
}
