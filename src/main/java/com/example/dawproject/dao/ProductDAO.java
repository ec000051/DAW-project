package com.example.dawproject.dao;

import com.example.dawproject.model.Product;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProductDAO implements Serializable {

    private final List<Product> products = new ArrayList<>();
    private Long nextId = 1L;

    public ProductDAO() {
        products.add(new Product(nextId++, "Laptop", 500.0, "Portable computer"));
        products.add(new Product(nextId++, "Chair", 30.0, "Wooden chair"));
    }

    public List<Product> findAll() {
        return products;
    }

    public void create(Product product) {
        product.setId(nextId++);
        products.add(product);
    }

    public Product findById(Long id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    public void update(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(updatedProduct.getId())) {
                products.set(i, updatedProduct);
                return;
            }
        }
    }

    public void delete(Long id) {
        products.removeIf(product -> product.getId().equals(id));
    }
}