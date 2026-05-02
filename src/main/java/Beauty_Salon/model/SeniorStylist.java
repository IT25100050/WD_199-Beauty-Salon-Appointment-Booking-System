package Beauty_Salon.model;

public class SeniorStylist extends Stylist {

    public SeniorStylist() {}

    public SeniorStylist(Long id, String name, String specialty,
                         String email, String phone,
                         String status, double rating,
                         int experienceYears) {
        super(id, name, specialty, email, phone,
                status, rating, experienceYears, "senior");
    }

    @Override
    public String getRole() {
        return "Senior Stylist";
    }

    @Override
    public String getDescription() {
        return getName() + " is an experienced " +
                getRole() + " with " +
                getExperienceYears() +
                " years in " + getSpecialty();
    }

    public double getSeniorBonus() {
        return getRating() * 100;
    }
    @Override
    public double calculateBonus() {
        return getRating() * getExperienceYears() * 20;
    }
}
