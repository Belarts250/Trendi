package com.Trendi.demo.dto;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ArticleResponse {
    private Long id;
    private String title;
    private String content;
    private String authorName;
}
