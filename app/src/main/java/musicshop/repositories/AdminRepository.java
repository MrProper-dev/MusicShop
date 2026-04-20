package musicshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    public Admin findByLogin(String login);

}
