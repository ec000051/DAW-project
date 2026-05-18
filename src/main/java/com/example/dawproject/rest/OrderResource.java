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

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    private OrderDAO orderDAO;

    @Inject
    private ProductDAO productDAO;

    @Inject
    private UserDAO userDAO;

    @Context
    private HttpServletRequest request;

    @GET
    @Path("/purchases")
    public Response getMyPurchases() {
        User currentUser = getManagedCurrentUser();

        if (currentUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<Order> purchases = orderDAO.getPurchases(currentUser);

        return Response.ok(purchases).build();
    }

    @GET
    @Path("/sales")
    public Response getMySales() {
        User currentUser = getManagedCurrentUser();

        if (currentUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<Order> sales = orderDAO.getSales(currentUser);

        return Response.ok(sales).build();
    }


    @POST
    public Response createOrder(OrderRequest request) {
        try {
            User currentUser = getManagedCurrentUser();

            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new OrderResponse("No logged-in user found"))
                        .build();
            }

            if (request == null || request.getProductId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new OrderResponse("Product ID is required"))
                        .build();
            }

            Product product = productDAO.findById(request.getProductId());

            if (product == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new OrderResponse("Product not found"))
                        .build();
            }

            if (product.getOwner() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new OrderResponse("Product has no seller"))
                        .build();
            }

            Order order = new Order();
            order.setProduct(product);
            order.setBuyer(currentUser);
            order.setSeller(product.getOwner());
            order.setContactMessage(request.getContactMessage());
            order.setStatus("PENDING");

            orderDAO.create(order);

            return Response.status(Response.Status.CREATED)
                    .entity(new OrderResponse("Order created successfully"))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new OrderResponse("Server error: " + e.getMessage()))
                    .build();
        }
    }


    @PUT
    @Path("/{id}/status")
    public Response updateOrderStatus(@PathParam("id") Long id, OrderStatusRequest request) {
        User currentUser = getManagedCurrentUser();

        if (currentUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if (request == null || request.getStatus() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new OrderResponse("Status is required"))
                    .build();
        }

        Order order = orderDAO.findById(id);

        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new OrderResponse("Order not found"))
                    .build();
        }

        if (order.getSeller() == null || !order.getSeller().getId().equals(currentUser.getId())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new OrderResponse("You are not allowed to update this order"))
                    .build();
        }

        if (!"APPROVED".equals(request.getStatus()) &&
                !"REJECTED".equals(request.getStatus()) &&
                !"PENDING".equals(request.getStatus())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new OrderResponse("Invalid order status"))
                    .build();
        }

        order.setStatus(request.getStatus());
        orderDAO.update(order);

        return Response.ok(new OrderResponse("Order status updated successfully")).build();
    }

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

    private User getManagedCurrentUser() {
        User sessionUser = getCurrentUser();

        if (sessionUser == null) {
            return null;
        }

        return userDAO.findById(sessionUser.getId());
    }

    public static class OrderRequest {
        private Long productId;
        private String contactMessage;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getContactMessage() {
            return contactMessage;
        }

        public void setContactMessage(String contactMessage) {
            this.contactMessage = contactMessage;
        }
    }

    public static class OrderStatusRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class OrderResponse {
        private String message;

        public OrderResponse() {
        }

        public OrderResponse(String message) {
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