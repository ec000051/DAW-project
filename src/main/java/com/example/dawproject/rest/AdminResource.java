package com.example.dawproject.rest;

import com.example.dawproject.dao.OrderDAO;
import com.example.dawproject.dao.ProductDAO;
import com.example.dawproject.dao.UserDAO;

import com.example.dawproject.model.Order;
import com.example.dawproject.model.Product;
import com.example.dawproject.model.User;

import jakarta.inject.Inject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    @Inject
    private UserDAO userDAO;

    @Inject
    private ProductDAO productDAO;

    @Inject
    private OrderDAO orderDAO;

    @Context
    private HttpServletRequest request;

    private User getCurrentUser() {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Object user = session.getAttribute("currentUser");

        if (user instanceof User) {
            return (User) user;
        }

        return null;
    }

    private boolean isAdmin() {
        User currentUser = getCurrentUser();

        return currentUser != null &&
                "ADMIN".equals(currentUser.getRole());
    }

    private Response unauthorizedAdmin() {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new MessageResponse("Admin access required"))
                .build();
    }

    @GET
    @Path("/dashboard")
    public Response getDashboardData() {

        if (!isAdmin()) {
            return unauthorizedAdmin();
        }

        List<User> users = userDAO.findAll();
        List<Product> products = productDAO.findAll();
        List<Order> orders = orderDAO.findAll();

        DashboardDto dto = new DashboardDto();

        dto.setTotalUsers(users.size());
        dto.setTotalProducts(products.size());
        dto.setTotalOrders(orders.size());

        dto.setElectronicsProducts(
                countProductsByCategory(products, "Electronics")
        );

        dto.setFurnitureProducts(
                countProductsByCategory(products, "Furniture")
        );

        dto.setBooksProducts(
                countProductsByCategory(products, "Books")
        );

        dto.setAccessoriesProducts(
                countProductsByCategory(products, "Accessories")
        );

        int knownCategories =
                dto.getElectronicsProducts()
                        + dto.getFurnitureProducts()
                        + dto.getBooksProducts()
                        + dto.getAccessoriesProducts();

        dto.setOtherProducts(
                products.size() - knownCategories
        );

        dto.setPendingOrders(
                countOrdersByStatus(orders, "PENDING")
        );

        dto.setApprovedOrders(
                countOrdersByStatus(orders, "APPROVED")
        );

        dto.setRejectedOrders(
                countOrdersByStatus(orders, "REJECTED")
        );

        calculatePercentages(dto);

        return Response.ok(dto).build();
    }

    @GET
    @Path("/users")
    public Response getUsers() {

        if (!isAdmin()) {
            return unauthorizedAdmin();
        }

        List<UserDto> users = userDAO.findAll()
                .stream()
                .map(this::toDto)
                .toList();

        return Response.ok(users).build();
    }

    @DELETE
    @Path("/users/{id}")
    public Response deleteUser(@PathParam("id") Long id) {

        if (!isAdmin()) {
            return unauthorizedAdmin();
        }

        User currentUser = getCurrentUser();
        User user = userDAO.findById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MessageResponse("User not found"))
                    .build();
        }

        if (currentUser.getId().equals(user.getId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new MessageResponse("You cannot delete your own admin account"))
                    .build();
        }

        orderDAO.deleteByUser(user);
        productDAO.deleteByOwner(user);
        userDAO.deleteUser(user);

        return Response.ok(
                new MessageResponse("User deleted successfully")
        ).build();
    }

    @DELETE
    @Path("/users/{id}/products")
    public Response deleteProductsOfUser(@PathParam("id") Long id) {

        if (!isAdmin()) {
            return unauthorizedAdmin();
        }

        User user = userDAO.findById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new MessageResponse("User not found"))
                    .build();
        }

        productDAO.deleteByOwner(user);

        return Response.ok(
                new MessageResponse("User products deleted successfully")
        ).build();
    }

    @DELETE
    @Path("/marketplace")
    public Response resetMarketplaceData() {

        if (!isAdmin()) {
            return unauthorizedAdmin();
        }

        orderDAO.deleteAll();
        productDAO.deleteAll();

        return Response.ok(
                new MessageResponse("Marketplace data reset successfully")
        ).build();
    }

    @GET
    @Path("/orders")
    public Response getOrders() {
        if (!isAdmin()) {
            return unauthorizedAdmin();
        }

        List<OrderDto> orders = orderDAO.findAll()
                .stream()
                .map(this::toOrderDto)
                .toList();

        return Response.ok(orders).build();
    }

    @DELETE
    @Path("/orders/{id}")
    public Response deleteOrder(@PathParam("id") Long id) {
        if (!isAdmin()) {
            return unauthorizedAdmin();
        }

        orderDAO.delete(id);

        return Response.ok(new MessageResponse("Order deleted successfully")).build();
    }

    @GET
    @Path("/products")
    public Response getProducts() {

        if (!isAdmin()) {
            return unauthorizedAdmin();
        }

        List<AdminProductDto> products = productDAO.findAll()
                .stream()
                .map(this::toAdminProductDto)
                .toList();

        return Response.ok(products).build();
    }

    @DELETE
    @Path("/products/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {

        if (!isAdmin()) {
            return unauthorizedAdmin();
        }

        productDAO.delete(id);

        return Response.ok(
                new MessageResponse("Product deleted successfully")
        ).build();
    }

    private AdminProductDto toAdminProductDto(Product product) {

        AdminProductDto dto = new AdminProductDto();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        dto.setStatus(product.getStatus());
        dto.setImageUrl(product.getImageUrl());

        if (product.getOwner() != null) {
            dto.setOwnerUsername(
                    product.getOwner().getUsername()
            );
        }

        return dto;
    }

    public static class AdminProductDto {

        private Long id;
        private String name;
        private String ownerUsername;
        private String category;
        private Double price;
        private String status;
        private String imageUrl;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOwnerUsername() {
            return ownerUsername;
        }

        public void setOwnerUsername(String ownerUsername) {
            this.ownerUsername = ownerUsername;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    private OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto();

        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setContactMessage(order.getContactMessage());

        if (order.getProduct() != null) {
            dto.setProductName(order.getProduct().getName());
        }

        if (order.getBuyer() != null) {
            dto.setBuyerUsername(order.getBuyer().getUsername());
        }

        if (order.getSeller() != null) {
            dto.setSellerUsername(order.getSeller().getUsername());
        }

        return dto;
    }

    public static class OrderDto {
        private Long id;
        private String productName;
        private String buyerUsername;
        private String sellerUsername;
        private String contactMessage;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getBuyerUsername() { return buyerUsername; }
        public void setBuyerUsername(String buyerUsername) { this.buyerUsername = buyerUsername; }

        public String getSellerUsername() { return sellerUsername; }
        public void setSellerUsername(String sellerUsername) { this.sellerUsername = sellerUsername; }

        public String getContactMessage() { return contactMessage; }
        public void setContactMessage(String contactMessage) { this.contactMessage = contactMessage; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    private int countProductsByCategory(
            List<Product> products,
            String category
    ) {

        return (int) products.stream()
                .filter(product -> product.getCategory() != null)
                .filter(product ->
                        product.getCategory().equalsIgnoreCase(category)
                )
                .count();
    }

    private int countOrdersByStatus(
            List<Order> orders,
            String status
    ) {

        return (int) orders.stream()
                .filter(order -> order.getStatus() != null)
                .filter(order ->
                        order.getStatus().equalsIgnoreCase(status)
                )
                .count();
    }

    private void calculatePercentages(DashboardDto dto) {

        int total = dto.getTotalProducts();

        if (total == 0) {
            dto.setElectronicsPercentage(0);
            dto.setFurnitureEndPercentage(0);
            dto.setBooksEndPercentage(0);
            dto.setAccessoriesEndPercentage(0);
            return;
        }

        double electronics =
                dto.getElectronicsProducts() * 100.0 / total;

        double furniture =
                dto.getFurnitureProducts() * 100.0 / total;

        double books =
                dto.getBooksProducts() * 100.0 / total;

        double accessories =
                dto.getAccessoriesProducts() * 100.0 / total;

        dto.setElectronicsPercentage(electronics);

        dto.setFurnitureEndPercentage(
                electronics + furniture
        );

        dto.setBooksEndPercentage(
                electronics + furniture + books
        );

        dto.setAccessoriesEndPercentage(
                electronics + furniture + books + accessories
        );
    }

    private UserDto toDto(User user) {

        UserDto dto = new UserDto();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setBio(user.getBio());
        dto.setProfileImage(user.getProfileImage());

        return dto;
    }

    public static class DashboardDto {

        private int totalUsers;
        private int totalProducts;
        private int totalOrders;

        private int electronicsProducts;
        private int furnitureProducts;
        private int booksProducts;
        private int accessoriesProducts;
        private int otherProducts;

        private int pendingOrders;
        private int approvedOrders;
        private int rejectedOrders;

        private double electronicsPercentage;
        private double furnitureEndPercentage;
        private double booksEndPercentage;
        private double accessoriesEndPercentage;

        public int getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
        }

        public int getTotalProducts() {
            return totalProducts;
        }

        public void setTotalProducts(int totalProducts) {
            this.totalProducts = totalProducts;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
        }

        public int getElectronicsProducts() {
            return electronicsProducts;
        }

        public void setElectronicsProducts(int electronicsProducts) {
            this.electronicsProducts = electronicsProducts;
        }

        public int getFurnitureProducts() {
            return furnitureProducts;
        }

        public void setFurnitureProducts(int furnitureProducts) {
            this.furnitureProducts = furnitureProducts;
        }

        public int getBooksProducts() {
            return booksProducts;
        }

        public void setBooksProducts(int booksProducts) {
            this.booksProducts = booksProducts;
        }

        public int getAccessoriesProducts() {
            return accessoriesProducts;
        }

        public void setAccessoriesProducts(int accessoriesProducts) {
            this.accessoriesProducts = accessoriesProducts;
        }

        public int getOtherProducts() {
            return otherProducts;
        }

        public void setOtherProducts(int otherProducts) {
            this.otherProducts = otherProducts;
        }

        public int getPendingOrders() {
            return pendingOrders;
        }

        public void setPendingOrders(int pendingOrders) {
            this.pendingOrders = pendingOrders;
        }

        public int getApprovedOrders() {
            return approvedOrders;
        }

        public void setApprovedOrders(int approvedOrders) {
            this.approvedOrders = approvedOrders;
        }

        public int getRejectedOrders() {
            return rejectedOrders;
        }

        public void setRejectedOrders(int rejectedOrders) {
            this.rejectedOrders = rejectedOrders;
        }

        public double getElectronicsPercentage() {
            return electronicsPercentage;
        }

        public void setElectronicsPercentage(double electronicsPercentage) {
            this.electronicsPercentage = electronicsPercentage;
        }

        public double getFurnitureEndPercentage() {
            return furnitureEndPercentage;
        }

        public void setFurnitureEndPercentage(double furnitureEndPercentage) {
            this.furnitureEndPercentage = furnitureEndPercentage;
        }

        public double getBooksEndPercentage() {
            return booksEndPercentage;
        }

        public void setBooksEndPercentage(double booksEndPercentage) {
            this.booksEndPercentage = booksEndPercentage;
        }

        public double getAccessoriesEndPercentage() {
            return accessoriesEndPercentage;
        }

        public void setAccessoriesEndPercentage(double accessoriesEndPercentage) {
            this.accessoriesEndPercentage = accessoriesEndPercentage;
        }
    }

    public static class UserDto {

        private Long id;
        private String username;
        private String email;
        private String role;
        private String bio;
        private String profileImage;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }
    }

    public static class MessageResponse {

        private String message;

        public MessageResponse() {
        }

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


}