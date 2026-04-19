package musicshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import musicshop.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
