package com.project.beautysalon.service;

import com.project.beautysalon.model.JuniorStylist;
import com.project.beautysalon.model.SeniorStylist;
import com.project.beautysalon.model.Stylist;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StylistService {

    private static final String FILE_PATH = "stylists.txt";
    private Long nextId = 1L;

    public StylistService() {
        // Add sample data if file is empty
        if (getAllStylists().isEmpty()) {
            addStylist(new SeniorStylist(null,
                    "Aria Fontaine", "Color Specialist",
                    "aria@salon.com", "555-0101",
                    "available", 4.9, 8));
            addStylist(new JuniorStylist(null,
                    "Marco Delacroix", "Hair Styling",
                    "marco@salon.com", "555-0102",
                    "busy", 4.2, 1));
            addStylist(new SeniorStylist(null,
                    "Priya Nair", "Nail Technician",
                    "priya@salon.com", "555-0103",
                    "available", 4.7, 5));
        } else {
            // Set nextId based on existing data
            List<Stylist> existing = getAllStylists();
            nextId = existing.stream()
                    .mapToLong(Stylist::getId)
                    .max().orElse(0L) + 1;
        }
    }

    // READ all from file
    public List<Stylist> getAllStylists() {
        List<Stylist> stylists = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) return stylists;

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    stylists.add(Stylist.fromFileString(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stylists;
    }

    // WRITE all to file
    private void saveAllToFile(List<Stylist> stylists) {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Stylist s : stylists) {
                writer.write(s.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // READ one by ID
    public Optional<Stylist> getStylistById(Long id) {
        return getAllStylists().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    // CREATE - add new stylist
    public Stylist addStylist(Stylist stylist) {
        List<Stylist> stylists = getAllStylists();
        stylist.setId(nextId++);
        stylists.add(stylist);
        saveAllToFile(stylists);
        return stylist;
    }

    // UPDATE - edit stylist
    public Optional<Stylist> updateStylist(
            Long id, Stylist updated) {
        List<Stylist> stylists = getAllStylists();
        for (int i = 0; i < stylists.size(); i++) {
            if (stylists.get(i).getId().equals(id)) {
                updated.setId(id);
                stylists.set(i, updated);
                saveAllToFile(stylists);
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    // DELETE - remove stylist
    public boolean deleteStylist(Long id) {
        List<Stylist> stylists = getAllStylists();
        boolean removed = stylists
                .removeIf(s -> s.getId().equals(id));
        if (removed) saveAllToFile(stylists);
        return removed;
    }

    // READ by status
    public List<Stylist> getStylistsByStatus(String status) {
        List<Stylist> result = new ArrayList<>();
        for (Stylist s : getAllStylists()) {
            if (s.getStatus().equalsIgnoreCase(status)) {
                result.add(s);
            }
        }
        return result;
    }

    // READ by type
    public List<Stylist> getStylistsByType(String type) {
        List<Stylist> result = new ArrayList<>();
        for (Stylist s : getAllStylists()) {
            if (s.getType().equalsIgnoreCase(type)) {
                result.add(s);
            }
        }
        return result;
    }
}
