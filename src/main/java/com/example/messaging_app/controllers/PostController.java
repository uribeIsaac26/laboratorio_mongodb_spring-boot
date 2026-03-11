package com.example.messaging_app.controllers;

import com.example.messaging_app.entities.Comment;
import com.example.messaging_app.entities.Post;
import com.example.messaging_app.entities.Reply;
import com.example.messaging_app.repositories.CommentRepository;
import com.example.messaging_app.repositories.PostRepository;
import com.example.messaging_app.repositories.ReplyRepository;
import com.example.messaging_app.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PostService postService;

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

    @GetMapping("/{postId}/comments")
    public List<Comment> getComments(@PathVariable String postId){
        return commentRepository.findByPostId(postId);
    }

    @GetMapping("/{commentId}/replies")
    public List<Reply> getReplies(@PathVariable String commentId){
        return replyRepository.findByCommentId(commentId);
    }



    @PostMapping("/generate")
    public String insertCommentsMassive(){
        List<Reply> replyList = new ArrayList<>();

        long inicio = System.currentTimeMillis();

        for (int i = 0; i<100; i++){
            Post post = new Post();
            post.setAuthor("Test" + i);
            post.setTitle("Test" + i);
            post.setContent("Test" + i);

            Post postSaved = postRepository.save(post);

            for (int j = 0; j<100;j++){
                Comment comment = new Comment();
                comment.setUser("User " + j);
                comment.setMessage("Message " + j);
                comment.setPostId(postSaved.getId());

                Comment commentSaved = commentRepository.save(comment);

                for (int k = 0; k<100;k++){
                    Reply reply = new Reply();
                    reply.setUser("User" + k);
                    reply.setMessage("Message " + k);
                    reply.setCommentId(commentSaved.getId());

                    replyList.add(reply);

                    if (replyList.size() == 5000){
                        postService.insertReplies(replyList);

                        replyList.clear();
                    }

                }

            }
        }

        if (!replyList.isEmpty()) {
            postService.insertReplies(replyList);
        }

        long fin = System.currentTimeMillis();

        System.out.println("Tiempo: " + ((fin - inicio)/1000) + " s");

        return "Comentarios guardados";
    }
}
