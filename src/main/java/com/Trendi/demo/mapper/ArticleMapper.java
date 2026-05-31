package com.Trendi.demo.mapper;


import com.Trendi.demo.dto.ArticleResponse;
import com.Trendi.demo.entity.Article;
import lombok.Builder;

@Builder
    public class ArticleMapper {

        /**
         * Converts an Article Entity → ArticleResponse DTO.
         * This is the data we send back to the client.
         */
        public static ArticleResponse toResponse(Article article) {
            return ArticleResponse.builder()
                    .id(article.getId())
                    .title(article.getTitle())
                    .content(article.getContent())
                    .imagePath(article.getImagePath())
                    .authorName(article.getAuthor() != null ? article.getAuthor().getName() : "Unknown")
                    .authorId(article.getAuthor() != null ? article.getAuthor().getId() : null)
                    .createdAt(article.getCreatedAt())
                    .updatedAt(article.getUpdatedAt())
                    .build();
        }
}
