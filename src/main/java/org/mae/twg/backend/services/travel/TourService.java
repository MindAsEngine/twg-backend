package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.GradeData;
import org.mae.twg.backend.dto.travel.request.CommentDTO;
import org.mae.twg.backend.dto.travel.request.geo.TourGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.TourLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.TourLogicDTO;
import org.mae.twg.backend.dto.travel.request.logic.TourPeriodDTO;
import org.mae.twg.backend.dto.travel.response.TourDTO;
import org.mae.twg.backend.dto.travel.response.comments.TourCommentDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.travel.*;
import org.mae.twg.backend.models.travel.comments.TourComment;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.enums.Stars;
import org.mae.twg.backend.models.travel.enums.TourType;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.models.travel.media.TourMedia;
import org.mae.twg.backend.repositories.travel.TourPeriodRepo;
import org.mae.twg.backend.repositories.travel.TourRepo;
import org.mae.twg.backend.repositories.travel.comments.TourCommentsRepo;
import org.mae.twg.backend.repositories.travel.images.TourMediaRepo;
import org.mae.twg.backend.repositories.travel.localization.TourLocalRepo;
import org.mae.twg.backend.services.ImageService;
import org.mae.twg.backend.services.ModelType;
import org.mae.twg.backend.services.TravelService;
import org.mae.twg.backend.utils.SlugUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Log4j2
public class TourService implements TravelService<TourDTO, TourLocalDTO> {
    private final TourRepo tourRepo;
    private final TourLocalRepo localRepo;
    private final TourCommentsRepo commentsRepo;
    private final TourMediaRepo tourMediaRepo;
    private final TourPeriodRepo tourPeriodRepo;
    private final SlugUtils slugUtils;
    private final ImageService imageService;
    private final HotelService hotelService;
    private final CountryService countryService;
    private final HospitalService hospitalService;
    private final TagService tagService;

