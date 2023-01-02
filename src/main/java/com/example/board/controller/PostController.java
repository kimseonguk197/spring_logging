package com.example.board.controller;

import com.example.board.domain.Author;
import com.example.board.domain.Post;
import com.example.board.service.AuthorService;
import com.example.board.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class PostController {
    @Autowired
    private ObjectMapper mapper;
    private final PostService postService;
    private final AuthorService authorService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public PostController(PostService postService, AuthorService authorService) {
        this.postService = postService;
        this.authorService = authorService;
    }

//    api요청의 경우
    @PostMapping("/posts/api/new")
    @ResponseBody
    public void createApi(@RequestBody PostForm postForm) throws JsonProcessingException {
        Post post = new Post();
        post.setTitle(postForm.getTitle());
        post.setContents(postForm.getContents());
        post.setEmail(postForm.getEmail());
        post.setCreateDate(LocalDateTime.now());
        try{
            Author a1 = authorService.findByEmail(postForm.getEmail()).orElse(null);
            post.setAuthor_id(a1);
            postService.create(post);
        }catch (Exception e){
            String tempText = mapper.writeValueAsString(postForm);
            logger.error("PostController createApi");
            logger.error("Error request : {}", tempText);
            e.printStackTrace();
        }
    }


    //    화면요청의 경우 return 화면
    @PostMapping("/posts/new")
    public String create(PostForm postForm) throws JsonProcessingException{
        Post post = new Post();
        post.setTitle(postForm.getTitle());
        post.setContents(postForm.getContents());
        post.setEmail(postForm.getEmail());
        post.setCreateDate(LocalDateTime.now());
        try{
            Author a1 = authorService.findByEmail(postForm.getEmail()).orElse(null);
            post.setAuthor_id(a1);
            postService.create(post);
            return "redirect:/";
        }catch (Exception e){
            logger.error("info request postForm = {}, {}, {}", postForm.getTitle(),post.getContents(), post.getEmail());
            e.printStackTrace();
            return "redirect:/error";
        }
    }
    @GetMapping("posts/api/findById")
    @ResponseBody
    public Post findByIdApi(@RequestParam(value="id")Long id) throws Exception {
        try{
            Post post = postService.findById(id).orElse(null);
            return post;
        }catch (Exception e){
            logger.info("posts/api/findById");
            logger.info("Error request : {}", id);
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("posts/findById")
    public String findById(@RequestParam(value="id")Long id, Model model){
        try{
            model.addAttribute("post",  postService.findById(id).orElse(null));
            return "posts/postDetail";
        }catch (Exception e){
            return "/error";
        }
    }


    @GetMapping("/posts/new")
    public String createForm(){
        return "posts/createPostForm";
    }


    @GetMapping("/posts")
    public String postList(Model model){
//        key, value 값으로 넘겨줘야한다.
        model.addAttribute("posts", postService.findAll());
        return "posts/postList";
    }
}
