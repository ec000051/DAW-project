package com.example.dawproject.beans;

import com.example.dawproject.dao.ProductDAO;
import com.example.dawproject.dao.UserDAO;
import com.example.dawproject.model.Product;
import com.example.dawproject.model.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class SellerProfileBean {

    @Inject
    private UserDAO userDAO;

    @Inject
    private ProductDAO productDAO;

    private User seller;
    private List<Product> sellerProducts = new ArrayList<>();

    public void loadSeller() {
        String idParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("id");

        if (idParam != null) {
            seller = userDAO.findById(Long.parseLong(idParam));

            if (seller != null) {
                sellerProducts = productDAO.findByOwner(seller);
            }
        }
    }

    public User getSeller() {
        return seller;
    }

    public List<Product> getSellerProducts() {
        return sellerProducts;
    }
}