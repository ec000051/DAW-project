package com.example.dawproject.dao;

import com.example.dawproject.model.Product;
import com.example.dawproject.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ProductDAO {

    @PersistenceContext(unitName = "dawPU")
    private EntityManager em;

    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM Product p", Product.class)
                .getResultList();
    }

    public Product findById(Long id) {
        return em.find(Product.class, id);
    }

    public void create(Product product) {
        em.persist(product);
    }

    public Product update(Product product) {
        return em.merge(product);
    }

    public void delete(Long id) {
        Product product = findById(id);
        if (product != null) {
            em.remove(product);
        }
    }

    public List<Product> findByOwner(User owner) {
        return em.createQuery(
                        "SELECT p FROM Product p WHERE p.owner = :owner",
                        Product.class
                )
                .setParameter("owner", owner)
                .getResultList();
    }

    public void deleteByOwner(User owner) {
        List<Product> products = findByOwner(owner);

        for (Product product : products) {
            Product managedProduct = em.merge(product);
            em.remove(managedProduct);
        }
    }

    public void deleteAll() {
        em.createQuery("DELETE FROM Product p").executeUpdate();
    }

    public Long countProducts() {
        return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class)
                .getSingleResult();
    }

    public Long countAvailableProducts() {
        return em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.status = 'AVAILABLE'", Long.class)
                .getSingleResult();
    }

    public Long countReservedProducts() {
        return em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.status = 'RESERVED'", Long.class)
                .getSingleResult();
    }

    public Long countSoldProducts() {
        return em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.status = 'SOLD'", Long.class)
                .getSingleResult();
    }

    public Long countProductsByCategory(String category) {
        return em.createQuery(
                        "SELECT COUNT(p) FROM Product p WHERE p.category = :category",
                        Long.class
                )
                .setParameter("category", category)
                .getSingleResult();
    }

    public List<Product> searchProducts(String keyword, String category) {
        String jpql = "SELECT p FROM Product p WHERE p.status = 'AVAILABLE'";

        if (keyword != null && !keyword.trim().isEmpty()) {
            jpql += " AND LOWER(p.name) LIKE LOWER(:keyword)";
        }

        if (category != null && !category.trim().isEmpty() && !category.equals("ALL")) {
            jpql += " AND p.category = :category";
        }

        jpql += " ORDER BY p.id DESC";

        var query = em.createQuery(jpql, Product.class);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.trim() + "%");
        }

        if (category != null && !category.trim().isEmpty() && !category.equals("ALL")) {
            query.setParameter("category", category);
        }

        return query.getResultList();
    }

    public List<Product> findLatestAvailable(int limit) {
        return em.createQuery(
                        "SELECT p FROM Product p WHERE p.status = 'AVAILABLE' ORDER BY p.id DESC",
                        Product.class
                )
                .setMaxResults(limit)
                .getResultList();
    }
}