package com.project.beautysalon.Controller;

import com.project.beautysalon.model.Stylist;
import com.project.beautysalon.repository.StylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stylists")
@CrossOrigin(origins = "*")
public class StylistController {

    @Autowired
    private StylistRepository stylistRepository;

    // GET all stylists
    @GetMapping
    public ResponseEntity<List<Stylist>> getAllStylists() {
        return ResponseEntity.ok(stylistRepository.findAll());
    }


    @PostMapping
    public ResponseEntity<?> createStylist(@RequestBody Map<String, Object> body) {
        try {
            Stylist stylist = new Stylist();

            stylist.setName((String) body.get("name"));
            stylist.setSpecialization((String) body.get("specialization"));
            stylist.setPhone((String) body.get("phone"));
            stylist.setEmail((String) body.get("email"));
            stylist.setStatus(body.get("status") != null ? (String) body.get("status") : "AVAILABLE");

            Stylist saved = stylistRepository.save(stylist);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
