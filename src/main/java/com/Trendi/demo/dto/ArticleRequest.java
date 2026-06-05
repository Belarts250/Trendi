package com.Trendi.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Article title must be under 200 characters")
    private String title;

    @NotBlank(message = "content is required")
    private String content;

    private String imagePath;
//    @NotBlank(message = "Author name is required")
//    private String author;

//    private  String imagePath;
}
