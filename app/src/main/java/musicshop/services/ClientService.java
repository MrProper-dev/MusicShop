package musicshop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import musicshop.dto.request.ClientDto;
import musicshop.entities.Client;
import musicshop.repositories.ClientRepository;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

}
