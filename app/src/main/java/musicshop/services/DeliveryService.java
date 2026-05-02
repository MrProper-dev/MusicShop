package musicshop.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.dto.DeliveryDto;
import musicshop.entities.Admin;
import musicshop.entities.Delivery;
import musicshop.entities.Product;
import musicshop.entities.ProductDelivery;
import musicshop.entities.keys.ProductDeliveryId;
import musicshop.mappers.DeliveryMapper;
import musicshop.repositories.DeliveryRepositroy;
import musicshop.repositories.ProductDeliveryRepository;

@Service
public class DeliveryService {

    private final Integer DELIVERIES_LIST_SIZE = 5; 

    @Autowired
    private DeliveryRepositroy deliveryRepositroy;

    @Autowired
    private ProductDeliveryRepository productDeliveryRepository;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Transactional
    public void putProductToActiveDelivery(Long productId, Integer quantity, Long adminId){
        if(quantity == null || quantity <= 0) throw new IllegalArgumentException();
        Delivery delivery = deliveryRepositroy.findByAdminIdAndStatus(adminId, Delivery.Status.CREATED);
        if(delivery == null){
            delivery = new Delivery();
            Admin admin = new Admin();
            admin.setId(adminId);
            delivery.setAdmin(admin);
            delivery.setStatus(Delivery.Status.CREATED);
            delivery.setSupplierName("");
            deliveryRepositroy.save(delivery);
        }
        ProductDelivery productDelivery = productDeliveryRepository.findById(new ProductDeliveryId(productId, delivery.getId())).orElse(null);
        if(productDelivery == null){
            productDelivery = new ProductDelivery();
            Product product = new Product();
            product.setId(productId);
            productDelivery.setProduct(product);
            productDelivery.setDelivery(delivery);
            productDelivery.setId(new ProductDeliveryId(productId, delivery.getId()));
            productDelivery.setQuantity(quantity);
        }
        productDeliveryRepository.save(productDelivery);
    }

    public Page<DeliveryDto> getDeliveriesPage(Integer page, LocalDate from, LocalDate to){
        Pageable pageable = PageRequest.of(page == null ? 0 : page < 0 ? 0 : page  , DELIVERIES_LIST_SIZE).withSort(Sort.by("timestamp").descending());
        Page<Delivery> deliveries = null;
        
        
        if(from != null && to != null){
            deliveries = deliveryRepositroy.findWithAdminAndWithProductByStatusAndTimestampAfterAndTimestampBefore(Delivery.Status.ISSUED, from.atStartOfDay(), to.plusDays(1l).atStartOfDay(), pageable);
        }else if(from != null){
            deliveries = deliveryRepositroy.findWithAdminAndWithProductByStatusAndTimestampAfter(Delivery.Status.ISSUED, from.atStartOfDay(), pageable);
        }else if(to != null){
            deliveries = deliveryRepositroy.findWithAdminAndWithProductByStatusAndTimestampBefore(Delivery.Status.ISSUED, to.plusDays(1l).atStartOfDay(), pageable);
        }else{
            deliveries = deliveryRepositroy.findWithAdminAndWithProductByStatus(Delivery.Status.ISSUED, pageable);
        }
        List<DeliveryDto> deliveryDtos = deliveries.getContent().stream()
            .map(deliveryMapper::mapToDeliveryDto)
            .toList();
        return new PageImpl<>(deliveryDtos, pageable, deliveries.getTotalElements()); 
    }

    public DeliveryDto getActualDelivery(Long adminId){
        Delivery delivery = deliveryRepositroy.findWithAdminAndWithProductByAdminIdAndStatus(adminId, Delivery.Status.CREATED);
        return deliveryMapper.mapToDeliveryDto(delivery);
    } 

    @Transactional
    public void deleteProductFromActiveDelivery(Long productId, Long adminId){
        productDeliveryRepository.deleteByProductIdAndDeliveryAdminIdAndDeliveryStatus(productId, adminId, Delivery.Status.CREATED);
    }

    @Transactional
    public void takeActiveDelivery(Long adminId){
        Delivery delivery = deliveryRepositroy.findWithAdminAndWithProductByAdminIdAndStatus(adminId, Delivery.Status.CREATED);
        if(delivery.getProductDeliveries().size() == 0) throw new IllegalStateException();
        for(ProductDelivery pd : delivery.getProductDeliveries()){
            Product product = pd.getProduct();
            product.setQuantity(product.getQuantity() + pd.getQuantity());
        }
        delivery.setTimestamp(LocalDateTime.now());
        delivery.setStatus(Delivery.Status.ISSUED);
        deliveryRepositroy.save(delivery);
    }

    @Transactional
    public void updatDeliverier(String deliverier, Long adminId){
        if(deliverier == null) throw new NullPointerException();
        Delivery delivery = deliveryRepositroy.findByAdminIdAndStatus(adminId, Delivery.Status.CREATED);
        delivery.setSupplierName(deliverier);
        deliveryRepositroy.save(delivery);
    }

    @Transactional
    public void updateProductQuantity(Long productId, Integer quantity, Long adminId){
        ProductDelivery productDelivery = productDeliveryRepository.findByProductIdAndDeliveryAdminIdAndDeliveryStatus(productId, adminId, Delivery.Status.CREATED);
        if(productDelivery == null) throw new IllegalArgumentException();
        if(quantity == null || quantity < 0) throw new IllegalArgumentException();
        productDelivery.setQuantity(quantity);
        productDeliveryRepository.save(productDelivery);
    }

}
