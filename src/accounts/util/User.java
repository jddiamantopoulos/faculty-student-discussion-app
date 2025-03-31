package accounts.util;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, role, name, and email.
 */
public class User {
    private String userName;
    private String password;
    private String role;
    private String name;
    private String email;

    /**
     * Constructs a new user. Inputs should be validated BEFORE use.
     * @param userName The userName to be used.
     * @param password The password to be used.
     * @param role The user's role (from invite code).
     */
    public User(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.name = "";
        this.email = "";
    }
    
    /**
     * Constructs a new user with more control over user info. Inputs should be
     * validated BEFORE use.
     * @param userName The user's provided username.
     * @param password The user's provided password.
     * @param role The user's role (from invite code).
     * @param name The user's provided name.
     * @param email The user's provided email.
     */
    public User(String userName, String password, String role, String name, String email) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.name = name;
        this.email = email;
    }
    
    // Setters
    public void setRole(String role) { this.role = role; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    /* There is no username setter since this should never be changed */

    // Getters
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
