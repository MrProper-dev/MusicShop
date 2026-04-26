package musicshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private String fullName;
    private String phone;
    private String password;
    private String confirmPassword;
}
