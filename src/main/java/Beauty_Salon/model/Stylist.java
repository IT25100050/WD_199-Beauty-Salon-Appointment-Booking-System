package Beauty_Salon.model;



public class Stylist {

    private Long id;
    private String name;
    private String specialty;
    private String email;
    private String phone;
    private String status; // available, busy, off
    private double rating;
    private int experienceYears;

    // Constructor
    public Stylist() {}

    public Stylist(Long id, String name, String specialty,
                   String email, String phone,
                   String status, double rating, int experienceYears) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.rating = rating;
        this.experienceYears = experienceYears;
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
}
