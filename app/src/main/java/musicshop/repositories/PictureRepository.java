package musicshop.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musicshop.entities.Picture;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long>{

    int deleteByIdIn(List<Long> ids);

    List<Picture> findByIdIn(List<Long> ids);

}