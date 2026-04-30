package musicshop.services;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.App;
import musicshop.entities.Picture;
import musicshop.repositories.PictureRepository;

@Service
public class PictureService {
    
    private final String PICTURES_PATH = App.RESOURCES_PATH + "/static/pictures/";

    @Autowired
    private PictureRepository pictureRepository;

    @Transactional
    public void deletePicturesByIds(List<Long> ids){
        List<Picture> pictures = pictureRepository.findByIdIn(ids);
        for (Picture picture : pictures) {
            File file = new File(PICTURES_PATH + picture.getPath());
            file.delete();
        }
        pictureRepository.deleteByIdIn(ids);
    }

}
