package com.beautysalon.customer.dto;

public class ContactRequest {
    private String name;
    private String email;
    private String serviceInterest;
    private String message;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getServiceInterest() { return serviceInterest; }
    public void setServiceInterest(String serviceInterest) { this.serviceInterest = serviceInterest; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
