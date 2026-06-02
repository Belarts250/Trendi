package com.Trendi.demo.service;

import com.Trendi.demo.dto.ArticleRequest;
import com.Trendi.demo.dto.ArticleResponse;
import com.Trendi.demo.entity.Article;
import com.Trendi.demo.entity.User;
import com.Trendi.demo.exception.BadRequestException;
import com.Trendi.demo.exception.ResourceNotFoundException;
import com.Trendi.demo.mapper.ArticleMapper;
import com.Trendi.demo.repository.ArticleRepository;
import com.Trendi.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<ArticleResponse> getAllArticles(){
        return articleRepository.findAll()
                .stream()
                .map(ArticleMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ArticleResponse getArticleById(Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Article not find with id" + id));
        return ArticleMapper.toResponse(article);
    }

    public ArticleResponse updateArticle(Long articleId, ArticleRequest request, Long userId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(()-> new ResourceNotFoundException("Article not found" + articleId));

        if(!article.getAuthor().getId().equals(userId)){
            throw  new BadRequestException("You can edit only your Articles");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        Article updated = articleRepository.save(article);

        return ArticleMapper.toResponse(updated);
    }


    public void deleteArticle(Long articleId, Long userId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found" + articleId));

        if(!article.getAuthor().getId().equals(userId)){
            throw new BadRequestException("You can delete only your articles");
        }

        articleRepository.delete(article);
    }

    public List<ArticleResponse> getArticlesByUser(Long userId){
        return articleRepository.findByAuthorId(userId)
                .stream()
                .map(ArticleMapper::toResponse)
                .collect(Collectors.toList());    }
}
