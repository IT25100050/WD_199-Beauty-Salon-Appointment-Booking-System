package Beauty_Salon.controller;

import Beauty_Salon.model.Stylist;
import Beauty_Salon.service.StylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stylists")
@CrossOrigin(origins = "*")
public class StylistController {

    @Autowired
    private StylistService stylistService;

    // GET all stylists
    @GetMapping
    public List<Stylist> getAllStylists() {
        return stylistService.getAllStylists();
    }

    // GET stylist by ID
    @GetMapping("/{id}")
    public ResponseEntity<Stylist> getStylistById(@PathVariable Long id) {
        return stylistService.getStylistById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST add new stylist
    @PostMapping
    public Stylist addStylist(@RequestBody Stylist stylist) {
        return stylistService.addStylist(stylist);
    }

    // PUT update stylist
    @PutMapping("/{id}")
    public ResponseEntity<Stylist> updateStylist(
            @PathVariable Long id,
            @RequestBody Stylist stylist) {
        return stylistService.updateStylist(id, stylist)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE stylist
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStylist(@PathVariable Long id) {
        if (stylistService.deleteStylist(id)) {
            return ResponseEntity.ok("Stylist deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    // GET by status
    @GetMapping("/status/{status}")
    public List<Stylist> getByStatus(@PathVariable String status) {
        return stylistService.getStylistsByStatus(status);
    }
}
