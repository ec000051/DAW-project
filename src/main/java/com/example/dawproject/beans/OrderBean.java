package com.example.dawproject.beans;

import com.example.dawproject.dao.OrderDAO;
import com.example.dawproject.dao.ProductDAO;
import com.example.dawproject.model.Order;
import com.example.dawproject.model.Product;
import com.example.dawproject.model.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class OrderBean implements Serializable {

    @Inject
    private AuthBean authBean;

    @Inject
    private OrderDAO orderDAO;

    @Inject
    private ProductDAO productDAO;

    private Product selectedProduct;
    private String contactMessage;

    public String goToCheckout() {
        if (authBean.getCurrentUser() == null) {
            return "/auth/login.xhtml?faces-redirect=true";
        }

        String idParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("id");

        if (idParam != null) {
            selectedProduct = productDAO.findById(Long.parseLong(idParam));
        }

        return "/orders/checkout.xhtml?faces-redirect=true";
    }

    public String confirmOrder() {
        User buyer = authBean.getCurrentUser();

        if (buyer == null) {
            return "/auth/login.xhtml?faces-redirect=true";
        }

        if (selectedProduct == null) {
            return "/products/browse.xhtml?faces-redirect=true";
        }

        Order order = new Order(
                selectedProduct,
                buyer,
                selectedProduct.getOwner(),
                contactMessage,
                "PENDING"
        );

        orderDAO.create(order);

        selectedProduct.setStatus("RESERVED");
        productDAO.update(selectedProduct);

        contactMessage = null;
        selectedProduct = null;

        return "/orders/purchases.xhtml?faces-redirect=true";
    }

    public String acceptOrder(Long orderId) {
        Order order = orderDAO.findById(orderId);

        if (order != null) {
            order.setStatus("APPROVED");
            order.getProduct().setStatus("SOLD");

            orderDAO.update(order);
            productDAO.update(order.getProduct());
        }

        return "/orders/sales.xhtml?faces-redirect=true";
    }

    public String rejectOrder(Long orderId) {
        Order order = orderDAO.findById(orderId);

        if (order != null) {
            order.setStatus("REJECTED");
            order.getProduct().setStatus("AVAILABLE");

            orderDAO.update(order);
            productDAO.update(order.getProduct());
        }

        return "/orders/sales.xhtml?faces-redirect=true";
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }

    public String adminDelete(Long orderId) {
        orderDAO.delete(orderId);
        return "/admin/orders.xhtml?faces-redirect=true";
    }

    public List<Order> getMyPurchases() {
        return orderDAO.getPurchases(authBean.getCurrentUser());
    }

    public List<Order> getMySales() {
        return orderDAO.getSales(authBean.getCurrentUser());
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public String getContactMessage() {
        return contactMessage;
    }

    public void setContactMessage(String contactMessage) {
        this.contactMessage = contactMessage;
    }
}