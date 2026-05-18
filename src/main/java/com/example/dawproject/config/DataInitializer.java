package com.example.dawproject.config;

import com.example.dawproject.dao.UserDAO;
import com.example.dawproject.dao.ProductDAO;
import com.example.dawproject.model.User;
import com.example.dawproject.model.Product;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class DataInitializer {

    @Inject
    private UserDAO userDAO;

    @Inject
    private ProductDAO productDAO;

    @PostConstruct
    public void init() {
        userDAO.createSchema();

        if (!userDAO.exists("admin")) {
            userDAO.addUser(new User("admin", "admin123", "admin@test.com", "ADMIN"));
        }

        if (!userDAO.exists("user")) {
            userDAO.addUser(new User("user", "user123", "user@test.com", "USER"));
        }

        createDemoUserWithProducts(
                "anna",
                "anna123",
                "anna@test.com",
                new String[][]{
                        {"Laptop", "450", "Used laptop in good condition", "Electronics", "https://images.unsplash.com/photo-1496181133206-80ce9b88a853"},
                        {"Backpack", "25", "Student backpack with many compartments", "Accessories", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62"},
                        {"Desk Lamp", "18", "Small desk lamp for study room", "Home", "https://images.unsplash.com/photo-1507473885765-e6ed057f782c"}
                }
        );

        createDemoUserWithProducts(
                "mark",
                "mark123",
                "mark@test.com",
                new String[][]{
                        {"Headphones", "35", "Wireless headphones with good sound", "Electronics", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e"},
                        {"Coffee Maker", "40", "Simple coffee maker, barely used", "Kitchen", "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085"},
                        {"Office Chair", "70", "Comfortable chair for studying or working", "Furniture", "https://images.unsplash.com/photo-1586023492125-27b2c045efd7"}
                }
        );

        createDemoUserWithProducts(
                "sofia",
                "sofia123",
                "sofia@test.com",
                new String[][]{
                        {"Bicycle", "120", "City bicycle in good condition", "Transport", "https://images.unsplash.com/photo-1485965120184-e220f721d03e"},
                        {"Winter Jacket", "45", "Warm jacket, size M", "Clothing", "https://images.unsplash.com/photo-1544022613-e87ca75a784a"},
                        {"Books Set", "30", "Set of university books", "Books", "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f"}
                }
        );
    }

    private void createDemoUserWithProducts(String username, String password, String email, String[][] products) {
        if (!userDAO.exists(username)) {
            User user = new User(username, password, email, "USER");
            userDAO.addUser(user);

            for (String[] item : products) {
                Product product = new Product(
                        item[0],
                        Double.parseDouble(item[1]),
                        item[2],
                        user
                );

                product.setCategory(item[3]);
                product.setImageUrl(item[4]);
                product.setStatus("AVAILABLE");

                productDAO.create(product);
            }
        }
    }
}