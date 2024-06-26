package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mae.twg.backend.dto.travel.request.CommentDTO;
import org.mae.twg.backend.dto.travel.request.geo.HotelGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.HotelLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.HotelLogicDTO;
import org.mae.twg.backend.dto.travel.response.HotelDTO;
import org.mae.twg.backend.dto.travel.response.comments.HotelCommentDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.travel.Hotel;
import org.mae.twg.backend.models.travel.Property;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.travel.comments.HotelComment;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.HotelLocal;
import org.mae.twg.backend.models.travel.media.HotelMedia;
import org.mae.twg.backend.repositories.travel.HotelRepo;
import org.mae.twg.backend.repositories.travel.PropertyRepo;
import org.mae.twg.backend.repositories.travel.SightRepo;
import org.mae.twg.backend.dto.GradeData;
import org.mae.twg.backend.repositories.travel.comments.HotelCommentsRepo;
import org.mae.twg.backend.repositories.travel.images.HotelMediaRepo;
import org.mae.twg.backend.repositories.travel.localization.HotelLocalRepo;
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
public class HotelService implements TravelService<HotelDTO, HotelLocalDTO> {
    private final HotelRepo hotelRepo;
    private final HotelLocalRepo localRepo;
    private final HotelCommentsRepo commentsRepo;
    private final PropertyRepo propertyRepo;
    private final SightRepo sightRepo;
    private final SlugUtils slugUtils;
    private final ImageService imageService;
    private final HotelMediaRepo hotelMediaRepo;

