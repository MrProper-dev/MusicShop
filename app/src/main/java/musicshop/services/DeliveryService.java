package musicshop.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import musicshop.dto.DeliveryDto;
import musicshop.entities.Delivery;
import musicshop.entities.Product;
import musicshop.entities.ProductDelivery;
import musicshop.entities.keys.ProductDeliveryId;
import musicshop.mappers.DeliveryMapper;
import musicshop.repositories.DeliveryRepositroy;
import musicshop.repositories.ProductDeliveryRepository;

//TODO: сделать создание поставки автосатически
@Service
public class DeliveryService {

    private final Integer DELIVERIES_LIST_SIZE = 5; 

    @Autowired
    private DeliveryRepositroy deliveryRepositroy;

    @Autowired
    private ProductDeliveryRepository productDeliveryRepository;

    @Autowired
    private DeliveryMapper deliveryMapper;

    public void putProductToActiveDelivery(Long productId, Integer quantity, Long adminId){
        if(quantity == null || quantity <= 0) throw new IllegalArgumentException();
        Delivery delivery = deliveryRepositroy.findByAdminIdAndStatus(adminId, Delivery.Status.CREATED);
        if(delivery == null) throw new IllegalStateException();
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
            deliveries = deliveryRepositroy.findWithAdminAndWithProductByStatusAndTimestampAfterAndTimestampBefore(Delivery.Status.ISSUED, from.atStartOfDay(), to.atStartOfDay(), pageable);
        }else if(from != null){
            deliveries = deliveryRepositroy.findWithAdminAndWithProductByStatusAndTimestampAfter(Delivery.Status.ISSUED, from.atStartOfDay(), pageable);
        }else if(to != null){
            deliveries = deliveryRepositroy.findWithAdminAndWithProductByStatusAndTimestampBefore(Delivery.Status.ISSUED, to.atStartOfDay(), pageable);
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

}
