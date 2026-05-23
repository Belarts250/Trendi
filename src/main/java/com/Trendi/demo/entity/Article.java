package com.Trendi.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imagePath;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User author;

//    @PrePersist ...
//    @PreUpdate

}
