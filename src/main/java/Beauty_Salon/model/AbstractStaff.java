package Beauty_Salon.model;

public abstract class AbstractStaff {

    // Abstract methods
    // must be implemented by subclasses
    public abstract String getRole();

    public abstract String getDescription();

    public abstract double calculateBonus();

    // Concrete method
    // shared by all staff types
    public String getWelcomeMessage() {
        return "Welcome to Velour Salon, "
                + getRole() + "!";
    }

    // Information hiding example
    public String getMaskedEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "****";
        }
        String[] parts = email.split("@");
        return "****@" + parts[1];
    }
}