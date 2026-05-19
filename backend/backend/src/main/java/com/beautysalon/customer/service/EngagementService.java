package com.beautysalon.customer.service;

import com.beautysalon.customer.dto.ContactRequest;
import com.beautysalon.customer.dto.NewsletterRequest;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class EngagementService {

    private static final String INQUIRIES_FILE = "inquiries.txt";
    private static final String NEWSLETTER_FILE = "newsletter.txt";

    public String saveContact(ContactRequest request) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INQUIRIES_FILE, true))) {
            writer.write(String.format("%s|%s|%s|%s|%s\n",
                    LocalDateTime.now(),
                    request.getName(),
                    request.getEmail(),
                    request.getServiceInterest(),
                    request.getMessage().replace("\n", " ")
            ));
            return "Contact inquiry saved successfully.";
        } catch (IOException e) {
            throw new RuntimeException("Error saving contact inquiry", e);
        }
    }

    public String saveNewsletter(NewsletterRequest request) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NEWSLETTER_FILE, true))) {
            writer.write(String.format("%s|%s\n",
                    LocalDateTime.now(),
                    request.getEmail()
            ));
            return "Subscribed to newsletter successfully.";
        } catch (IOException e) {
            throw new RuntimeException("Error saving newsletter subscription", e);
        }
    }
}
