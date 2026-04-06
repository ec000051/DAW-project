package com.example.dawproject.beans;

import com.example.dawproject.dao.ProductDAO;
import com.example.dawproject.model.Product;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class ProductBean implements Serializable {

    private Product product = new Product();

    @Inject
    private ProductDAO productDAO;

    public List<Product> getProducts() {
        return productDAO.findAll();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String prepareCreate() {
        product = new Product();
        return "/products/create.xhtml?faces-redirect=true";
    }

    public String create() {
        productDAO.create(product);
        product = new Product();
        return "/products/list.xhtml?faces-redirect=true";
    }

    public String prepareEdit(Long id) {
        Product existing = productDAO.findById(id);
        if (existing != null) {
            product = new Product(
                    existing.getId(),
                    existing.getName(),
                    existing.getPrice(),
                    existing.getDescription()
            );
        }
        return "/products/edit.xhtml?faces-redirect=true";
    }

    public String update() {
        productDAO.update(product);
        return "/products/list.xhtml?faces-redirect=true";
    }

    public String delete(Long id) {
        productDAO.delete(id);
        return "/products/list.xhtml?faces-redirect=true";
    }
}