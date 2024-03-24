package org.mae.twg.backend.repositories.news;

import org.mae.twg.backend.models.news.NewsMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsMediaRepo extends JpaRepository<NewsMedia, Long> {
    List<NewsMedia> findByNews_id(Long id);
}
