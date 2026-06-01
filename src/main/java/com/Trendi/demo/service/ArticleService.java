package com.Trendi.demo.service;

import com.Trendi.demo.dto.ArticleRequest;
import com.Trendi.demo.dto.ArticleResponse;
import com.Trendi.demo.entity.Article;
import com.Trendi.demo.entity.User;
import com.Trendi.demo.exception.ResourceNotFoundException;
import com.Trendi.demo.mapper.ArticleMapper;
import com.Trendi.demo.repository.ArticleRepository;
import com.Trendi.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    public ArticleResponse createArticle(ArticleRequest request, Long userId, String imagePath){
        User author = userRepository.findById(userId)

        .orElseThrow(()-> new ResourceNotFoundException("User not found "));

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setAuthor(author);
        article.setImagePath(imagePath);

        Article saved = articleRepository.save(article);

        return ArticleMapper.toResponse(saved);
    }
}
