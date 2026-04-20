package musicshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    public Client findByPhone(String phone);

}
