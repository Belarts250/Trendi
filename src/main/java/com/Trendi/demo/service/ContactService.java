package com.Trendi.demo.service;

import com.Trendi.demo.dto.ContactRequest;
import com.Trendi.demo.entity.Contact;
import com.Trendi.demo.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private JavaMailSender mailSender;

    public Contact submitContact(ContactRequest request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setEmail(request.getEmail());
        contact.setMessage(request.getMessage());
        contact.setSent(false);
        
        return contactRepository.save(contact);
    }

    @Scheduled(fixedDelay = 600000) // 10 minutes in milliseconds
    public void sendPendingContactEmails() {
        List<Contact> pendingContacts = contactRepository.findBySentFalse();
        
        for (Contact contact : pendingContacts) {
            try {
                sendContactEmail(contact);
                contact.setSent(true);
                contact.setSentAt(LocalDateTime.now());
                contactRepository.save(contact);
            } catch (Exception e) {
                System.err.println("Failed to send email for contact: " + contact.getId());
                e.printStackTrace();
            }
        }
    }

    private void sendContactEmail(Contact contact) {
        SimpleMailMessage email = new SimpleMailMessage();
        
        // Send to admin email
        String adminEmail = System.getenv("ADMIN_EMAIL");
        if (adminEmail == null) {
            adminEmail = "igihozobelise6@@gmail.com"; // Default admin email
        }
        
        email.setTo(adminEmail);
        email.setFrom(System.getenv("MAIL_USERNAME"));
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
