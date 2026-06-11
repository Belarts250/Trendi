package com.Trendi.demo.repository;

import com.Trendi.demo.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Derived Query for pagination
    Page<Article> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // JPQL Query for pagination
    @Query("SELECT a FROM Article a WHERE a.author.id = :authorId")
    Page<Article> findArticlesByAuthorIdWithJPQL(@org.springframework.data.repository.query.Param("authorId") Long authorId, Pageable pageable);

    // Native Query for pagination
    @Query(value = "SELECT * FROM articles WHERE created_at >= :startDate",
           countQuery = "SELECT count(*) FROM articles WHERE created_at >= :startDate",
           nativeQuery = true)
    Page<Article> findRecentArticlesNative(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, Pageable pageable);

    List<Article> findByAuthorId(Long authorId);

    List<Article> findByTitleContainingIgnoreCase(String keyword);
}
