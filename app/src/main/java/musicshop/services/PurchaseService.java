package musicshop.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.dto.PurchaseForAdminDto;
import musicshop.dto.PurchaseForSellerDto;
import musicshop.entities.Product;
import musicshop.entities.ProductPurchase;
import musicshop.entities.Purchase;
import musicshop.entities.Seller;
import musicshop.entities.keys.ProductPurchaseId;
import musicshop.mappers.PurchaseMapper;
import musicshop.repositories.ProductPurchaseRepository;
import musicshop.repositories.ProductRepository;
import musicshop.repositories.PurchaseRepository;

@Service
public class PurchaseService {

    private final Integer PURCHASES_PAGE_SIZE = 5;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductPurchaseRepository productPurchaseRepository;

    @Autowired
    private PurchaseMapper purchaseMapper;

    public List<Long> getActiveBySellerId(Long sellerId){
        return purchaseRepository.findIdsBySellerIdAndStatus(sellerId, Purchase.Status.CREATED, Sort.by("timestamp").ascending());
    }

    @Transactional
    public void addProductToPurhase(Long productId, Long purchaseId, Integer quantity){
        if(quantity <= 0) throw new RuntimeException();
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException());
        Integer actualQuantity = product.getQuantity();
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
        if(actualQuantity < quantity) throw new IllegalArgumentException();
        product.setQuantity(actualQuantity - quantity);
        productRepository.save(product);
        productPurchase.setQuantity(quantity);
        productPurchaseRepository.save(productPurchase);
    }

    public List<PurchaseForSellerDto> getActivePurchases(Long sellerId) {
        List<Purchase> purchases;

        purchases = purchaseRepository.findWithProductPurchasesWithProductBySellerIdAndStatus(
            sellerId, Purchase.Status.CREATED, Sort.by("timestamp").ascending());
        
        return purchases.stream()
                .map(purchaseMapper::mapToPurchaseForSellerDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Purchase createPurchase(Seller seller) {
        Purchase purchase = new Purchase();
        purchase.setSeller(seller);
        purchase.setTimestamp(LocalDateTime.now());
        purchase.setStatus(Purchase.Status.CREATED);
        
        return purchaseRepository.save(purchase);
    }

    @Transactional
    public void removeProductFromPurchase(Long purchaseId, Long sellerId, Long productId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));
        
        if (!purchase.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("Access denied");
        }
        
        if (purchase.getStatus() != Purchase.Status.CREATED) {
            throw new RuntimeException("Can't delete product from issued purchase");
        }
        
        ProductPurchase productPurchase = productPurchaseRepository.findById(new ProductPurchaseId(purchaseId, productId)).orElseThrow(() -> new RuntimeException());
        Product product = productPurchase.getProduct();
        Integer newQuantity = product.getQuantity() + productPurchase.getQuantity();
        productRepository.updateQuantityById(productId, newQuantity);
        productPurchaseRepository.delete(productPurchase);
    }

    @Transactional
    public void deletePurchase(Long purchaseId, Long sellerId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));
        
        if (!purchase.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("Access denied");
        }
        
        if (purchase.getStatus() != Purchase.Status.CREATED) {
            throw new RuntimeException("Can't delete issued purchase");
        }
        productPurchaseRepository.findWithProductByPurchaseId(purchaseId);
        for(ProductPurchase pp : purchase.getProductPurchases()){
            Integer productQuantity = pp.getProduct().getQuantity();
            pp.getProduct().setQuantity(productQuantity + pp.getQuantity());
        }
        purchaseRepository.delete(purchase);
    }

    @Transactional
    public void checkoutPurchase(Long purchaseId, Long sellerId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));
        
        if (!purchase.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("Access denied");
        }
        
        if (purchase.getStatus() != Purchase.Status.CREATED) {
            throw new RuntimeException("Can't checkout issued purchase");
        }
        
        if (purchase.getProductPurchases().isEmpty()) {
            throw new RuntimeException("Can't checkout empty purchase");
        }
        
        purchase.setStatus(Purchase.Status.ISSUED);
    }

    public Page<PurchaseForAdminDto> getPurchasesForAdmin(String strStatus, LocalDate from, LocalDate to, Integer page){
        Pageable pageable = PageRequest.of(page == null ? 0 : page < 0 ? 0 : page , PURCHASES_PAGE_SIZE).withSort(Sort.by("timestamp").descending());
        Page<Purchase> purchases = null;
        List<Purchase.Status> statuses = new ArrayList<>();
        statuses.add(Purchase.Status.CREATED);
        statuses.add(Purchase.Status.ISSUED);
        if(strStatus != null){
            if(strStatus.equals("CREATED")) statuses.remove(1);
            if(strStatus.equals("ISSUED")) statuses.remove(0);
        }
        if(from != null && to != null && from.isBefore(to)){
            purchases = purchaseRepository.findWithProductPurchasesWithProductAndWithSellerByStatusInAndTimestampAfterAndTimestampBefore(statuses, from.atStartOfDay(), to.plusDays(1l).atStartOfDay(), pageable);
        }else if(from != null){
            purchases = purchaseRepository.findWithProductPurchasesWithProductAndWithSellerByStatusInAndTimestampAfter(statuses, from.atStartOfDay(), pageable);
        }else if(to != null){
            purchases = purchaseRepository.findWithProductPurchasesWithProductAndWithSellerByStatusInAndTimestampBefore(statuses, to.plusDays(1l).atStartOfDay(), pageable);
        }else{
            purchases = purchaseRepository.findWithProductPurchasesWithProductAndWithSellerByStatusIn(statuses, pageable);
        }

        List<PurchaseForAdminDto> dtos = purchases.getContent().stream()
            .map(purchaseMapper::mapToPurchaseForAdminDto)
            .toList();

        return new PageImpl<>(dtos, pageable, purchases.getTotalElements());
    }

}
