package com.gabriel.desafio_anota_ai.services;

import com.gabriel.desafio_anota_ai.domain.product.Product;
import com.gabriel.desafio_anota_ai.domain.product.ProductDTO;
import com.gabriel.desafio_anota_ai.domain.product.exceptions.ProductNotFoundException;
import com.gabriel.desafio_anota_ai.repositories.ProductRepository;
import com.gabriel.desafio_anota_ai.services.aws.AwsSnsService;
import com.gabriel.desafio_anota_ai.services.aws.MessageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final AwsSnsService awsSnsService;

    public ProductService(CategoryService categoryService, ProductRepository productRepository, AwsSnsService awsSnsService) {
        this.categoryService = categoryService;
        this.productRepository = productRepository;
        this.awsSnsService = awsSnsService;
    }

    public Product insertProduct(ProductDTO productData) {
        this.categoryService.getCategoryById(productData.categoryId());
        Product newProduct = new Product(productData);
        this.productRepository.save(newProduct);
        this.awsSnsService.publish(new MessageDTO(newProduct.toString()));
        return newProduct;
    }

    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    public Product updateProduct(String id, ProductDTO productData) {
        Product product = this.productRepository.findById(id).orElseThrow(ProductNotFoundException::new);

        if (productData.categoryId() != null && !productData.categoryId().isBlank()) {
            this.categoryService.getCategoryById(productData.categoryId());
            product.setCategory(productData.categoryId());
        }
        if (productData.title() != null && !productData.title().isEmpty()) {
            product.setTitle(productData.title());
        }
        if (productData.description() != null && !productData.description().isEmpty()) {
            product.setDescription(productData.description());
        }
        if (productData.ownerId() != null && !productData.ownerId().isEmpty()) {
            product.setOwnerId(productData.ownerId());
        }
        if (productData.price() != null) {
            product.setPrice(productData.price());
        }

        this.productRepository.save(product);
        this.awsSnsService.publish(new MessageDTO(product.toString()));

        return product;
    }

    public void deleteProduct(String id) {
        Product product = this.productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        this.productRepository.delete(product);
        this.awsSnsService.publish(new MessageDTO(product.deleteToString()));
    }
}
