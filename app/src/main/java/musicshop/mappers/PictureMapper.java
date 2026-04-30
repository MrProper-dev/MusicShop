package musicshop.mappers;

import org.springframework.stereotype.Component;

import musicshop.dto.PictureDto;
import musicshop.entities.Picture;

@Component
public class PictureMapper {

    public PictureDto mapToPictureDto(Picture picture){
        PictureDto dto = new PictureDto();
        dto.setId(picture.getId());
        dto.setPath(picture.getPath());
        return dto;
    }

}
