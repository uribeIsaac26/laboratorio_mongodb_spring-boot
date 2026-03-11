package com.example.messaging_app.services;

import com.example.messaging_app.entities.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final MongoTemplate mongoTemplate;

    public void insertReplies(List<Reply> replies){
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Reply.class);

        bulkOperations.insert(replies);
        bulkOperations.execute();
    }
}
