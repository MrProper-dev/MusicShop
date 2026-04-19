package musicshop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import musicshop.entities.keys.ProductPurchaseId;

@Entity
@Table(name = "product_purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPurchase {

    @EmbeddedId
    private ProductPurchaseId id;

    @ManyToOne
    @MapsId("purchaseId")
    @JoinColumn(name = "purchese_id")
    private Purchase purchase;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
}