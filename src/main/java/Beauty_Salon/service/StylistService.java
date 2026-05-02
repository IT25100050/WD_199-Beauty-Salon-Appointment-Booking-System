package Beauty_Salon.service;


import Beauty_Salon.model.Stylist;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StylistService {

    private List<Stylist> stylists = new ArrayList<>();
    private Long nextId = 1L;

    // Constructor with sample data
    public StylistService() {
        stylists.add(new Stylist(nextId++, "Aria Fontaine",
                "Color Specialist", "aria@salon.com",
                "555-0101", "available", 4.9, 8));
        stylists.add(new Stylist(nextId++, "Marco Delacroix",
                "Master Stylist", "marco@salon.com",
                "555-0102", "busy", 4.8, 12));
        stylists.add(new Stylist(nextId++, "Priya Nair",
                "Nail Technician", "priya@salon.com",
                "555-0103", "available", 4.7, 5));
    }

    // Get all stylists
    public List<Stylist> getAllStylists() {
        return stylists;
    }

    // Get by ID
    public Optional<Stylist> getStylistById(Long id) {
        return stylists.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    // Add new stylist
    public Stylist addStylist(Stylist stylist) {
        stylist.setId(nextId++);
        stylists.add(stylist);
        return stylist;
    }

    // Update stylist
    public Optional<Stylist> updateStylist(Long id, Stylist updated) {
        for (int i = 0; i < stylists.size(); i++) {
            if (stylists.get(i).getId().equals(id)) {
                updated.setId(id);
                stylists.set(i, updated);
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    // Delete stylist
    public boolean deleteStylist(Long id) {
        return stylists.removeIf(s -> s.getId().equals(id));
    }

    // Get by status
    public List<Stylist> getStylistsByStatus(String status) {
        List<Stylist> result = new ArrayList<>();
        for (Stylist s : stylists) {
            if (s.getStatus().equalsIgnoreCase(status)) {
                result.add(s);
            }
        }
        return result;
    }
}