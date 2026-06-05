package com.Trendi.demo.controller;

import com.Trendi.demo.config.JwtUtil;
import com.Trendi.demo.dto.CommentRequest;
import com.Trendi.demo.dto.CommentResponse;
import com.Trendi.demo.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comments")
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/article/{articleId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long articleId,
            @Valid @RequestBody CommentRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);
        CommentResponse response = commentService.addComment(articleId, userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentResponse>> getArticleComments(@PathVariable Long articleId) {
        List<CommentResponse> comments = commentService.getCommentsByArticle(articleId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = getUserIdFromHeader(authHeader);
        CommentResponse response = commentService.updateComment(commentId, userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getUserComments(@PathVariable Long userId) {
        List<CommentResponse> comments = commentService.getUserComments(userId);
        return ResponseEntity.ok(comments);
    }

    private Long getUserIdFromHeader(String authHeader) {
        String token = authHeader.substring(7).trim();
        return jwtUtil.getUserIdFromToken(token);
    }
}
