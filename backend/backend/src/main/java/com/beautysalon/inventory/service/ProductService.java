package com.beautysalon.inventory.service;

import com.beautysalon.inventory.dto.ProductDto;
import com.beautysalon.inventory.entity.CosmeticProduct;
import com.beautysalon.inventory.entity.EquipmentProduct;
import com.beautysalon.inventory.entity.Product;
import com.beautysalon.inventory.exception.LowStockException;
import com.beautysalon.inventory.exception.ProductNotFoundException;
import com.beautysalon.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        return convertToDto(product);
    }

    public ProductDto addProduct(ProductDto dto) {
        if (dto.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
        if (dto.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        String id = UUID.randomUUID().toString();
        Product product;

        if ("EQUIPMENT".equalsIgnoreCase(dto.getType())) {
            product = new EquipmentProduct(id, dto.getName(), dto.getCategory(), dto.getQuantity(),
                    dto.getPrice(), dto.getSupplier(), dto.getImageUrl(), dto.getWarrantyMonths(), dto.getLastServicedDate());
        } else {
            product = new CosmeticProduct(id, dto.getName(), dto.getCategory(), dto.getQuantity(),
                    dto.getPrice(), dto.getSupplier(), dto.getImageUrl(), dto.getExpiryDate(), dto.isOrganic());
        }

        productRepository.save(product);
        return convertToDto(product);
    }

    public ProductDto updateStock(String id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        product.setQuantity(quantity);
        productRepository.update(product);
        return convertToDto(product);
    }

    public ProductDto updateProduct(String id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        if (dto.getQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        if (dto.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setQuantity(dto.getQuantity());
        product.setPrice(dto.getPrice());
        product.setSupplier(dto.getSupplier());
        if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            product.setImageUrl(dto.getImageUrl());
        }

        if (product instanceof CosmeticProduct cosmetic) {
            cosmetic.setExpiryDate(dto.getExpiryDate());
            cosmetic.setOrganic(dto.isOrganic());
        } else if (product instanceof EquipmentProduct equip) {
            equip.setWarrantyMonths(dto.getWarrantyMonths());
            equip.setLastServicedDate(dto.getLastServicedDate());
        }

        productRepository.update(product);
        return convertToDto(product);
    }

    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        productRepository.deleteById(id);
    }

    public List<ProductDto> getLowStockAlerts() {
        List<Product> lowStock = productRepository.findAll().stream()
                .filter(p -> p.getQuantity() < 5)
                .toList();

        if (lowStock.isEmpty()) {
            throw new LowStockException("All inventory items have sufficient stock levels");
        }

        return lowStock.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ProductDto> searchProducts(String query) {
        return productRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()) ||
                             p.getCategory().toLowerCase().contains(query.toLowerCase()) ||
                             p.getSupplier().toLowerCase().contains(query.toLowerCase()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductDto convertToDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setCategory(p.getCategory());
        dto.setQuantity(p.getQuantity());
        dto.setPrice(p.getPrice());
        dto.setSupplier(p.getSupplier());
        dto.setImageUrl(p.getImageUrl());

        if (p instanceof CosmeticProduct cosmetic) {
            dto.setType("COSMETIC");
            dto.setExpiryDate(cosmetic.getExpiryDate());
            dto.setOrganic(cosmetic.isOrganic());
        } else if (p instanceof EquipmentProduct equip) {
            dto.setType("EQUIPMENT");
            dto.setWarrantyMonths(equip.getWarrantyMonths());
            dto.setLastServicedDate(equip.getLastServicedDate());
        }
        return dto;
    }
}
