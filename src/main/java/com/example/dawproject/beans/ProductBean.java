package com.example.dawproject.beans;

import com.example.dawproject.dao.ProductDAO;
import com.example.dawproject.model.Product;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class ProductBean implements Serializable {

    private Product product = new Product();

    @Inject
    private ProductDAO productDAO;

    @Inject
    private AuthBean authBean;

    private String searchKeyword;
    private String selectedCategory = "ALL";

    public List<Product> getProducts() {
        return productDAO.findByOwner(authBean.getCurrentUser());
    }

    public List<Product> getAllProducts() {
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
        product.setOwner(authBean.getCurrentUser());
        productDAO.create(product);
        product = new Product();
        return "/products/list.xhtml?faces-redirect=true";
    }

    public String prepareEdit(Long id) {
        Product existing = productDAO.findById(id);

        if (existing != null) {
            product = existing;
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

    public String adminDelete(Long id) {
        productDAO.delete(id);
        return "/admin/products.xhtml?faces-redirect=true";
    }

    public void loadProductById() {
        String idParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("id");

        if (idParam != null) {
            Long id = Long.parseLong(idParam);
            product = productDAO.findById(id);
        }
    }

    public boolean isOwnProduct() {
        return authBean.getCurrentUser() != null
                && product != null
                && product.getOwner() != null
                && product.getOwner().getId().equals(authBean.getCurrentUser().getId());
    }

    public List<Product> getFilteredProducts() {
        return productDAO.searchProducts(searchKeyword, selectedCategory);
    }

    public String search() {
        return null;
    }

    public String clearFilters() {
        searchKeyword = null;
        selectedCategory = "ALL";
        return null;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public List<Product> getLatestProducts() {
        return productDAO.findLatestAvailable(4);
    }

    public boolean isProductAvailable() {
        return product != null && "AVAILABLE".equals(product.getStatus());
    }
}