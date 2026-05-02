package Beauty_Salon.model;

public class Stylist extends AbstractStaff {

    private Long id;
    private String name;
    private String specialty;
    private String email;
    private String phone;
    private String status;
    private double rating;
    private int experienceYears;
    private String type; // "senior" or "junior"

    public Stylist() {}

    public Stylist(Long id, String name, String specialty,
                   String email, String phone,
                   String status, double rating,
                   int experienceYears, String type) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Convert to file line
    public String toFileString() {
        return id + "," + name + "," + specialty + "," +
                email + "," + phone + "," + status + "," +
                rating + "," + experienceYears + "," + type;
    }

    // Create from file line
    public static Stylist fromFileString(String line) {
        String[] parts = line.split(",");
        return new Stylist(
                Long.parseLong(parts[0]),
                parts[1], parts[2], parts[3],
                parts[4], parts[5],
                Double.parseDouble(parts[6]),
                Integer.parseInt(parts[7]),
                parts[8]
        );
    }

    // Polymorphism
    public String getRole() {
        return "Stylist";
    }

    public String getDescription() {
        return name + " is a " + getRole() +
                " specializing in " + specialty;
    }
    @Override
    public double calculateBonus() {
        return rating * experienceYears * 10;
    }
}