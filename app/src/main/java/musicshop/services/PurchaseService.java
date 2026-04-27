package musicshop.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.entities.Product;
import musicshop.entities.ProductPurchase;
import musicshop.entities.Purchase;
import musicshop.entities.keys.ProductPurchaseId;
import musicshop.repositories.ProductPurchaseRepository;
import musicshop.repositories.ProductRepository;
import musicshop.repositories.PurchaseRepository;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductPurchaseRepository productPurchaseRepository;

    public List<Long> getActiveBySellerId(Long sellerId){
        return purchaseRepository.findIdsBySellerIdAndStatus(sellerId, Purchase.Status.CREATED, Sort.by("timestamp").ascending());
    }

    @Transactional
    public void addProductToPurhase(Long productId, Long purchaseId, Integer quantity){
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException());
        Integer actualQuantity = product.getQuantity();
        if(actualQuantity < quantity) throw new IllegalArgumentException();
        ProductPurchase productPurchase = productPurchaseRepository.findById(new ProductPurchaseId(purchaseId, productId)).orElse(null);
        if(productPurchase == null){
            productPurchase = new ProductPurchase();
            Purchase purchase = new Purchase();
            purchase.setId(purchaseId);
            productPurchase.setProduct(product);
            productPurchase.setPurchase(purchase);
            productPurchase.setId(new ProductPurchaseId(purchaseId, productId));
        }else{
            actualQuantity += productPurchase.getQuantity();
        }
        product.setQuantity(actualQuantity - quantity);
        productRepository.save(product);
        productPurchase.setQuantity(quantity);
        productPurchaseRepository.save(productPurchase);
    }

}
