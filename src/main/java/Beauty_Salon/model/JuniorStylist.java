package Beauty_Salon.model;

public class JuniorStylist extends Stylist {

    public JuniorStylist() {}

    public JuniorStylist(Long id, String name, String specialty,
                         String email, String phone,
                         String status, double rating,
                         int experienceYears) {
        super(id, name, specialty, email, phone,
                status, rating, experienceYears, "junior");
    }

    @Override
    public String getRole() {
        return "Junior Stylist";
    }

    @Override
    public String getDescription() {
        return getName() + " is a " + getRole() +
                " learning " + getSpecialty();
    }

    public String getTrainingStatus() {
        if (getExperienceYears() < 2) {
            return "In Training";
        }
        return "Training Complete";
    }
    @Override
    public double calculateBonus() {
        return getRating() * getExperienceYears() * 10;
    }
}