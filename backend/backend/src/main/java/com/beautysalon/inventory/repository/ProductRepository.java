package com.beautysalon.inventory.repository;

import com.beautysalon.inventory.entity.CosmeticProduct;
import com.beautysalon.inventory.entity.EquipmentProduct;
import com.beautysalon.inventory.entity.Product;
import com.beautysalon.util.AbstractFileRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * OOP Concepts Applied:
 * - Abstraction: Provides an interface or abstract mechanism for data access without exposing persistence details.
 * - Inheritance: Inherits common CRUD operations from abstract repository classes.
 * - Encapsulation: Hides file/database reading and writing mechanics from the rest of the application.
 */
public class ProductRepository extends AbstractFileRepository<Product, String> {

    @Override
    protected String getFilePath() {
        return "inventory.txt";
    }

    @Override
    protected String serialize(Product product) {
        String type = "COSMETIC";
        String extra1 = "";
        String extra2 = "";

        if (product instanceof CosmeticProduct cosmetic) {
            type = "COSMETIC";
            extra1 = cosmetic.getExpiryDate() != null ? cosmetic.getExpiryDate() : "";
            extra2 = String.valueOf(cosmetic.isOrganic());
        } else if (product instanceof EquipmentProduct equip) {
            type = "EQUIPMENT";
            extra1 = String.valueOf(equip.getWarrantyMonths());
            extra2 = equip.getLastServicedDate() != null ? equip.getLastServicedDate() : "";
        }

        return String.join("|",
                product.getId(),
                product.getName(),
                product.getCategory(),
                String.valueOf(product.getQuantity()),
                String.valueOf(product.getPrice()),
                product.getSupplier(),
                type,
                extra1,
                extra2,
                product.getImageUrl() != null ? product.getImageUrl() : ""
        );
    }

    @Override
    protected Product deserialize(String record) {
        String[] parts = record.split("\\|");
        String id = parts[0];
        String name = parts[1];
        String category = parts[2];
        int qty = Integer.parseInt(parts[3]);
        double price = Double.parseDouble(parts[4]);
        String supplier = parts[5];
        String type = parts[6];
        String extra1 = parts[7];
        String extra2 = parts[8];
        String imageUrl = parts.length > 9 ? parts[9] : "";

        if ("EQUIPMENT".equalsIgnoreCase(type)) {
            int warranty = Integer.parseInt(extra1);
            return new EquipmentProduct(id, name, category, qty, price, supplier, imageUrl, warranty, extra2);
        } else {
            boolean organic = Boolean.parseBoolean(extra2);
            return new CosmeticProduct(id, name, category, qty, price, supplier, imageUrl, extra1, organic);
        }
    }

    @Override
    protected String getIdPrefix(String id) {
        return id + "|";
    }

    @Override
    protected String getEntityIdPrefix(Product product) {
        return product.getId() + "|";
    }
}
