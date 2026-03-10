package com.example.messaging_app.entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
import java.util.List;

@Data
@Document(collection = "comments")
public class Comment {
    private String id;
    private String user;
    private String message;
    private List<Response> responses;
}
