package musicshop.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.dto.ClientForAdminDto;
import musicshop.dto.ClientPreviewDto;
import musicshop.dto.request.ClientDto;
import musicshop.entities.Client;
import musicshop.entities.Order;
import musicshop.mappers.ClientMapper;
import musicshop.repositories.ClientRepository;
import musicshop.repositories.OrderRepository;

@Service
public class ClientService {

    private final Integer CLIENTS_PAGE_SIZE_FOR_ADMIN = 5;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Transactional
    public void updateClient(Client client,ClientDto clientUpdate){
        if(clientUpdate.getPassword() != null && clientUpdate.getConfirmPassword() != null && 
            !clientUpdate.getPassword().isEmpty() && !clientUpdate.getConfirmPassword().isEmpty()){
            if(clientUpdate.getPassword().equals(clientUpdate.getConfirmPassword())){
                client.setFullName(clientUpdate.getFullName());
                client.setPhone(clientUpdate.getPhone());
                client.setPassword(passwordEncoder.encode(clientUpdate.getPassword()));
                clientRepository.save(client);
            }else{
                throw new IllegalArgumentException();
            }
        }else{
            client.setFullName(clientUpdate.getFullName());
            client.setPhone(clientUpdate.getPhone());
            clientRepository.save(client);
        }
    }

    @Transactional
    public void createUser(String fullName, String phone, String password, String confirmPassword){
        if(!(password != null && confirmPassword != null && !password.isEmpty() && !confirmPassword.isEmpty())){
            throw new RuntimeException();
        }
        if(!password.equals(confirmPassword)){
            throw new RuntimeException();
        }
        Client client = new Client(null, fullName, phone, passwordEncoder.encode(password), null);
        clientRepository.save(client);
    }

    public Page<ClientPreviewDto> getClientsForAdmin(String search, Integer page){
        Pageable pageable = PageRequest.of(page == null ? 0 : page < 0 ? 0 : page , CLIENTS_PAGE_SIZE_FOR_ADMIN).withSort(Sort.by("id"));
        search = search != null ? search : "";
        return clientRepository.findClientPreviewDtoByOrderStatusNotIn(List.of(Order.Status.NULL), search, pageable);
    }

    @Transactional
    public ClientForAdminDto getClientsForAdmin(Long clientId){
        Client client = clientRepository.findWithOrdersWithSellerByIdAndOrdersStatusNotIn(clientId, List.of(Order.Status.NULL));
        orderRepository.findWithProductOrdersWithProductByStatusNotInAndClientId(List.of(Order.Status.NULL), clientId);
        return clientMapper.mapToClientForAdminDto(client);
    }

}
