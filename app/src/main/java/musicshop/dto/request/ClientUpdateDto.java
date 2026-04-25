package musicshop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpdateDto {
    private String fullName;
    private String phone;
    private String newPassword;
    private String confirmPassword;
}
