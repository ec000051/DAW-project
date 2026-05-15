package com.example.dawproject.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "orders")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private User buyer;

    @ManyToOne
    private User seller;

    @Column(length = 1000)
    private String contactMessage;

    private String status = "PENDING";

    public Order() {
    }

    public Order(Product product, User buyer, User seller, String contactMessage, String status) {
        this.product = product;
        this.buyer = buyer;
        this.seller = seller;
        this.contactMessage = contactMessage;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public String getContactMessage() {
        return contactMessage;
    }

    public void setContactMessage(String contactMessage) {
        this.contactMessage = contactMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductName() {
        return product != null ? product.getName() : "";
    }

    public Double getPrice() {
        return product != null ? product.getPrice() : 0.0;
    }
}