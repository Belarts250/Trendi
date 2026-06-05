package com.Trendi.demo.service;

import com.Trendi.demo.dto.ContactRequest;
import com.Trendi.demo.entity.Contact;
import com.Trendi.demo.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private ContactService contactService;

    private Contact mockContact;

    @BeforeEach
    void setUp() {
        mockContact = new Contact();
        mockContact.setId(1L);
        mockContact.setName("John Doe");
        mockContact.setEmail("john@example.com");
        mockContact.setMessage("Test Message");
        mockContact.setSent(false);
        mockContact.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSubmitContact() {
        ContactRequest request = new ContactRequest();
        request.setName("John Doe");
        request.setEmail("igihozobelise6@gmail.com");
        request.setMessage("Test Message");

        when(contactRepository.save(any(Contact.class))).thenReturn(mockContact);

        Contact response = contactService.submitContact(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertFalse(response.getSent());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void testSendPendingContactEmails() {
        when(contactRepository.findBySentFalse()).thenReturn(Arrays.asList(mockContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(mockContact);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        contactService.sendPendingContactEmails();

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(contactRepository, times(1)).save(mockContact);
        assertTrue(mockContact.getSent());
    }
}
