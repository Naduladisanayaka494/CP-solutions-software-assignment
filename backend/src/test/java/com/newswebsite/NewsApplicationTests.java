package com.newswebsite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newswebsite.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

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

    private String loginAndGetToken(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

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
        mockMvc.perform(get("/api/news?categoryId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Sl lost")));

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
                .andExpect(jsonPath("$.categories", hasSize(2)));
    }

    @Test
    void testAuthAndCommentSubmission() throws Exception {
        String token = loginAndGetToken("reader_kamal", "password123");

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
    }

    @Test
    void testAdminCategoryCrudAndPermissions() throws Exception {
        String adminToken = loginAndGetToken("admin_editor", "admin123");
        String readerToken = loginAndGetToken("reader_kamal", "password123");

        // 1. Non-admin reader attempts to create category -> 403 Forbidden
        CategoryRequest catReq = new CategoryRequest("Science", "Scientific discoveries");
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + readerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catReq)))
                .andExpect(status().isForbidden());

        // 2. Admin creates category -> 201 Created
        MvcResult createResult = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Science"))
                .andReturn();

        long catId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // 3. Admin updates category -> 200 OK
        CategoryRequest updateReq = new CategoryRequest("Space & Science", "Astronomy and science");
        mockMvc.perform(put("/api/categories/" + catId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Space & Science"));

        // 4. Admin deletes category -> 200 OK
        mockMvc.perform(delete("/api/categories/" + catId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testReaderCommentEditAndDelete() throws Exception {
        String readerToken = loginAndGetToken("reader_kamal", "password123");

        // 1. Create a comment
        CreateCommentRequest commentRequest = new CreateCommentRequest(
                "Initial comment to be edited",
                "reader_kamal"
        );
        MvcResult createResult = mockMvc.perform(post("/api/news/1/comments")
                        .header("Authorization", "Bearer " + readerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        long commentId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // 2. Edit comment by author -> 200 OK
        UpdateCommentRequest updateReq = new UpdateCommentRequest("Updated comment by author!");
        mockMvc.perform(put("/api/comments/" + commentId)
                        .header("Authorization", "Bearer " + readerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentText").value("Updated comment by author!"));

        // 3. Delete comment by author -> 200 OK
        mockMvc.perform(delete("/api/comments/" + commentId)
                        .header("Authorization", "Bearer " + readerToken))
                .andExpect(status().isOk());
    }

    @Test
    void testWriterSubmitAndEditorApprovalWorkflow() throws Exception {
        String writerToken = loginAndGetToken("writer_sahan", "writer123");
        String adminToken = loginAndGetToken("admin_editor", "admin123");

        // 1. Writer submits new article
        NewsSubmissionRequest submission = new NewsSubmissionRequest(
                "Autonomous Flying Taxis Approved for City Trials",
                "Electric air transport makes historical urban debut.",
                "Urban aviation regulators have cleared commercial test flights for electric vertical takeoff passenger vehicles.",
                List.of(3L) // Technology
        );

        MvcResult submitResult = mockMvc.perform(post("/api/news/submit")
                        .header("Authorization", "Bearer " + writerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"))
                .andReturn();

        long articleId = objectMapper.readTree(submitResult.getResponse().getContentAsString()).get("id").asLong();

        // 2. Verify article does NOT show up in public headlines while pending
        mockMvc.perform(get("/api/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", not(hasItem((int) articleId))));

        // 3. Main Editor checks pending queue
        mockMvc.perform(get("/api/admin/news/pending")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem((int) articleId)));

        // 4. Main Editor approves the article
        mockMvc.perform(put("/api/admin/news/" + articleId + "/approve")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        // 5. Now verify the article appears in public headlines
        mockMvc.perform(get("/api/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem((int) articleId)));
    }
}
