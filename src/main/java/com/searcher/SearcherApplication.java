package com.searcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication(exclude = ThymeleafAutoConfiguration.class)
@Slf4j
public class SearcherApplication {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(SearcherApplication.class, args);
        log.info("http://localhost:8080");
    }


//    @RestController
//    static class TweetController {
//        @GetMapping
//        public Mono<String> getAllTweets() {
//
//            return Mono.just("test");
//        }
//
//
//    }
}