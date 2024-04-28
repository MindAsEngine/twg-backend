package org.mae.twg.backend.services.news;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.news.NewsDTO;
import org.mae.twg.backend.dto.news.NewsLocalRequestDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.news.News;
import org.mae.twg.backend.models.news.NewsLocal;
import org.mae.twg.backend.models.news.NewsMedia;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.news.NewsLocalRepo;
import org.mae.twg.backend.repositories.news.NewsMediaRepo;
import org.mae.twg.backend.repositories.news.NewsRepo;
import org.mae.twg.backend.services.ImageService;
import org.mae.twg.backend.services.ModelType;
import org.mae.twg.backend.services.TravelService;
import org.mae.twg.backend.utils.SlugUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Log4j2
public class NewsService implements TravelService<NewsDTO, NewsLocalRequestDTO> {
    private final NewsRepo newsRepo;
    private final NewsLocalRepo localRepo;
    private final SlugUtils slugUtils;
    private final ImageService imageService;
    private final NewsMediaRepo newsMediaRepo;

    private News findById(Long id) {
        log.debug("Start NewsService.findById");
        News news = newsRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("News with id=" + id + " not found");
                    return new ObjectNotFoundException("News with id=" + id + " not found");
                });
        if (news.getIsDeleted()) {
            log.error("News with id=" + id + " marked as deleted");
            throw new ObjectNotFoundException("News with id=" + id + " marked as deleted");
        }
        log.debug("End NewsService.findById");
        return news;
    }

    private News findBySlug(String slug) {
        log.debug("Start NewsService.findBySlug");
        News news = newsRepo.findBySlug(slug)
                .orElseThrow(() -> {
                    log.error("News with slug=" + slug + " not found");
                    return new ObjectNotFoundException("News with slug=" + slug + " not found");
                });
        if (news.getIsDeleted()) {
            log.error("News with slug=" + slug + " marked as deleted");
            throw new ObjectNotFoundException("News with slug=" + slug + " marked as deleted");
        }
        log.debug("End NewsService.findBySlug");
        return news;
    }

    private List<NewsDTO> modelsToDTOs(Stream<News> news_m, Localization localization) {
        log.debug("Start NewsService.modelsToDTOs");
        List<NewsDTO> newsDTOs = news_m
                .filter(news -> !news.getIsDeleted())
                .filter(news -> news.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(news -> new NewsDTO(news, localization))
                .toList();
        if (newsDTOs.isEmpty()) {
            log.error("News with " + localization + " with localization not found");
            throw new ObjectNotFoundException("News with " + localization + " with localization not found");
        }
        log.debug("End NewsService.modelsToDTOs");
        return newsDTOs;
    }

//    public List<NewsDTO> getAll(Localization localization) {
//        log.debug("Start NewsService.getAll");
//        List<News> news_m = newsRepo.findAll();
//        log.debug("End NewsService.getAll");
//        return modelsToDTOs(news_m.stream(), localization);
//    }
    @Transactional
    public NewsDTO uploadImages(Long id, Localization local, List<MultipartFile> images) throws IOException {
        log.debug("Start NewsService.uploadImages");
        List<String> urls = imageService.saveImages(ModelType.NEWS, images);
        List<NewsMedia> newsMedias = urls.stream().map(NewsMedia::new).toList();
        News news = findById(id);
        for (NewsMedia newsMedia : newsMedias) {
            news.addMedia(newsMedia);
        }
        newsRepo.saveAndFlush(news);
        log.debug("End NewsService.uploadImages");
        return new NewsDTO(news, local);
    }

    public NewsDTO deleteImages(Long id, Localization local, List<String> images) {
        log.debug("Start NewsService.deleteImages");
        imageService.deleteImages(images);
        List<NewsMedia> newsMedias = newsMediaRepo.findByNews_id(id);
        for (NewsMedia newsMedia : newsMedias) {
            if (images.contains(newsMedia.getMediaPath())) {
                newsMediaRepo.delete(newsMedia);
            }
        }
        log.debug("End NewsService.deleteImages");
        return new NewsDTO(findById(id), local);
    }

    public List<NewsDTO> getAllPaged(Localization localization, Integer page, Integer size) {
        log.debug("Start NewsService.getAllPaged");
        Pageable newsPage = null;
        if (page != null && size != null) {
            newsPage = PageRequest.of(page, size);
        }
        Page<News> news_m = newsRepo.findAllByIsDeletedFalse(newsPage);
        log.debug("End NewsService.getAllPaged");
        return modelsToDTOs(news_m.stream(), localization);
    }

    public NewsDTO getById(Long id, Localization localization) {
        log.debug("Start NewsService.getById");
        return new NewsDTO(findById(id), localization);
    }

    public NewsDTO getBySlug(String slug, Localization localization) {
        log.debug("Start NewsService.getBySlug");
        return new NewsDTO(findBySlug(slug), localization);
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Start NewsService.deleteById");
        News news = findById(id);
        news.setIsDeleted(true);
        newsRepo.save(news);
        log.debug("End NewsService.deleteById");
    }

    @Transactional
    public NewsDTO create(NewsLocalRequestDTO newsDTO, Localization localization) {
        log.debug("Start NewsService.create");
        News news = new News();
        newsRepo.saveAndFlush(news);
        NewsLocal local = new NewsLocal(newsDTO.getTitle(),
                        newsDTO.getDescription(),
                        news, localization);
        localRepo.saveAndFlush(local);
        news.addLocal(local);
        news.setSlug(slugUtils.getSlug(news));
        newsRepo.saveAndFlush(news);
        log.debug("End NewsService.create");
        return new NewsDTO(news, localization);
    }

    @Transactional
    public NewsDTO addLocal(Long id, NewsLocalRequestDTO newsDTO, Localization localization) {
        log.debug("Start NewsService.addLocal");
        News news = findById(id);
        boolean isExists = news.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            log.error(localization + " localization for news with id=" + id + " already exists");
            throw new ObjectAlreadyExistsException(
                    localization + " localization for news with id=" + id + " already exists");
        }

        NewsLocal newsLocal =
                new NewsLocal(newsDTO.getTitle(),
                                newsDTO.getDescription(),
                                news, localization);
        newsLocal = localRepo.saveAndFlush(newsLocal);
        news.addLocal(newsLocal);

        news.setSlug(slugUtils.getSlug(news));
        newsRepo.saveAndFlush(news);
        log.debug("End NewsService.addLocal");
        return new NewsDTO(news, localization);
    }

    @Transactional
    public NewsDTO updateLocal(Long id, NewsLocalRequestDTO newsDTO, Localization localization) {
        log.debug("Start NewsService.updateLocal");
        News news = findById(id);
        NewsLocal cur_local = news.getLocals().stream()
                .filter(local -> local.getLocalization() == localization)
                .findFirst()
                .orElseThrow(() -> {
                    log.error(localization + " localization for news with id=" + id + " not found");
                    return new ObjectNotFoundException(
                            localization + " localization for news with id=" + id + " not found");
                });

        cur_local.setTitle(newsDTO.getTitle());
        cur_local.setDescription(newsDTO.getDescription());
        localRepo.saveAndFlush(cur_local);

        news.setSlug(slugUtils.getSlug(news));
        newsRepo.saveAndFlush(news);
        log.debug("End NewsService.updateLocal");
        return new NewsDTO(news, localization);
    }
}