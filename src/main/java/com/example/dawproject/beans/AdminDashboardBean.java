package com.example.dawproject.beans;

import com.example.dawproject.dao.OrderDAO;
import com.example.dawproject.dao.ProductDAO;
import com.example.dawproject.dao.UserDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class AdminDashboardBean {

    @Inject
    private UserDAO userDAO;

    @Inject
    private ProductDAO productDAO;

    @Inject
    private OrderDAO orderDAO;

    public Long getTotalUsers() {
        return userDAO.countUsers();
    }

    public Long getTotalProducts() {
        return productDAO.countProducts();
    }

    public Long getTotalOrders() {
        return orderDAO.countOrders();
    }

    public Long getAvailableProducts() {
        return productDAO.countAvailableProducts();
    }

    public Long getReservedProducts() {
        return productDAO.countReservedProducts();
    }

    public Long getSoldProducts() {
        return productDAO.countSoldProducts();
    }

    public Long getPendingOrders() {
        return orderDAO.countPendingOrders();
    }

    public Long getApprovedOrders() {
        return orderDAO.countApprovedOrders();
    }

    public Long getRejectedOrders() {
        return orderDAO.countRejectedOrders();
    }

    public Long getElectronicsProducts() {
        return productDAO.countProductsByCategory("Electronics");
    }

    public Long getFurnitureProducts() {
        return productDAO.countProductsByCategory("Furniture");
    }

    public Long getBooksProducts() {
        return productDAO.countProductsByCategory("Books");
    }

    public Long getAccessoriesProducts() {
        return productDAO.countProductsByCategory("Accessories");
    }

    public Long getOtherProducts() {
        return productDAO.countProductsByCategory("Other");
    }

    public double getElectronicsPercentage() {
        Long total = getTotalProducts();
        if (total == null || total == 0) return 0;

        return getElectronicsProducts() * 100.0 / total;
    }

    public double getFurnitureEndPercentage() {
        Long total = getTotalProducts();
        if (total == null || total == 0) return 0;

        return getElectronicsPercentage()
                + getFurnitureProducts() * 100.0 / total;
    }

    public double getBooksEndPercentage() {
        Long total = getTotalProducts();
        if (total == null || total == 0) return 0;

        return getFurnitureEndPercentage()
                + getBooksProducts() * 100.0 / total;
    }

    public double getAccessoriesEndPercentage() {
        Long total = getTotalProducts();
        if (total == null || total == 0) return 0;

        return getBooksEndPercentage()
                + getAccessoriesProducts() * 100.0 / total;
    }
}