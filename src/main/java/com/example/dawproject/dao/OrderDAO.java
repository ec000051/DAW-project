package com.example.dawproject.dao;

import com.example.dawproject.model.Order;
import com.example.dawproject.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class OrderDAO {

    @PersistenceContext(unitName = "dawPU")
    private EntityManager em;

    public void create(Order order) {
        em.persist(order);
    }

    public List<Order> getPurchases(User buyer) {
        return em.createQuery(
                        "SELECT o FROM Order o WHERE o.buyer.id = :buyerId",
                        Order.class
                )
                .setParameter("buyerId", buyer.getId())
                .getResultList();
    }

    public List<Order> getSales(User seller) {
        return em.createQuery(
                        "SELECT o FROM Order o WHERE o.seller.id = :sellerId",
                        Order.class
                )
                .setParameter("sellerId", seller.getId())
                .getResultList();
    }

    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o", Order.class)
                .getResultList();
    }

    public void delete(Long id) {
        Order order = findById(id);

        if (order != null) {
            em.remove(order);
        }
    }

    public Order findById(Long id) {
        return em.find(Order.class, id);
    }

    public Order update(Order order) {
        return em.merge(order);
    }

    public void deleteAll() {
        em.createQuery("DELETE FROM Order o").executeUpdate();
    }

    public Long countOrders() {
        return em.createQuery("SELECT COUNT(o) FROM Order o", Long.class)
                .getSingleResult();
    }

    public Long countPendingOrders() {
        return em.createQuery("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'", Long.class)
                .getSingleResult();
    }

    public Long countApprovedOrders() {
        return em.createQuery("SELECT COUNT(o) FROM Order o WHERE o.status = 'APPROVED'", Long.class)
                .getSingleResult();
    }

    public Long countRejectedOrders() {
        return em.createQuery("SELECT COUNT(o) FROM Order o WHERE o.status = 'REJECTED'", Long.class)
                .getSingleResult();
    }

    public void deleteByUser(User user) {
        em.createQuery("DELETE FROM Order o WHERE o.buyer.id = :userId OR o.seller.id = :userId")
                .setParameter("userId", user.getId())
                .executeUpdate();
    }
}