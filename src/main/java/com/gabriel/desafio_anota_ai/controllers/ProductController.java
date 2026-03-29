package com.gabriel.desafio_anota_ai.controllers;

import com.gabriel.desafio_anota_ai.domain.product.Product;
import com.gabriel.desafio_anota_ai.domain.product.ProductDTO;
import com.gabriel.desafio_anota_ai.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Product> insertProduct(@RequestBody ProductDTO productData) {
        Product newProduct = this.service.insertProduct(productData);
        return ResponseEntity.ok().body(newProduct);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = this.service.getAllProducts();
        return ResponseEntity.ok().body(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody ProductDTO productData) {
        Product updatedProduct = this.service.updateProduct(id, productData);
        return ResponseEntity.ok().body(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        this.service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
