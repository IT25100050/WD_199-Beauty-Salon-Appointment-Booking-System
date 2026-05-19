package com.beautysalon.servicepackage.repository;

import com.beautysalon.servicepackage.entity.Service;
import com.beautysalon.servicepackage.entity.RegularService;
import com.beautysalon.servicepackage.entity.PremiumService;
import com.beautysalon.util.AbstractFileRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * OOP Concepts Applied:
 * - Abstraction: Provides an interface or abstract mechanism for data access without exposing persistence details.
 * - Inheritance: Inherits common CRUD operations from abstract repository classes.
 * - Encapsulation: Hides file/database reading and writing mechanics from the rest of the application.
 */
public class ServiceRepository extends AbstractFileRepository<Service, String> {

    @Override
    protected String getFilePath() {
        return "services.txt";
    }

    @Override
    protected String serialize(Service service) {
        String type = "REGULAR";
        String extra1 = "";
        String extra2 = "0.0";

        if (service instanceof PremiumService premium) {
            type = "PREMIUM";
            extra1 = premium.getPremiumAmenities() != null ? premium.getPremiumAmenities() : "";
            extra2 = String.valueOf(premium.getLuxurySurcharge());
        } else if (service instanceof RegularService regular) {
            type = "REGULAR";
            extra1 = regular.getStandardProductsUsed() != null ? regular.getStandardProductsUsed() : "";
        }

        return String.join("|",
                service.getId(),
                service.getName(),
                service.getCategory(),
                String.valueOf(service.getPrice()),
                String.valueOf(service.getDurationMinutes()),
                String.valueOf(service.isAvailable()),
                type,
                extra1,
                extra2
        );
    }

    @Override
    protected Service deserialize(String record) {
        String[] parts = record.split("\\|");
        String id = parts[0];
        String name = parts[1];
        String category = parts[2];
        double price = Double.parseDouble(parts[3]);
        int duration = Integer.parseInt(parts[4]);
        boolean available = Boolean.parseBoolean(parts[5]);
        String type = parts[6];

        if ("PREMIUM".equalsIgnoreCase(type)) {
            String amenities = parts[7];
            double surcharge = Double.parseDouble(parts[8]);
            return new PremiumService(id, name, category, price, duration, available, amenities, surcharge);
        } else {
            String products = parts[7];
            return new RegularService(id, name, category, price, duration, available, products);
        }
    }

    @Override
    protected String getIdPrefix(String id) {
        return id + "|";
    }

    @Override
    protected String getEntityIdPrefix(Service service) {
        return service.getId() + "|";
    }
}
