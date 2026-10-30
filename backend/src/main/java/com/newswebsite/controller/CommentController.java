package com.newswebsite.controller;

import com.newswebsite.dto.CommentDto;
import com.newswebsite.dto.UpdateCommentRequest;
import com.newswebsite.model.Comment;
import com.newswebsite.repository.CommentRepository;
import com.newswebsite.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    /**
     * Edit comment. Allowed only if authenticated user is the comment author or is an Admin.
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> editComment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentRequest request,
            Authentication authentication) {

        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        if (!canModifyComment(comment, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not have permission to edit this comment"));
        }

        comment.setCommentText(request.getCommentText());
        Comment updated = commentRepository.save(comment);

        return ResponseEntity.ok(CommentDto.fromEntity(updated));
    }

    /**
     * Delete comment. Allowed only if authenticated user is the comment author or is an Admin.
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteComment(
            @PathVariable Long id,
            Authentication authentication) {

        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        if (!canModifyComment(comment, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not have permission to delete this comment"));
        }

        commentRepository.delete(comment);
        return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
    }

    private boolean canModifyComment(Comment comment, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return false;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Check if user is Admin
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return true;
        }

        // Check if user is the comment author
        return comment.getUser() != null && comment.getUser().getId().equals(userDetails.getId());
    }
}
