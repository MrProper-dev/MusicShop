package musicshop.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import musicshop.dto.request.ClientDto;
import musicshop.entities.Client;
import musicshop.services.ClientService;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientRestController {

    @Autowired
    private ClientService clientService;

    @PutMapping()
    public void updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ClientDto clientUpdate){
        Client client = (Client) userDetails;
        try{
            clientService.updateClient(client, clientUpdate);
        }catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatusCode.valueOf(422));
        }catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
    }

    @PostMapping("/signup")
    public void signUp(@RequestBody ClientDto clientDto){
        try{
            clientService.createUser(clientDto.getFullName(), clientDto.getPhone(), clientDto.getPassword(), clientDto.getConfirmPassword());
        }catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatusCode.valueOf(422));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
    }

}