    public Tour findById(Long id) {
        log.debug("Start TourService.findById");
        Tour tour = tourRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Tour with id=" + id + " not found");
                    return new ObjectNotFoundException("Tour with id=" + id + " not found");
                });
        if (tour.getIsDeleted() || !tour.getIsActive()) {
            log.error("Tour with id=" + id + " marked as deleted or inactive");
            throw new ObjectNotFoundException("Tour with id=" + id + " marked as deleted");
        }
        log.debug("End TourService.findById");
        return tour;
    }

    private Tour findBySlug(String slug) {
        log.debug("Start TourService.findBySlug");
        Tour tour = tourRepo.findBySlug(slug)
                .orElseThrow(() -> {
                    log.error("Tour with slug=" + slug + " not found");
                    return new ObjectNotFoundException("Tour with slug=" + slug + " not found");
                });
        if (tour.getIsDeleted() || !tour.getIsActive()) {
            log.error("Tour with slug=" + slug + " marked as deleted or inactive");
            throw new ObjectNotFoundException("Tour with slug=" + slug + " marked as deleted");
        }
        log.debug("End TourService.findBySlug");
        return tour;
    }

    private TourDTO addGrade(TourDTO tourDTO) {
        log.debug("Start TourService.addGrade");
        GradeData gradeData = commentsRepo.averageGradeByTourId(tourDTO.getId());
        if (gradeData == null) {
            tourDTO.setCommentAmount(0L);
            return tourDTO;
        }
        tourDTO.setGrade(gradeData.getGrade());
        tourDTO.setCommentAmount(gradeData.getCount());
        log.debug("End TourService.addGrade");
        return tourDTO;
    }

    private TourDTO addGrade(TourDTO tourDTO, GradeData gradeData) {
        log.debug("Start TourService.addGrade");
        if (gradeData == null) {
            tourDTO.setCommentAmount(0L);
            return tourDTO;
        }
        tourDTO.setGrade(gradeData.getGrade());
        tourDTO.setCommentAmount(gradeData.getCount());
        log.debug("End TourService.addGrade");
        return tourDTO;
    }

    public List<TourDTO> modelsToDTOs(Stream<Tour> tours, Localization localization) {
        log.debug("Start TourService.modelsToAdminDTOs");
        Map<Long, GradeData> grades = commentsRepo.allAverageGrades()
                .stream().collect(Collectors.toMap(GradeData::getId, Function.identity()));
        List<TourDTO> tourDTOs = tours
                .filter(tour -> !tour.getIsDeleted() && tour.getIsActive())
                .filter(tour -> tour.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(tour -> new TourDTO(tour, localization))
                .map(tourDTO -> addGrade(tourDTO, grades.getOrDefault(tourDTO.getId(), null)))
                .toList();
        if (tourDTOs.isEmpty()) {
            log.error("Tours with " + localization + " with localization not found");
            throw new ObjectNotFoundException("Tours with " + localization + " with localization not found");
        }
        log.debug("End TourService.modelsToAdminDTOs");
        return tourDTOs;
    }


    public Pageable getPageable(Integer page, Integer size) {
        log.debug("Start TourService.getPageable");
        if (page != null && size != null) {
            log.debug("End TourService.getPageable");
            return PageRequest.of(page, size);
        }
        log.debug("End TourService.getPageable");
        return null;
    }

    public List<TourDTO> findByTitle(String title,
                                     Localization localization,
                                     Integer page, Integer size) {
        log.debug("Start TourService.findByTitle");
        return modelsToDTOs(tourRepo.findByTitle(localization.name(), title,
                getPageable(page, size)).stream(), localization);
    }

    public List<TourDTO> findByFilters(List<Long> countryIds,
                                       List<Long> tagIds,
                                       List<Long> hospitalId,
                                       List<Long> hotelIds,
                                       List<TourType> types,
                                       Integer minDuration,
                                       Integer maxDuration,
                                       Long minCost,
                                       Long maxCost,
                                       List<Stars> stars,
                                       List<Long> resortIds,
                                       Localization localization,
                                       Integer page, Integer size) {
        log.debug("Start TourService.findByFilters");
        return modelsToDTOs(tourRepo.findFilteredFours(
                localization.name(),
                countryIds, tagIds, hospitalId, hotelIds,
                types.stream().map(TourType::name).toList(),
                minDuration, maxDuration,
                minCost, maxCost,
                stars.stream().map(Stars::name).toList(),
                resortIds,
                getPageable(page, size)).stream(), localization);
    }

    @Transactional
    public TourDTO uploadImage(Long id, Localization local, MultipartFile image) throws IOException {
        log.debug("Start TourService.uploadImage");
        String url = imageService.saveImage(ModelType.TOUR, image);
        TourMedia tourMedia = new TourMedia(url);
        tourMediaRepo.saveAndFlush(tourMedia);
        Tour tour = findById(id);
        tour.setHeader(tourMedia);
        tourRepo.saveAndFlush(tour);
        log.debug("End TourService.uploadImage");
        return new TourDTO(tour, local);
    }

    @Transactional
    public TourDTO uploadImages(Long id, Localization local, List<MultipartFile> images) throws IOException {
        log.debug("Start TourService.uploadImages");
        List<String> urls = imageService.saveImages(ModelType.TOUR, images);
        List<TourMedia> tourMedias = urls.stream().map(TourMedia::new).toList();
        Tour tour = findById(id);
        for (TourMedia tourMedia : tourMedias) {
            tourMediaRepo.saveAndFlush(tourMedia);
            tour.addMedia(tourMedia);
        }
        tourRepo.saveAndFlush(tour);
        log.debug("End TourService.uploadImages");
        return new TourDTO(tour, local);
    }

    public TourDTO deleteImages(Long id, Localization local, List<String> images) {
        log.debug("Start TourService.deleteImages");
        imageService.deleteImages(images);
        List<TourMedia> tourMedias = tourMediaRepo.findByTour_id(id);
        for (TourMedia tourMedia : tourMedias) {
            Tour tour = findById(id);
            if (tour.getHeader() == tourMedia) {
                tour.removeHeader(tourMedia);
                tourRepo.saveAndFlush(tour);
            }
            if (images.contains(tourMedia.getMediaPath())) {
                tourMediaRepo.delete(tourMedia);
            }
        }
        log.debug("End TourService.deleteImages");
        return new TourDTO(findById(id), local);
    }

//    public List<TourDTO> getAll(Localization localization) {
//        log.debug("Start TourService.getAll");
//        Page<Tour> tours = tourRepo.findAllActiveByLocal(localization.name(), null);
//        log.debug("End TourService.getAll");
//        return modelsToDTOs(tours.stream(), localization);
//    }

    public List<TourDTO> getAllPaged(Localization localization, Integer page, Integer size) {
        log.debug("Start TourService.getAllPaged");
        Pageable toursPage = null;
        if (page != null && size != null) {
            toursPage = PageRequest.of(page, size);
        }
        Page<Tour> tours = tourRepo.findAllActiveByLocal(localization.name(), toursPage);
        log.debug("End TourService.getAllPaged");
        return modelsToDTOs(tours.stream(), localization);
    }

    public TourDTO getById(Long id, Localization localization) {
        log.debug("Start TourService.getById");
        return addGrade(new TourDTO(findById(id), localization));
    }

    public TourDTO getBySlug(String slug, Localization localization) {
        log.debug("Start TourService.getBySlug");
        return addGrade(new TourDTO(findBySlug(slug), localization));
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Start TourService.deleteById");
        Tour tour = findById(id);
        tour.setIsDeleted(true);
        tourRepo.save(tour);
        log.debug("End TourService.deleteById");
    }

    @Transactional
    public TourDTO create(TourLocalDTO tourDTO, Localization localization) {
        log.debug("Start TourService.create");
        Tour tour = new Tour();
        tour.setIsActive(true);
        tour.setIsDeleted(false);
        tourRepo.saveAndFlush(tour);

        TourLocal local = new TourLocal(tourDTO.getTitle(),
                tourDTO.getIntroduction(),
                tourDTO.getDescription(),
                tourDTO.getAdditional(),
                localization);
        localRepo.saveAndFlush(local);
        tour.addLocal(local);

        tour.setSlug(slugUtils.getSlug(tour));
        tourRepo.saveAndFlush(tour);
        log.debug("End TourService.create");
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO addLocal(Long id, TourLocalDTO tourDTO, Localization localization) {
        log.debug("Start TourService.addLocal");
        Tour tour = findById(id);
        boolean isExists = tour.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            log.error(localization + " localization for tour with id=" + id + " already exists");
            throw new ObjectAlreadyExistsException(
                    localization + " localization for tour with id=" + id + " already exists");
        }

        TourLocal tourLocal =
                new TourLocal(tourDTO.getTitle(),
                        tourDTO.getIntroduction(),
                        tourDTO.getDescription(),
                        tourDTO.getAdditional(),
                        localization);
        tourLocal = localRepo.saveAndFlush(tourLocal);
        tour.addLocal(tourLocal);

        tour.setSlug(slugUtils.getSlug(tour));
        tourRepo.saveAndFlush(tour);
        log.debug("End TourService.addLocal");
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO updateLocal(Long id, TourLocalDTO tourDTO, Localization localization) {
        log.debug("Start TourService.updateLocal");
        Tour tour = findById(id);
        TourLocal cur_local = tour.getLocals().stream()
                .filter(local -> local.getLocalization() == localization)
                .findFirst()
                .orElseThrow(() -> {
                    log.error(localization + " localization for tour with id=" + id + " not found");
                    return new ObjectNotFoundException(
                            localization + " localization for tour with id=" + id + " not found");
                });

        cur_local.setTitle(tourDTO.getTitle());
        cur_local.setDescription(tourDTO.getDescription());
        localRepo.saveAndFlush(cur_local);

        tour.setSlug(slugUtils.getSlug(tour));
        tourRepo.saveAndFlush(tour);
        log.debug("End TourService.updateLocal");
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO updateLogicData(Long id, TourLogicDTO tourDTO, Localization localization) {
        log.debug("Start TourService.updateLogicData");
        Tour tour = findById(id);

        for (Hotel hotel : tour.getHotels().stream().toList()) {
            tour.removeHotel(hotel);
        }
        for (Long hotelId : tourDTO.getHotelIds()) {
            Hotel hotel = hotelService.findById(hotelId);
            tour.addHotel(hotel);
        }

        for (Tag tag : tour.getTags().stream().toList()) {
            tour.removeTag(tag);
        }
        for (Long tagId : tourDTO.getTagIds()) {
            Tag tag = tagService.findById(tagId);
            tour.addTag(tag);
        }

        Country oldCountry = tour.getCountry();
        if (oldCountry != null) {
            oldCountry.removeTour(tour);
        }
        Country newCountry = countryService.findById(tourDTO.getCountryId());
        newCountry.addTour(tour);

        Hospital newHospital = null;
        if (tourDTO.getHospitalId() != null) {
            newHospital = hospitalService.findById(tourDTO.getHospitalId());
        }
        tour.setHospital(newHospital);

        tour.setType(tourDTO.getType());
        tour.setIsActive(tourDTO.getIsActive());
        tour.setDuration(tour.getDuration());
        tour.setPrice(tourDTO.getPrice());

        tourRepo.saveAndFlush(tour);
        log.debug("End TourService.updateLogicData");
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO updateGeoData(Long id, TourGeoDTO tourDTO, Localization localization) {
        log.debug("Start TourService.updateGeoData");
        Tour tour = findById(id);
        tour.setRoute(tourDTO.getGeoData());
        log.debug("End TourService.updateGeoData");
        return new TourDTO(tour, localization);
    }

    public List<TourDTO> findByGeoData(Double minLongitude,
                                       Double maxLongitude,
                                       Double minLatitude,
                                       Double maxLatitude,
                                       Localization localization,
                                       Integer page, Integer size) {
        log.debug("Start TourService.findByGeoData");

        return modelsToDTOs(
                tourRepo.findToursByGeoData(
                        localization.name(),
                        minLongitude,
                        maxLongitude,
                        minLatitude,
                        maxLatitude, getPageable(page, size)).stream(), localization);
    }

    @Transactional
    public TourDTO createNewPeriod(Long id, TourPeriodDTO tourPeriodDTO, Localization localization) {
        log.debug("Start TourService.createNewPeriod");
        Tour tour = findById(id);

        TourPeriod period = new TourPeriod(
                tourPeriodDTO.getStartDate(),
                tourPeriodDTO.getEndDate());
        tourPeriodRepo.saveAndFlush(period);
        tour.addPeriod(period);
        tourRepo.saveAndFlush(tour);
        log.debug("End TourService.createNewPeriod");
        return new TourDTO(tour, localization);
    }

    private List<TourCommentDTO> commentsToDTOs(Stream<TourComment> comments) {
        log.debug("Start TourService.commentsToDTOs");
        List<TourCommentDTO> commentDTOs = comments
                .filter(comment -> !comment.getIsDeleted())
                .map(TourCommentDTO::new)
                .toList();
        if (commentDTOs.isEmpty()) {
            log.error("Comments not found");
            throw new ObjectNotFoundException("Comments not found");
        }
        log.debug("End TourService.commentsToDTOs");
        return commentDTOs;
    }

    private TourComment findCommentByUserIdAndTourId(Long authorId, Long hotelId) {
        log.debug("Start TourService.findCommentByUserIdAndTourId");
        TourComment comment = commentsRepo.findByUser_IdAndTour_Id(authorId, hotelId)
                .orElseThrow(() -> {
                    log.error("Tour comment with author id=" + authorId + " not found");
                    return new ObjectNotFoundException("Tour comment with author id=" + authorId + " not found");
                });
        if (comment.getIsDeleted()) {
            log.error("Tour comment with author id=" + authorId + " marked as deleted");
            throw new ObjectNotFoundException("Tour comment with author id=" + authorId + " marked as deleted");
        }
        log.debug("End TourService.findCommentByUserIdAndTourId");
        return comment;
    }

    public TourCommentDTO getCommentByUserAndTour(Long tourId) {
        log.debug("Start TourService.getCommentByUserAndTour");
        Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        log.debug("End TourService.getCommentByUserAndTour");
        return new TourCommentDTO(findCommentByUserIdAndTourId(userId, tourId));
    }

    public List<TourCommentDTO> getAllCommentsById(Long id) {
        log.debug("Start TourService.getAllCommentsById");
        return commentsToDTOs(commentsRepo.findAllByTour_IdOrderByCreatedAtDesc(id).stream());
    }

    public List<TourCommentDTO> getPaginatedCommentsById(Long id, int page, int size) {
        log.debug("Start TourService.getPaginatedCommentsById");
        Pageable commentsPage = PageRequest.of(page, size);
        log.debug("End TourService.getPaginatedCommentsById");
        return commentsToDTOs(commentsRepo.findAllByTour_IdOrderByCreatedAtDesc(id, commentsPage).stream());
    }


    @Transactional
    public TourCommentDTO addComment(Long id, CommentDTO commentDTO) {
        log.debug("Start TourService.addComment");
        Tour tour = findById(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (commentsRepo.existsByUser_IdAndTour_Id(user.getId(), id)) {
            log.error("Comment for this tour already exists");
            throw new ObjectAlreadyExistsException("Comment for this tour already exists");
        }

        TourComment comment = new TourComment(user, commentDTO.getGrade(), commentDTO.getComment());
        commentsRepo.saveAndFlush(comment);

        tour.addComment(comment);
        tourRepo.saveAndFlush(tour);
        log.debug("End TourService.addComment");
        return new TourCommentDTO(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        log.debug("Start TourService.deleteComment");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TourComment comment = findCommentByUserIdAndTourId(user.getId(), id);

        commentsRepo.delete(comment);
        log.debug("End TourService.deleteComment");
    }

    @Transactional
    @SneakyThrows
    public TourCommentDTO updateComment(Long id, CommentDTO commentDTO) {
        log.debug("Start TourService.updateComment");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TourComment comment = findCommentByUserIdAndTourId(user.getId(), id);

        comment.setComment(commentDTO.getComment());
        comment.setGrade(commentDTO.getGrade());

        commentsRepo.saveAndFlush(comment);
        log.debug("End TourService.updateComment");
        return new TourCommentDTO(comment);
    }
}
