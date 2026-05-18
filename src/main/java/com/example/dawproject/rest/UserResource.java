package com.example.dawproject.rest;

import com.example.dawproject.dao.UserDAO;
import com.example.dawproject.model.User;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserDAO userDAO;

    @Context
    private HttpServletRequest request;

    @GET
    @Path("/me")
    public Response getMyProfile() {
        User currentUser = getManagedCurrentUser();

        if (currentUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new UserResponse("No logged-in user found"))
                    .build();
        }

        return Response.ok(toDto(currentUser)).build();
    }

    @GET
    @Path("/{id}")
    public Response getPublicProfile(@PathParam("id") Long id) {
        User user = userDAO.findById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new UserResponse("User not found"))
                    .build();
        }

        return Response.ok(toPublicDto(user)).build();
    }

    @PUT
    @Path("/me")
    public Response updateMyProfile(UserUpdateRequest updateRequest) {
        User currentUser = getManagedCurrentUser();

        if (currentUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new UserResponse("No logged-in user found"))
                    .build();
        }

        currentUser.setUsername(updateRequest.getUsername());
        currentUser.setEmail(updateRequest.getEmail());
        currentUser.setBio(updateRequest.getBio());
        currentUser.setProfileImage(updateRequest.getProfileImage());

        if (updateRequest.getPassword() != null &&
                !updateRequest.getPassword().trim().isEmpty()) {
            currentUser.setPassword(updateRequest.getPassword());
        }

        User updatedUser = userDAO.updateUser(currentUser);

        request.getSession()
                .setAttribute("currentUser", updatedUser);

        return Response.ok(toDto(updatedUser)).build();
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

    private PublicUserDto toPublicDto(User user) {
        PublicUserDto dto = new PublicUserDto();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio());
        dto.setProfileImage(user.getProfileImage());

        return dto;
    }

    public static class UserUpdateRequest {
        private String username;
        private String password;
        private String email;
        private String bio;
        private String profileImage;

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

    public static class PublicUserDto {
        private Long id;
        private String username;
        private String email;
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

    public static class UserResponse {
        private String message;

        public UserResponse() {
        }

        public UserResponse(String message) {
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