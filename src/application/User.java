package application;

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

    // Constructor to initialize a new User object
    public User(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.name = "";
        this.email = "";
    }
    
    // Constructor with all fields
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
    // This one exists for debugging questions. DO NOT USE.
    public void setUsername(String userName) { this.userName = userName; } 

    // Getters
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
