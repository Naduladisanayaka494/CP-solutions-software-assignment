package com.newswebsite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newswebsite.dto.CreateCommentRequest;
import com.newswebsite.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NewsApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCategoriesEndpoint() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))))
                .andExpect(jsonPath("$[*].name", hasItems("Sports", "Entertainment", "Technology")));
    }

    @Test
    void testManyToManyCategoryFiltering() throws Exception {
        // "Sl lost" belongs to BOTH Sports (ID 1) and Entertainment (ID 2)

        // 1. Check under Sports (Category 1)
        mockMvc.perform(get("/api/news?categoryId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Sl lost")));

        // 2. Check under Entertainment (Category 2)
        mockMvc.perform(get("/api/news?categoryId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Sl lost")));
    }

    @Test
    void testNewsDetailEndpoint() throws Exception {
        mockMvc.perform(get("/api/news/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Sl lost"))
                .andExpect(jsonPath("$.content", containsString("cricket team")))
                .andExpect(jsonPath("$.categories", hasSize(2)))
                .andExpect(jsonPath("$.comments", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testAuthAndCommentSubmission() throws Exception {
        // 1. Login with seeded reader
        LoginRequest loginRequest = new LoginRequest("reader_kamal", "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String responseJson = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("token").asText();

        // 2. Post a comment with JWT Bearer token
        CreateCommentRequest commentRequest = new CreateCommentRequest(
                "A fantastic article dissecting the match highlights!",
                "reader_kamal"
        );

        mockMvc.perform(post("/api/news/1/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentText").value("A fantastic article dissecting the match highlights!"))
                .andExpect(jsonPath("$.authorName").value("reader_kamal"));

        // 3. Verify comment appears in news details
        mockMvc.perform(get("/api/news/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[*].commentText",
                        hasItem("A fantastic article dissecting the match highlights!")));
    }
}
