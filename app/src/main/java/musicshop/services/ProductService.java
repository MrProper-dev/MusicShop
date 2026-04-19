package musicshop.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.entities.Product;
import musicshop.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;


    // ВАЖНО: сессия hibernate доступна в предлеах транзакции, т.е. lazy загрузку можно выполнять только в ней
    @Transactional
    public Product getFirstProductToString(){
        List<Product> products = productRepository.findAll();
        Product product = products.getFirst();
        product.getPictures().size();
        return product;
    }

}
