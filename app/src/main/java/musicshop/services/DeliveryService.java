package musicshop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import musicshop.entities.Delivery;
import musicshop.repositories.DeliveryRepositroy;

@Service
//TODO: доделать
public class DeliveryService {

    @Autowired
    private DeliveryRepositroy deliveryRepositroy;

    public void putProductToActiveDelivery(Long productId, Integer quantity){
        Delivery delivery = deliveryRepositroy.findByStatus(Delivery.Status.CREATED);
        if(delivery == null){
            throw new IllegalStateException();
        }
        
    }

}
