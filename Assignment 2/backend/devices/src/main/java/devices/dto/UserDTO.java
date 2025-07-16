package devices.dto;


import lombok.Data;
import devices.entities.UserRole;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private UserRole role;
}
