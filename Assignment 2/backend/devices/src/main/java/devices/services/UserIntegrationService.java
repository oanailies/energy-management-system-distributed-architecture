package devices.services;

import devices.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserIntegrationService {

    private static final String USER_SERVICE_BASE_URL = "http://users-service:8080/api/users";

    @Autowired
    private RestTemplate restTemplate;

    public UserDTO getUserById(Long userId) {
        String url = USER_SERVICE_BASE_URL + "/" + userId;
        return restTemplate.getForObject(url, UserDTO.class);
    }
}
