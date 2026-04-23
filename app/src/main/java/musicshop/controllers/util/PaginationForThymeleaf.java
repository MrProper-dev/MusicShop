package musicshop.controllers.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class PaginationForThymeleaf {

    public Integer getFirstPage(Integer currentPage){
        if(currentPage<=3){
            return null;
        }else{
            return 1;
        }
    }

    public List<Integer> getPagesBefore(Integer currentPage){
        List<Integer> pages = new ArrayList<>();
        for(int i = 2; i>0; i--){
            int buff = currentPage - i;
            if(buff > 0) pages.add(buff);
        }
        return pages;
    }

    public List<Integer> getPagesAfter(Integer currentPage, Integer lastPage){
        List<Integer> pages = new ArrayList<>();
        for(int i = 1; i<=2; i++){
            int buff = currentPage + i;
            if(buff <= lastPage) pages.add(buff);
        }
        return pages;
    }

    public Integer getLastPage(Integer currentPage, Integer lastPage){
        if(lastPage - currentPage <= 2){
            return null;
        }else{
            return lastPage;
        }
    }

}