    public Hotel findById(Long id) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Hotel with id=" + id + " not found"));
        if (hotel.getIsDeleted()) {
            throw new ObjectNotFoundException("Hotel with id=" + id + " marked as deleted");
        }
        return hotel;
    }

    private Hotel findBySlug(String slug) {
        Hotel hotel = hotelRepo.findBySlug(slug)
                .orElseThrow(() -> new ObjectNotFoundException("Hotel with slug=" + slug + " not found"));
        if (hotel.getIsDeleted()) {
            throw new ObjectNotFoundException("Hotel with slug=" + slug + " marked as deleted");
        }
        return hotel;
    }

    private HotelDTO addGrade(HotelDTO hotelDTO) {
        GradeData gradeData = commentsRepo.averageGradeByHotelId(hotelDTO.getId());
        if (gradeData == null) {
            hotelDTO.setCommentAmount(0L);
            return hotelDTO;
        }
        hotelDTO.setGrade(gradeData.getGrade());
        hotelDTO.setCommentAmount(gradeData.getCount());
        return hotelDTO;
    }

    private HotelDTO addGrade(HotelDTO hotelDTO, GradeData gradeData) {
        if (gradeData == null) {
            hotelDTO.setCommentAmount(0L);
            return hotelDTO;
        }
        hotelDTO.setGrade(gradeData.getGrade());
        hotelDTO.setCommentAmount(gradeData.getCount());
        return hotelDTO;
    }

    private List<HotelDTO> modelsToDTOs(Stream<Hotel> hotels, Localization localization) {
        Map<Long, GradeData> grades = commentsRepo.allAverageGrades()
                .stream().collect(Collectors.toMap(GradeData::getId, Function.identity()));
        List<HotelDTO> hotelDTOs = hotels
                .filter(hotel -> !hotel.getIsDeleted())
                .filter(hotel -> hotel.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(hotel -> new HotelDTO(hotel, localization))
                .map(hotelDTO -> addGrade(hotelDTO, grades.getOrDefault(hotelDTO.getId(), null)))
                .toList();
        if (hotelDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Hotels with " + localization + " with localization not found");
        }
        return hotelDTOs;
    }

    public List<HotelDTO> getAll(Localization localization) {
        List<Hotel> hotels = hotelRepo.findAll();
        return modelsToDTOs(hotels.stream(), localization);
    }

    @Transactional
    public HotelDTO uploadImage(Long id, Localization local, MultipartFile image) throws IOException {
        String url = imageService.saveImage(ModelType.HOTEL, image);
        HotelMedia hotelMedia = new HotelMedia(url);
        hotelMediaRepo.saveAndFlush(hotelMedia);
        Hotel hotel = findById(id);
        hotel.addHeader(hotelMedia);
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, local);
    }

    @Transactional
    public HotelDTO uploadImages(Long id, Localization local, List<MultipartFile> images) throws IOException {
        List<String> urls = imageService.saveImages(ModelType.HOTEL, images);
        List<HotelMedia> hotelMedias = urls.stream().map(HotelMedia::new).toList();
        Hotel hotel = findById(id);
        for (HotelMedia hotelMedia : hotelMedias) {
            hotelMediaRepo.saveAndFlush(hotelMedia);
            hotel.addMedia(hotelMedia);
        }
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, local);
    }

    public HotelDTO deleteImages(Long id, Localization local, List<String> images) {
        imageService.deleteImages(images);
        List<HotelMedia> hotelMedias = hotelMediaRepo.findByHotel_id(id);
        for (HotelMedia hotelMedia : hotelMedias) {
            Hotel hotel = findById(id);
            if (hotel.getHeader() == hotelMedia) {
                hotel.removeHeader(hotelMedia);
                hotelRepo.saveAndFlush(hotel);
            }
            if (images.contains(hotelMedia.getMediaPath())) {
                hotelMediaRepo.delete(hotelMedia);
            }
        }
        return new HotelDTO(findById(id), local);
    }

    public List<HotelDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable hotelPage = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepo.findAll(hotelPage);
        return modelsToDTOs(hotels.stream(), localization);
    }

    public HotelDTO getById(Long id, Localization localization) {
        return addGrade(new HotelDTO(findById(id), localization));
    }

    public HotelDTO getBySlug(String slug, Localization localization) {
        return addGrade(new HotelDTO(findBySlug(slug), localization));
    }

    @Transactional
    public void deleteById(Long id) {
        Hotel hotel = findById(id);
        hotel.setIsDeleted(true);
        hotelRepo.save(hotel);
    }

    @Transactional
    public HotelDTO create(HotelLocalDTO hotelDTO, Localization localization) {
        Hotel hotel = new Hotel();
        hotelRepo.saveAndFlush(hotel);

        HotelLocal local = new HotelLocal(hotelDTO.getName(),
                hotelDTO.getCity(),
                hotelDTO.getDescription(),
                hotelDTO.getDescription(),
                hotelDTO.getAddress(),
                localization);
        localRepo.saveAndFlush(local);
        hotel.addLocal(local);

        hotel.setSlug(slugUtils.getSlug(hotel));
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO addLocal(Long id, HotelLocalDTO hotelDTO, Localization localization) {
        Hotel hotel = findById(id);
        boolean isExists = hotel.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for hotel with id=" + id + " already exists");
        }

        HotelLocal hotelLocal =
                new HotelLocal(hotelDTO.getName(),
                        hotelDTO.getCity(),
                        hotelDTO.getDescription(),
                        hotelDTO.getDescription(),
                        hotelDTO.getAddress(),
                        localization);
        hotelLocal = localRepo.saveAndFlush(hotelLocal);
        hotel.addLocal(hotelLocal);

        hotel.setSlug(slugUtils.getSlug(hotel));
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO updateLocal(Long id, HotelLocalDTO hotelDTO, Localization localization) {
        Hotel hotel = findById(id);
        HotelLocal cur_local = hotel.getLocals().stream()
                .filter(local -> local.getLocalization() == localization)
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(
                        localization + " localization for hotel with id=" + id + " not found"));

        cur_local.setName(hotelDTO.getName());
        cur_local.setCity(hotelDTO.getCity());
        cur_local.setDescription(hotelDTO.getDescription());
        cur_local.setAddress(hotelDTO.getAddress());
        localRepo.saveAndFlush(cur_local);

        hotel.setSlug(slugUtils.getSlug(hotel));
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO updateLogicData(Long id, HotelLogicDTO hotelDTO, Localization localization) {
        Hotel hotel = findById(id);
        hotel.setStars(hotelDTO.getStars());

        for (Property property : hotel.getProperties().stream().toList()) {
            hotel.removeProperty(property);
        }
        for (Long propertyId : hotelDTO.getPropertyIds()) {
            Property property = propertyRepo.findById(propertyId)
                    .orElseThrow(() -> new ObjectNotFoundException("Property with id=" + propertyId + " not found"));
            hotel.addProperty(property);
        }

        for (Sight sight : hotel.getSights().stream().toList()) {
            hotel.removeSight(sight);
        }
        for (Long sightId : hotelDTO.getSightIds()) {
            Sight sight = sightRepo.findById(sightId)
                    .orElseThrow(() -> new ObjectNotFoundException("Sight with id=" + sightId + " not found"));
            hotel.addSight(sight);
        }

        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO updateGeoData(Long id, HotelGeoDTO hotelDTO, Localization localization) {
        Hotel hotel = findById(id);

        hotel.setLatitude(hotelDTO.getLatitude());
        hotel.setLongitude(hotelDTO.getLongitude());

        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    private List<HotelCommentDTO> commentsToDTOs(Stream<HotelComment> comments) {
        List<HotelCommentDTO> commentDTOs = comments
                .filter(comment -> !comment.getIsDeleted())
                .map(HotelCommentDTO::new)
                .toList();
        if (commentDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Comments not found");
        }
        return commentDTOs;
    }

    private HotelComment findCommentByUserIdAndHotelId(Long authorId, Long hotelId) {
        HotelComment comment = commentsRepo.findByUser_IdAndHotel_Id(authorId, hotelId)
                .orElseThrow(() -> new ObjectNotFoundException("Hotel comment with author id=" + authorId + " not found"));
        if (comment.getIsDeleted()) {
            throw new ObjectNotFoundException("Hotel comment with author id=" + authorId + " marked as deleted");
        }
        return comment;
    }

    public List<HotelCommentDTO> getAllCommentsById(Long id) {
        return commentsToDTOs(commentsRepo.findAllByHotel_IdOrderByCreatedAtDesc(id).stream());
    }

    public List<HotelCommentDTO> getPaginatedCommentsById(Long id, int page, int size) {
        Pageable commentsPage = PageRequest.of(page, size);
        return commentsToDTOs(commentsRepo.findAllByHotel_IdOrderByCreatedAtDesc(id, commentsPage).stream());
    }

    @Transactional
    public HotelCommentDTO addComment(Long id, CommentDTO commentDTO) {
        Hotel hotel = findById(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (commentsRepo.existsByUser_IdAndHotel_Id(user.getId(), id)) {
            throw new ObjectAlreadyExistsException("Comment for this hotel already exists");
        }

        HotelComment comment = new HotelComment(user, commentDTO.getGrade(), commentDTO.getComment());
        commentsRepo.saveAndFlush(comment);

        hotel.addComment(comment);
        hotelRepo.saveAndFlush(hotel);

        return new HotelCommentDTO(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HotelComment comment = findCommentByUserIdAndHotelId(user.getId(), id);

        commentsRepo.delete(comment);
    }

    @Transactional
    @SneakyThrows
    public HotelCommentDTO updateComment(Long id, CommentDTO commentDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HotelComment comment = findCommentByUserIdAndHotelId(user.getId(), id);

        comment.setComment(commentDTO.getComment());
        comment.setGrade(commentDTO.getGrade());

        commentsRepo.saveAndFlush(comment);
        return new HotelCommentDTO(comment);
    }
}
