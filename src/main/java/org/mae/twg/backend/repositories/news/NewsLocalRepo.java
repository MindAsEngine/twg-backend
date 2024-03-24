package org.mae.twg.backend.repositories.news;

import org.mae.twg.backend.models.news.NewsLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsLocalRepo extends JpaRepository<NewsLocal, Long> {
}
