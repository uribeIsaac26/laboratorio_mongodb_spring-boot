package com.example.messaging_app.controllers;

import com.example.messaging_app.entities.Comment;
import com.example.messaging_app.entities.Post;
import com.example.messaging_app.entities.Response;
import com.example.messaging_app.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostRepository repository;

    @PostMapping
    public Post create(@RequestBody Post post){
        return repository.save(post);
    }

    @GetMapping
    public List<Post> getAll(){
        return repository.findAll();
    }

    @PostMapping("/{id}/comment")
    public Post addComment(@PathVariable String id, @RequestBody Comment comment){
        Post post = repository.findById(id).orElseThrow();

        if (post.getComments() == null){
            post.setComments(new ArrayList<>());
        }

        comment.setId(UUID.randomUUID().toString());

        post.getComments().add(comment);

        return repository.save(post);
    }

    @PostMapping("/{postId}/comment/{commentId}/response")
    public Post addResponse(@PathVariable String postId, @PathVariable String commentId, @RequestBody Response response){
        Post post = repository.findById(postId).orElseThrow();

        for (Comment comment : post.getComments()){
            if (comment.getId().equals(commentId)){
                if(comment.getResponses() == null){
                    comment.setResponses(new ArrayList<>());
                }

                comment.getResponses().add(response);
            }
        }

        return repository.save(post);
    }

    @PostMapping("/{postId}/comment/test/{quantity}")
    public Post insertCommentsMassive(@PathVariable String postId, @PathVariable Integer quantity){

        long inicio = System.currentTimeMillis();

        Post post = repository.findById(postId).orElseThrow();

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

        Post postSaved =  repository.save(post);

        long fin = System.currentTimeMillis();

        System.out.println("Tiempo: " + (fin - inicio) + " ms");

        return postSaved;
    }
}
