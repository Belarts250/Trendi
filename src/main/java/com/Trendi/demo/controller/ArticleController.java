package com.Trendi.demo.controller;

import com.Trendi.demo.config.JwtUtil;
import com.Trendi.demo.dto.ArticleResponse;
import com.Trendi.demo.service.ArticleService;
import com.Trendi.demo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private FileService fileService;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<List<ArticleResponse>> getAllArticles(){
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long id){
        return ResponseEntity.ok(articleService.getArticleById(id));
    }


}
