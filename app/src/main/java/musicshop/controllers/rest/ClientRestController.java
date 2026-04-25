package musicshop.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import musicshop.dto.request.ClientUpdateDto;
import musicshop.entities.Client;
import musicshop.services.ClientService;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientRestController {

    @Autowired
    private ClientService clientService;

    @PutMapping()
    public void updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ClientUpdateDto clientUpdate){
        Client client = (Client) userDetails;
        try{
            clientService.updateClient(client, clientUpdate);
        }catch (IllegalArgumentException exception){
            throw new ResponseStatusException(HttpStatusCode.valueOf(422));
        }
    }

}
