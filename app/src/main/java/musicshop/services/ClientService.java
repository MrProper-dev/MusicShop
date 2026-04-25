package musicshop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.dto.request.ClientUpdateDto;
import musicshop.entities.Client;
import musicshop.repositories.ClientRepository;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void updateClient(Client client,ClientUpdateDto clientUpdate){
        if(clientUpdate.getNewPassword() != null && clientUpdate.getConfirmPassword() != null && 
            !clientUpdate.getNewPassword().isEmpty() && !clientUpdate.getConfirmPassword().isEmpty()){
            if(clientUpdate.getNewPassword().equals(clientUpdate.getConfirmPassword())){
                client.setFullName(clientUpdate.getFullName());
                client.setPhone(clientUpdate.getPhone());
                client.setPassword(passwordEncoder.encode(clientUpdate.getNewPassword()));
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

}
