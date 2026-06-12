package com.Trendi.demo.service;

import com.Trendi.demo.dto.ContactRequest;
import com.Trendi.demo.entity.Contact;
import com.Trendi.demo.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private JavaMailSender mailSender;

    // Submit a new contact – send email immediately
    public Contact submitContact(ContactRequest request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setEmail(request.getEmail());
        contact.setMessage(request.getMessage());
        contact.setSent(false);
        contact.setCreatedAt(LocalDateTime.now());  // make sure you have this field in Contact entity
        contact = contactRepository.save(contact);

        // Try to send email right now
        try {
            sendContactEmail(contact);
            contact.setSent(true);
            contact.setSentAt(LocalDateTime.now());
            contact = contactRepository.save(contact);
        } catch (Exception e) {
            // Log the error but don't break the submission – the email will be retried by the scheduler
            System.err.println("Immediate email sending failed for contact ID " + contact.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return contact;
    }

    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void init() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this::sendPendingContactEmails, 10, 10, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void destroy() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    // Scheduled task: runs every 10 minutes to process any unsent emails
    public void sendPendingContactEmails() {
        List<Contact> pendingContacts = contactRepository.findBySentFalse();

        for (Contact contact : pendingContacts) {
            try {
                sendContactEmail(contact);
                contact.setSent(true);
                contact.setSentAt(LocalDateTime.now());
                contactRepository.save(contact);
            } catch (Exception e) {
                System.err.println("Failed to send email for contact ID: " + contact.getId());
                e.printStackTrace();
            }
        }
    }

    // Internal method that actually sends the email
    private void sendContactEmail(Contact contact) {
        SimpleMailMessage email = new SimpleMailMessage();

        // Admin email (where the message goes)
        String adminEmail = System.getenv("ADMIN_EMAIL");
        if (adminEmail == null || adminEmail.isBlank()) {
            adminEmail = "igihozobelise6@gmail.com"; // corrected default
        }
        email.setTo(adminEmail);

        // From address – must be the same as your SMTP username (most providers enforce this)
        String fromAddress = System.getenv("MAIL_USERNAME");
        if (fromAddress == null || fromAddress.isBlank()) {
            throw new IllegalStateException("MAIL_USERNAME environment variable not set");
        }
        email.setFrom(fromAddress);

        email.setSubject("New Contact Form Submission from " + contact.getName());
        email.setText(
                "New contact form submission:\n\n" +
                        "Name: " + contact.getName() + "\n" +
                        "Email: " + contact.getEmail() + "\n" +
                        "Message: " + contact.getMessage() + "\n\n" +
                        "Submitted at: " + contact.getCreatedAt()
        );

        mailSender.send(email);
    }
}