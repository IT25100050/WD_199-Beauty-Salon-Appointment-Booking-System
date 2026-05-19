package com.beautysalon.inventory.controller;

import com.beautysalon.inventory.dto.ProductDto;
import com.beautysalon.inventory.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
/**
 * OOP Concepts Applied:
 * - Encapsulation: Exposes REST endpoints while hiding internal business logic.
 * - Abstraction: Interacts with Service interfaces/classes without knowing their implementation details.
 * - Single Responsibility Principle (SOLID): Responsible solely for handling HTTP requests and responses.
 */
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.addProduct(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String id, @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDto> updateStock(@PathVariable String id, @RequestParam int quantity) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStockAlerts() {
        return ResponseEntity.ok(productService.getLowStockAlerts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }
}
