package com.example.messaging_app.repositories;

import com.example.messaging_app.entities.Reply;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends MongoRepository<Reply, String> {
    List<Reply> findByCommentId(String commentId);
}
