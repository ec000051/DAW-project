package com.example.dawproject.beans;

import com.example.dawproject.dao.OrderDAO;
import com.example.dawproject.dao.ProductDAO;
import com.example.dawproject.dao.UserDAO;
import com.example.dawproject.model.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named
@SessionScoped
public class AuthBean implements Serializable {
    @Inject
    private ProductDAO productDAO;

    @Inject
    private OrderDAO orderDAO;

    @Inject
    private UserDAO userDAO;

    private String username;
    private String password;
    private String email;

    private User currentUser;

    public String login() {
        User user = userDAO.findByUsernameAndPassword(username, password);

        if (user != null) {
            currentUser = user;

            if (isAdmin()) {
                return "/admin/dashboard.xhtml?faces-redirect=true";
            }

            return "/index.xhtml?faces-redirect=true";
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Invalid login",
                        "Username or password is incorrect."));

        return null;
    }

    public String register() {
        if (userDAO.exists(username)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Username already exists",
                            "Please choose another username."));

            return null;
        }

        User newUser = new User(username, password, email, "USER");
        userDAO.addUser(newUser);

        currentUser = newUser;

        username = null;
        password = null;
        email = null;

        return "/index.xhtml?faces-redirect=true";
    }

    public String logout() {
        currentUser = null;
        username = null;
        password = null;
        email = null;
        return "/index.xhtml?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public String deleteMyAccount() {
        if (currentUser == null) {
            return "/auth/login.xhtml?faces-redirect=true";
        }

        orderDAO.deleteByUser(currentUser);
        productDAO.deleteByOwner(currentUser);
        userDAO.deleteUser(currentUser);

        currentUser = null;
        username = null;
        password = null;
        email = null;

        return "/index.xhtml?faces-redirect=true";
    }

    public String updateAdminAccount() {
        currentUser = userDAO.updateUser(currentUser);
        return "/admin/account.xhtml?faces-redirect=true";
    }

    public String updateProfile() {
        currentUser = userDAO.updateUser(currentUser);
        return "/profile/profile.xhtml?faces-redirect=true";
    }

    public boolean isUser() {
        return currentUser != null && "USER".equals(currentUser.getRole());
    }

    public String requireLogin() {
        if (!isLoggedIn()) {
            return "/auth/login.xhtml?faces-redirect=true";
        }

        return null;
    }

    public String requireAdmin() {
        if (!isLoggedIn()) {
            return "/auth/login.xhtml?faces-redirect=true";
        }

        if (!isAdmin()) {
            return "/index.xhtml?faces-redirect=true";
        }

        return null;
    }

    public String requireUser() {
        if (!isLoggedIn()) {
            return "/auth/login.xhtml?faces-redirect=true";
        }

        if (!isUser()) {
            return "/index.xhtml?faces-redirect=true";
        }

        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}