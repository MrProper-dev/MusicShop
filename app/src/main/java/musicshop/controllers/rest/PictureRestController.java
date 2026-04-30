package musicshop.controllers.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import musicshop.services.PictureService;

@RestController
@RequestMapping("/api/v1/pictures")
public class PictureRestController {

    @Autowired
    private PictureService pictureService;

    @DeleteMapping
    public void deletePictures(@RequestBody List<Long> pictureIds){
        pictureService.deletePicturesByIds(pictureIds);
    }

}
