package com.newswebsite.repository;

import com.newswebsite.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("SELECT DISTINCT n FROM News n LEFT JOIN FETCH n.categories ORDER BY n.publishedAt DESC")
    List<News> findAllWithCategories();

    @Query("SELECT DISTINCT n FROM News n JOIN n.categories c WHERE c.id = :categoryId ORDER BY n.publishedAt DESC")
    List<News> findByCategoryId(@Param("categoryId") Long categoryId);
}
