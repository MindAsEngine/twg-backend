package org.mae.twg.backend.repositories.news;

import org.mae.twg.backend.models.news.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepo extends JpaRepository<News, Long> {
    Optional<News> findBySlug(String slug);
}
