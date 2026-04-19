package musicshop.entities.keys;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeliveryId implements Serializable {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "delivery_id")
    private Long deliveryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDeliveryId)) return false;
        ProductDeliveryId that = (ProductDeliveryId) o;
        return Objects.equals(productId, that.productId) && Objects.equals(deliveryId, that.deliveryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, deliveryId);
    }
}