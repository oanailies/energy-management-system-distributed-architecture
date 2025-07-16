package users.dto;


import lombok.Data;
import users.entities.UserRole;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private UserRole role;
}
