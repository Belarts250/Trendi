package com.Trendi.demo.mapper;

import com.Trendi.demo.dto.CommentResponse;
import com.Trendi.demo.entity.Comment;
import lombok.Builder;

@Builder
public class CommentMapper {

    /**
     * Converts a Comment Entity → CommentResponse DTO.
     * This is the data we send back to the client.
     */
    public static CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : "Anonymous")
                .authorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null)
                .articleId(comment.getArticle() != null ? comment.getArticle().getId() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
