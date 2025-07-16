package users.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import users.entities.User;
import users.entities.UserRole;
import users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import users.security.JwtUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User newUser = userRepository.save(user);

        Map<String, Object> claims = Map.of(
                "id", newUser.getId(),
                "email", newUser.getEmail(),
                "name", newUser.getName(),
                "role", newUser.getRole().toString()
        );

        String token = jwtUtil.generateToken(claims);

        return Map.of("user", newUser, "token", token);
    }


    public Map<String, Object> register(User user) {
        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User newUser = userRepository.save(user);

        Map<String, Object> claims = Map.of(
                "id", newUser.getId(),
                "email", newUser.getEmail(),
                "name", newUser.getName(),
                "role", newUser.getRole().toString()
        );

        String token = jwtUtil.generateToken(claims);

        return Map.of("user", newUser, "token", token);
    }


    public List<User> getAllUsersWithRoleUser() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.USER)
                .collect(Collectors.toList());
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        if (user != null) {
            user.setEmail(userDetails.getEmail());
            user.setName(userDetails.getName());
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(userDetails.getPassword());
            }

            user.setRole(UserRole.USER);
            return userRepository.save(user);
        }
        return null;
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null) {

            if (!user.getPassword().startsWith("$2a$")) {

                if (user.getPassword().equals(password)) {
                    user.setPassword(passwordEncoder.encode(password));
                    userRepository.save(user);
                } else {
                    return null;
                }
            } else if (!passwordEncoder.matches(password, user.getPassword())) {
                return null;
            }


            Map<String, Object> claims = Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole().toString()
            );

            String token = jwtUtil.generateToken(claims);
            return Map.of("user", user, "token", token);
        }

        return null;
    }



    public User getFirstAdmin() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .findFirst()
                .orElse(null);
    }


}
