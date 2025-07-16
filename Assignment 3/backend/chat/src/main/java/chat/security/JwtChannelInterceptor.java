package chat.security;

import io.jsonwebtoken.Claims;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public JwtChannelInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        Map<String, Object> sessionHeaders =
                (Map<String, Object>) message.getHeaders().get("simpSessionAttributes");
        if (sessionHeaders == null || !sessionHeaders.containsKey("token")) {
            System.out.println("WebSocket token missing!");
            return message;
        }

        String token = (String) sessionHeaders.get("token");
        Claims claims = jwtUtil.extractClaims(token);

        if (claims == null) {
            System.out.println("Invalid WebSocket token!");
            return message;
        }

        String email = claims.get("email").toString();
        String role = "ROLE_" + claims.get("role").toString();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        token,
                        Collections.singletonList(new SimpleGrantedAuthority(role))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("WebSocket SecurityContext set for user: " + email);

        SecurityContext context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        sessionHeaders.put("SECURITY_CONTEXT", context);

        sessionHeaders.put("AUTH_PRINCIPAL", authentication);

        return message;
    }
}
