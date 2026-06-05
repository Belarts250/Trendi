package com.Trendi.demo.service;

import com.Trendi.demo.dto.CommentRequest;
import com.Trendi.demo.dto.CommentResponse;
import com.Trendi.demo.entity.Article;
import com.Trendi.demo.entity.Comment;
import com.Trendi.demo.entity.User;
import com.Trendi.demo.exception.BadRequestException;
import com.Trendi.demo.exception.ResourceNotFoundException;
import com.Trendi.demo.mapper.CommentMapper;
import com.Trendi.demo.repository.ArticleRepository;
import com.Trendi.demo.repository.CommentRepository;
import com.Trendi.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(Long articleId, Long userId, CommentRequest request) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setArticle(article);
        comment.setAuthor(user);

        Comment saved = commentRepository.save(comment);
        return CommentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByArticle(Long articleId) {
        // Verify article exists
        articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        return commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId)
                .stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, Long userId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new BadRequestException("You can only update your own comments");
        }

        comment.setText(request.getText());
        Comment updated = commentRepository.save(comment);
        return CommentMapper.toResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getUserComments(Long userId) {
        return commentRepository.findByAuthorId(userId)
                .stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
