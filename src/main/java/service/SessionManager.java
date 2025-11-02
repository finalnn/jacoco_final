package service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private final Map<String, String> sessions = new HashMap<>();

    public String createSession(String username) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, username);
        return token;
    }

    public void invalidate(String token) {
        sessions.remove(token);
    }

    public boolean isValid(String token) {
        return sessions.containsKey(token);
    }

    public String getUsername(String token) {
        return sessions.get(token);
    }
}
