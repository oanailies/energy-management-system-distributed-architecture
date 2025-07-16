package chat.services;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserStatusService {
    private final ConcurrentHashMap<Long, String> userStatus = new ConcurrentHashMap<>();

    public void updateUserStatus(Long userId, String page) {
        if (userId == null || page == null) {
            System.out.println("ERROR: userId sau pagina este null!");
            return;
        }
        userStatus.put(userId, page);
        System.out.println("User " + userId + " este acum activ pe pagina: " + page);
    }

    public boolean isUserOnPage(Long userId, String page) {
        if (userId == null || page == null) {
            System.out.println("ERROR: userId sau pagina este null în verificare!");
            return false;
        }
        boolean isActive = page.equals(userStatus.getOrDefault(userId, ""));
        System.out.println("Verificare activitate: User " + userId + " pe pagina " + page + " → " + (isActive ? "ACTIV" : "INACTIV"));
        return isActive;
    }
}
