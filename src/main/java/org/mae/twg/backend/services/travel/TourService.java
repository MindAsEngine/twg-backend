package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mae.twg.backend.dto.GradeData;
import org.mae.twg.backend.dto.travel.request.CommentDTO;
import org.mae.twg.backend.dto.travel.request.geo.TourGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.TourLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.TourLogicDTO;
import org.mae.twg.backend.dto.travel.request.logic.TourPeriodDTO;
import org.mae.twg.backend.dto.travel.response.TourDTO;
import org.mae.twg.backend.dto.travel.response.comments.TourCommentDTO;
import org.mae.twg.backend.exceptions.AccessDeniedException;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.travel.*;
import org.mae.twg.backend.models.travel.comments.TourComment;
import org.mae.twg.backend.models.travel.enums.Localization;
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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
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
    private final SightService sightService;
    private final TagService tagService;

    public Tour findById(Long id) {
        Tour tour = tourRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Tour with id=" + id + " not found"));
        if (tour.getIsDeleted()) {
            throw new ObjectNotFoundException("Tour with id=" + id + " marked as deleted");
        }
        return tour;
    }

    private Tour findBySlug(String slug) {
        Tour tour = tourRepo.findBySlug(slug)
                .orElseThrow(() -> new ObjectNotFoundException("Tour with slug=" + slug + " not found"));
        if (tour.getIsDeleted()) {
            throw new ObjectNotFoundException("Tour with slug=" + slug + " marked as deleted");
        }
        return tour;
    }

    private TourDTO addGrade(TourDTO tourDTO) {
        GradeData gradeData = commentsRepo.averageGradeByTourId(tourDTO.getId());
        if (gradeData == null) {
            tourDTO.setCommentAmount(0L);
            return tourDTO;
        }
        tourDTO.setGrade(gradeData.getGrade());
        tourDTO.setCommentAmount(gradeData.getCount());
        return tourDTO;
    }

    private TourDTO addGrade(TourDTO tourDTO, GradeData gradeData) {
        if (gradeData == null) {
            tourDTO.setCommentAmount(0L);
            return tourDTO;
        }
        tourDTO.setGrade(gradeData.getGrade());
        tourDTO.setCommentAmount(gradeData.getCount());
        return tourDTO;
    }

    private List<TourDTO> modelsToDTOs(Stream<Tour> tours, Localization localization) {
        Map<Long, GradeData> grades = commentsRepo.allAverageGrades()
                .stream().collect(Collectors.toMap(GradeData::getId, Function.identity()));
        List<TourDTO> tourDTOs = tours
                .filter(tour -> !tour.getIsDeleted())
                .filter(tour -> tour.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(tour -> new TourDTO(tour, localization))
                .map(tourDTO -> addGrade(tourDTO, grades.getOrDefault(tourDTO.getId(), null)))
                .toList();
        if (tourDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Tours with " + localization + " with localization not found");
        }
        return tourDTOs;
    }

    @Transactional
    public TourDTO uploadImage(Long id, Localization local, MultipartFile image) throws IOException {
        String url = imageService.saveImage(ModelType.TOUR, image);
        TourMedia tourMedia = new TourMedia(url);
        tourMediaRepo.saveAndFlush(tourMedia);
        Tour tour = findById(id);
        tour.setHeader(tourMedia);
        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, local);
    }

    @Transactional
    public TourDTO uploadImages(Long id, Localization local, List<MultipartFile> images) throws IOException {
        List<String> urls = imageService.saveImages(ModelType.TOUR, images);
        List<TourMedia> tourMedias = urls.stream().map(TourMedia::new).toList();
        Tour tour = findById(id);
        for (TourMedia tourMedia : tourMedias) {
            tourMediaRepo.saveAndFlush(tourMedia);
            tour.addMedia(tourMedia);
        }
        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, local);
    }

    public TourDTO deleteImages(Long id, Localization local, List<String> images) {
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
        return new TourDTO(findById(id), local);
    }

    public List<TourDTO> getAll(Localization localization) {
        List<Tour> tours = tourRepo.findAll();
        return modelsToDTOs(tours.stream(), localization);
    }

    public List<TourDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable toursPage = PageRequest.of(page, size);
        Page<Tour> tours = tourRepo.findAll(toursPage);
        return modelsToDTOs(tours.stream(), localization);
    }

    public TourDTO getById(Long id, Localization localization) {
        return addGrade(new TourDTO(findById(id), localization));
    }

    public TourDTO getBySlug(String slug, Localization localization) {
        return addGrade(new TourDTO(findBySlug(slug), localization));
    }

    @Transactional
    public void deleteById(Long id) {
        Tour tour = findById(id);
        tour.setIsDeleted(true);
        tourRepo.save(tour);
    }

    @Transactional
    public TourDTO create(TourLocalDTO tourDTO, Localization localization) {
        Tour tour = new Tour();
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
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO addLocal(Long id, TourLocalDTO tourDTO, Localization localization) {
        Tour tour = findById(id);
        boolean isExists = tour.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
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
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO updateLocal(Long id, TourLocalDTO tourDTO, Localization localization) {
        Tour tour = findById(id);
        TourLocal cur_local = tour.getLocals().stream()
                .filter(local -> local.getLocalization() == localization)
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(
                        localization + " localization for tour with id=" + id + " not found"));

        cur_local.setTitle(tourDTO.getTitle());
        cur_local.setDescription(tourDTO.getDescription());
        localRepo.saveAndFlush(cur_local);

        tour.setSlug(slugUtils.getSlug(tour));
        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO updateLogicData(Long id, TourLogicDTO tourDTO, Localization localization) {
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

        Sight newHospital = null;
        if (tourDTO.getHospitalId() != null) {
            newHospital = sightService.findById(tourDTO.getHospitalId());
        }
        tour.setHospital(newHospital);

        tour.setType(tourDTO.getType());
        tour.setIsActive(tourDTO.getIsActive());
        tour.setDuration(tour.getDuration());
        tour.setPrice(tourDTO.getPrice());

        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO updateGeoData(Long id, TourGeoDTO tourDTO, Localization localization) {
        Tour tour = findById(id);
        tour.setRoute(tourDTO.getGeoData());
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO createNewPeriod(Long id, TourPeriodDTO tourPeriodDTO, Localization localization) {
        Tour tour = findById(id);

        TourPeriod period = new TourPeriod(
                tourPeriodDTO.getStartDate(),
                tourPeriodDTO.getEndDate());
        tourPeriodRepo.saveAndFlush(period);
        tour.addPeriod(period);
        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, localization);
    }

    private List<TourCommentDTO> commentsToDTOs(Stream<TourComment> comments) {
        List<TourCommentDTO> commentDTOs = comments
                .filter(comment -> !comment.getIsDeleted())
                .map(TourCommentDTO::new)
                .toList();
        if (commentDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Comments not found");
        }
        return commentDTOs;
    }

    private TourComment findCommentById(Long id) {
        TourComment comment = commentsRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Tour comment with id=" + id + " not found"));
        if (comment.getIsDeleted()) {
            throw new ObjectNotFoundException("Tour comment with id=" + id + " marked as deleted");
        }
        return comment;
    }

    public List<TourCommentDTO> getAllCommentsById(Long id) {
        return commentsToDTOs(commentsRepo.findAllByTour_IdOrderByCreatedAtDesc(id).stream());
    }

    public List<TourCommentDTO> getPaginatedCommentsById(Long id, int page, int size) {
        Pageable commentsPage = PageRequest.of(page, size);
        return commentsToDTOs(commentsRepo.findAllByTour_IdOrderByCreatedAtDesc(id, commentsPage).stream());
    }

    @Transactional
    public TourCommentDTO addComment(Long id, CommentDTO commentDTO) {
        Tour hotel = findById(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        TourComment comment = new TourComment(user, commentDTO.getGrade(), commentDTO.getComment());
        commentsRepo.saveAndFlush(comment);

        hotel.addComment(comment);
        tourRepo.saveAndFlush(hotel);

        return new TourCommentDTO(comment);
    }

    @SneakyThrows
    private void verifyAccess(TourComment comment) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(user.getId(), comment.getUser().getId())) {
            throw new AccessDeniedException("You are not the owner of this comment");
        }
    }

    @Transactional
    public void deleteByCommentId(Long commentId) {
        TourComment comment = findCommentById(commentId);
        verifyAccess(comment);

        comment.setIsDeleted(true);
        commentsRepo.save(comment);
    }

    @Transactional
    @SneakyThrows
    public TourCommentDTO updateByCommentId(Long commentId, CommentDTO commentDTO) {
        TourComment comment = findCommentById(commentId);
        verifyAccess(comment);

        comment.setComment(commentDTO.getComment());
        comment.setGrade(commentDTO.getGrade());

        commentsRepo.saveAndFlush(comment);
        return new TourCommentDTO(comment);
    }
}
