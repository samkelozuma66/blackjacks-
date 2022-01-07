package com.example.blackjacks.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String email;
    private String gender;
    private String mobile;
    private String designation;
    private String image;
    private String status;
    private String user_type;

    public LoggedInUser(String userId,
                        String displayName,
                        String email,
                        String gender,
                        String mobile,
                        String designation,
                        String image,
                        String status,
                        String user_type) {
        this.userId = userId;
        this.displayName = displayName;
        this.email          = email;
        this.gender         = gender;
        this.mobile         = mobile;
        this.designation    = designation;
        this.image          = image;
        this.status         = status;
        this.user_type      = user_type;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}