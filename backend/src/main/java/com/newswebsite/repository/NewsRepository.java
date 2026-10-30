package com.newswebsite.repository;

import com.newswebsite.model.News;
import com.newswebsite.model.NewsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("SELECT DISTINCT n FROM News n LEFT JOIN FETCH n.categories WHERE n.status = 'PUBLISHED' ORDER BY n.publishedAt DESC")
    List<News> findAllPublishedWithCategories();

    @Query("SELECT DISTINCT n FROM News n JOIN n.categories c WHERE c.id = :categoryId AND n.status = 'PUBLISHED' ORDER BY n.publishedAt DESC")
    List<News> findPublishedByCategoryId(@Param("categoryId") Long categoryId);

    List<News> findByStatusOrderByPublishedAtDesc(NewsStatus status);

    List<News> findByAuthorUser_IdOrderByPublishedAtDesc(Long authorId);
}
