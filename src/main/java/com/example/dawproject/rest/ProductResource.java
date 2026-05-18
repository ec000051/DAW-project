package com.example.dawproject.rest;

import com.example.dawproject.dao.ProductDAO;
import com.example.dawproject.model.Product;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    private ProductDAO productDAO;

    @GET
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") Long id) {
        Product product = productDAO.findById(id);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(product).build();
    }

    @POST
    public Response createProduct(Product product) {
        productDAO.create(product);
        return Response.status(Response.Status.CREATED).entity(product).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") Long id, Product updatedProduct) {
        Product existing = productDAO.findById(id);

        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setCategory(updatedProduct.getCategory());
        existing.setImageUrl(updatedProduct.getImageUrl());
        existing.setStatus(updatedProduct.getStatus());

        productDAO.update(existing);

        return Response.ok(existing).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        Product product = productDAO.findById(id);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        productDAO.delete(id);

        return Response.noContent().build();
    }
}