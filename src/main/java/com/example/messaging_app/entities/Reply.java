package com.example.messaging_app.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "replies")
public class Reply {
    @Id
    private String id;
    private String user;
    private String message;
    private String commentId;
}
