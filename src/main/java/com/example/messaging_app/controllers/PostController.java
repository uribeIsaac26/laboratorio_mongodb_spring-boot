package com.example.messaging_app.controllers;

import com.example.messaging_app.entities.Comment;
import com.example.messaging_app.entities.Post;
import com.example.messaging_app.entities.Reply;
import com.example.messaging_app.repositories.CommentRepository;
import com.example.messaging_app.repositories.PostRepository;
import com.example.messaging_app.repositories.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    @PostMapping
    public Post create(@RequestBody Post post){
        return postRepository.save(post);
    }

    @GetMapping
    public List<Post> getAll(){
        return postRepository.findAll();
    }

    @PostMapping("/{postId}/comment")
    public Comment addComment(@PathVariable String postId, @RequestBody Comment comment){

        comment.setPostId(postId);

        return commentRepository.save(comment);
    }



    @PostMapping("/{commentId}/reply")
    public Reply addResponse(@PathVariable String commentId, @RequestBody Reply reply){
        reply.setCommentId(commentId);

        return replyRepository.save(reply);
    }

    @PostMapping("/{postId}/comment/test/{quantity}")
    public Post insertCommentsMassive(@PathVariable String postId, @PathVariable Integer quantity){

        long inicio = System.currentTimeMillis();

        Post post = postRepository.findById(postId).orElseThrow();

        if (post.getComments() == null){
            post.setComments(new ArrayList<>());
        }

        for (int i = 0; i < quantity; i++){
            Comment comment = new Comment();
            comment.setId(UUID.randomUUID().toString());
            comment.setUser("user_" + i);
            comment.setMessage("Message test " + i);
            post.getComments().add(comment);
        }

        Post postSaved =  postRepository.save(post);

        long fin = System.currentTimeMillis();

        System.out.println("Tiempo: " + (fin - inicio) + " ms");

        return postSaved;
    }
}
