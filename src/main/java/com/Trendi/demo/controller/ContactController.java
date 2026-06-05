package com.Trendi.demo.controller;

import com.Trendi.demo.dto.ContactRequest;
import com.Trendi.demo.entity.Contact;
import com.Trendi.demo.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "http://localhost:3000")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<Map<String, String>> submitContact(@Valid @RequestBody ContactRequest request) {
        try {
            Contact contact = contactService.submitContact(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Contact form submitted successfully. We'll respond within 10 minutes."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit contact form"));
        }
    }
}
