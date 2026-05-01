package musicshop.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.dto.request.SellerDto;
import musicshop.entities.Seller;
import musicshop.repositories.SellerRepository;

@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Seller> findAllSellers(){
        return sellerRepository.findAll(Sort.by("id"));
    }

    @Transactional
    public void deleteSellerById(Long sellerId){
        Seller seller = new Seller();
        seller.setId(sellerId);
        sellerRepository.delete(seller);
    }

    public void updateSeller(Long sellerId, SellerDto sellerDto){
        if(sellerRepository.existsByLogin(sellerDto.getLogin())){
            throw new DataIntegrityViolationException(null);
        }
        Seller seller = sellerRepository.findById(sellerId).orElseThrow(() -> new RuntimeException());
        seller.setFullName(sellerDto.getFullName());
        seller.setLogin(sellerDto.getLogin());
        seller.setEmail(sellerDto.getEmail());
        seller.setPhone(sellerDto.getPhone());
        if(sellerDto.getPassword() != null && !sellerDto.getPassword().isEmpty()){
            seller.setPassword(passwordEncoder.encode(sellerDto.getPassword()));
        }
        sellerRepository.save(seller);
    }

    public Long createSeller(SellerDto sellerDto){
        if(sellerRepository.existsByLogin(sellerDto.getLogin())){
            throw new DataIntegrityViolationException(null);
        }
        Seller seller = new Seller();
        seller.setFullName(sellerDto.getFullName());
        seller.setLogin(sellerDto.getLogin());
        seller.setEmail(sellerDto.getEmail());
        seller.setPhone(sellerDto.getPhone());
        seller.setPassword(passwordEncoder.encode(sellerDto.getPassword()));
        sellerRepository.save(seller);
        return seller.getId();
    }
    
}
