package chat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import chat.entities.User;
import chat.entities.UserRole;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    public boolean validateUser(Long userId) {
        String url = "http://users-service:8080/api/users/" + userId;

        try {
            ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException e) {
            return false;
        }
    }

    public User getUserDetails(Long userId) {
        String url = "http://users-service:8080/api/users/" + userId;

        String token = null;

        try {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
            }
        } catch (Exception e) {
            System.out.println(" Could not retrieve token from SecurityContext.");
        }

        if (token == null) {
            System.out.println(" Token is missing in SecurityContext, trying alternative...");
            return null;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (HttpClientErrorException e) {
            System.out.println(" UserServiceClient: User not found for ID " + userId);
            return null;
        }
        return null;
    }




    public boolean isUserAdmin(Long userId) {
        User user = getUserDetails(userId);
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public List<User> getAllUsersWithRoleUser() {
        String url = "http://users-service:8080/api/users/";

        ResponseEntity<List<User>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<User>>() {});

        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : new ArrayList<>();
    }

}
