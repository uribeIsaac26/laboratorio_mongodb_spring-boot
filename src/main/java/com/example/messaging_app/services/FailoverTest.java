package com.example.messaging_app.services;

import com.example.messaging_app.entities.Post;
import com.example.messaging_app.repositories.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@Component
@Slf4j
public class FailoverTest implements CommandLineRunner {
    private final PostRepository postRepository;
    @Override
    public void run(String... args) throws Exception {
        int i = 0;

        while(true){
            try {
                postRepository.save(new Post(null, "Post prueba failover" + i, "Prueba", "Authoe prueba"));
                log.info("Documento "+ i +  " guardado");
                i++;
                Thread.sleep(1000);

            }catch (Exception e){
                log.error("Error temporal " + e.getMessage());
            }
        }
    }
}
