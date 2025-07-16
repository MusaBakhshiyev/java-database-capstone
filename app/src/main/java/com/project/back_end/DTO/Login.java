package com.project.back_end.DTO;

/**
 * DTO class used to capture login credentials from the client.
 * It is not persisted in the database and is typically used with @RequestBody.
 */
public class Login {

    private String identifier; // Can be email (for Doctor/Patient) or username (for Admin)
    private String password;

    // Default constructor
    public Login() {}

    // Getters and Setters
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
