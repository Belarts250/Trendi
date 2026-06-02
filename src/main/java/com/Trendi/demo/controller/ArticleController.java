package com.Trendi.demo.controller;

import com.Trendi.demo.config.JwtUtil;
import com.Trendi.demo.dto.ArticleRequest;
import com.Trendi.demo.dto.ArticleResponse;
import com.Trendi.demo.service.ArticleService;
import com.Trendi.demo.service.FileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAllArticles(){
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long id){
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArticleResponse> createArticle(@RequestHeader("article") @Valid ArticleRequest request, @RequestPart(value = "image", required = false) MultipartFile image, @RequestPart("Authorization") String authHeader){

        Long userId = jwtUtil.getUserIdFromToken(authHeader);

        String imagePath = (image != null && !image.isEmpty()) ? fileService.saveFile(image) : null;

        ArticleResponse response = articleService.createArticle(request, userId, imagePath);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> updateArticle( @PathVariable Long id, @Valid @RequestBody ArticleRequest request, @RequestHeader("Authorization") String authHeader){
        Long userId = jwtUtil.getUserIdFromToken(authHeader);
        return ResponseEntity.ok(articleService.updateArticle(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id, @RequestHeader("Authorization") String authHeader ){
        Long userId = jwtUtil.getUserIdFromToken(authHeader);

        articleService.deleteArticle(id, userId);

        return  ResponseEntity.noContent().build();
    }

    private Long getUserIdFromHeader(String authHeader){
        String token = authHeader.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }
}
