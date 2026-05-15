package com.example.dawproject.beans;

import com.example.dawproject.dao.ProductDAO;
import com.example.dawproject.dao.UserDAO;
import com.example.dawproject.dao.OrderDAO;
import com.example.dawproject.beans.AuthBean;

import com.example.dawproject.model.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@Named
@RequestScoped
public class AdminUsersBean {

    @Inject
    private UserDAO userDAO;

    @Inject
    private ProductDAO productDAO;

    @Inject
    private OrderDAO orderDAO;

    @Inject
    private AuthBean authBean;

    public List<User> getUsers() {
        return userDAO.findAll();
    }

    public void deleteProductsOfUser(User user) {
        productDAO.deleteByOwner(user);
    }

    public void resetMarketplaceData() {
        orderDAO.deleteAll();
        productDAO.deleteAll();
    }

    public void deleteUser(User user) {
        if (user == null) {
            return;
        }

        if (authBean.getCurrentUser() != null &&
                authBean.getCurrentUser().getId().equals(user.getId())) {
            return;
        }

        orderDAO.deleteByUser(user);
        productDAO.deleteByOwner(user);
        userDAO.deleteUser(user);
    }

    public String checkAdminAccess() {
        if (!authBean.isAdmin()) {
            return "/index.xhtml?faces-redirect=true";
        }
        return null;
    }
}