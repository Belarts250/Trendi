package com.Trendi.demo.mapper;


import com.Trendi.demo.dto.ArticleResponse;
import com.Trendi.demo.entity.Article;
import lombok.Builder;

@Builder
public class ArticleMapper {
 public static ArticleResponse toResponse(Article a){
    return ArticleResponse.builder().id(a.getId()).title(a.getId())
 }
}
