package model;

import org.mindrot.jbcrypt.BCrypt;

public class Admin {

    private final String username;
    private final String passwordHash;

    public Admin(String username, String rawPassword) {
        this.username = username;
        this.passwordHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String inputPassword) {
        return BCrypt.checkpw(inputPassword, this.passwordHash);
    }
}
