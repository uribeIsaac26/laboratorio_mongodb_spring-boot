package com.example.messaging_app.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "post")
public class Post {
    @Id
    private String id;
    private String title;
    private String content;
    private String author;
}
