package com.Trendi.demo.service;

import com.Trendi.demo.dto.ArticleRequest;
import com.Trendi.demo.dto.ArticleResponse;
import com.Trendi.demo.entity.Article;
import com.Trendi.demo.entity.User;
import com.Trendi.demo.repository.ArticleRepository;
import com.Trendi.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
//tes
@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private ArticleService articleService;

    private User mockUser;
    private Article mockArticle;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");

        mockArticle = new Article();
        mockArticle.setId(1L);
        mockArticle.setTitle("Test Title");
        mockArticle.setContent("Test Content");
        mockArticle.setAuthor(mockUser);
    }

    @Test
    void testCreateArticle() {
        ArticleRequest request = new ArticleRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(articleRepository.save(any(Article.class))).thenReturn(mockArticle);

        ArticleResponse response = articleService.createArticle(request, 1L, null);

        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void testGetArticleById() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(mockArticle));

        ArticleResponse response = articleService.getArticleById(1L);

        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());
    }
}
