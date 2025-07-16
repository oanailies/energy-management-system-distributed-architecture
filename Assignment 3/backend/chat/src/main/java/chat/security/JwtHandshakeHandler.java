package chat.security;

import io.jsonwebtoken.Claims;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@Component
public class JwtHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtUtil jwtUtil;

    public JwtHandshakeHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String token = extractTokenFromQuery(request);
        if (token == null) {
            System.out.println(" Missing WebSocket token");
            return null;
        }

        try {
            Claims claims = jwtUtil.extractClaims(token);
            if (claims == null) {
                System.out.println("Invalid token in WebSocket handshake");
                return null;
            }

            String userId = claims.get("id").toString();
            String email = claims.get("email").toString();
            String role = "ROLE_" + claims.get("role").toString();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            token,
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                    );


            SecurityContextHolder.getContext().setAuthentication(authentication);


            attributes.put("token", token);
            attributes.put("AUTH_PRINCIPAL", authentication);

            System.out.println("WebSocket authentication successful for user: " + email + " (ID: " + userId + ")");
            return () -> email;

        } catch (Exception e) {
            System.out.println("Error processing WebSocket token: " + e.getMessage());
            return null;
        }
    }

    private String extractTokenFromQuery(ServerHttpRequest request) {
        String uri = request.getURI().toString();
        System.out.println("WebSocket URI: " + uri);

        if (uri.contains("token=")) {
            String token = uri.split("token=")[1].split("&")[0];
            System.out.println("Extracted Token: " + token);
            return token;
        }
        return null;
    }
}
