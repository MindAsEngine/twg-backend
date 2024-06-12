package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.GradeData;
import org.mae.twg.backend.dto.PageDTO;
import org.mae.twg.backend.dto.travel.request.CommentDTO;
import org.mae.twg.backend.dto.travel.request.geo.HospitalGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.HospitalLocalDTO;
import org.mae.twg.backend.dto.travel.response.HospitalDTO;
import org.mae.twg.backend.dto.travel.response.comments.HospitalCommentDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.travel.Hospital;
import org.mae.twg.backend.models.travel.comments.HospitalComment;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.HospitalLocal;
import org.mae.twg.backend.models.travel.media.HospitalMedia;
import org.mae.twg.backend.repositories.travel.HospitalRepo;
import org.mae.twg.backend.repositories.travel.comments.HospitalCommentsRepo;
import org.mae.twg.backend.repositories.travel.images.HospitalMediaRepo;
import org.mae.twg.backend.repositories.travel.localization.HospitalLocalRepo;
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
public class HospitalService implements TravelService<HospitalDTO, HospitalLocalDTO> {
    private final HospitalRepo hospitalRepo;
    private final HospitalLocalRepo localRepo;
    private final HospitalCommentsRepo commentsRepo;
    private final SlugUtils slugUtils;
    private final ImageService imageService;
    private final HospitalMediaRepo hospitalMediaRepo;

    public Hospital findById(Long id) {
        log.debug("Start HospitalService.findById");
        Hospital hospital = hospitalRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Hospital with id=" + id + " not found");
                    return new ObjectNotFoundException("Hospital with id=" + id + " not found");
                });
        if (hospital.getIsDeleted()) {
            log.error("Hospital with id=" + id + " marked as deleted");
            throw new ObjectNotFoundException("Hospital with id=" + id + " marked as deleted");
        }
        log.debug("End HospitalService.findById");
        return hospital;
    }

    private Hospital findBySlug(String slug) {
        log.debug("Start HospitalService.findBySlug");
        Hospital hospital = hospitalRepo.findBySlug(slug)
                .orElseThrow(() -> {
                    log.error("Hospital with slug=" + slug + " not found");
                    return new ObjectNotFoundException("Hospital with slug=" + slug + " not found");
                });
        if (hospital.getIsDeleted()) {
            log.error("Hospital with slug=" + slug + " marked as deleted");
            throw new ObjectNotFoundException("Hospital with slug=" + slug + " marked as deleted");
        }
        log.debug("End HospitalService.findBySlug");
        return hospital;
    }

    public Pageable getPageable(Integer page, Integer size) {
        log.debug("Start HospitalService.getPageable");
        if (page != null && size != null) {
            log.debug("End HospitalService.getPageable");
            return PageRequest.of(page, size);
        }
        log.debug("End HospitalService.getPageable");
        return null;
    }

    private HospitalDTO addGrade(HospitalDTO hospitalDTO) {
        log.debug("Start HospitalService.addGrade");
        GradeData gradeData = commentsRepo.averageGradeByHospitalId(hospitalDTO.getId());
        if (gradeData == null) {
            hospitalDTO.setCommentAmount(0L);
            return hospitalDTO;
        }
        hospitalDTO.setGrade(gradeData.getGrade());
        hospitalDTO.setCommentAmount(gradeData.getCount());
        log.debug("End HospitalService.addGrade");
        return hospitalDTO;
    }

    private HospitalDTO addGrade(HospitalDTO hospitalDTO, GradeData gradeData) {
        log.debug("Start HospitalService.addGrade");
        if (gradeData == null) {
            hospitalDTO.setCommentAmount(0L);
            return hospitalDTO;
        }
        hospitalDTO.setGrade(gradeData.getGrade());
        hospitalDTO.setCommentAmount(gradeData.getCount());
        log.debug("End HospitalService.addGrade");
        return hospitalDTO;
    }

    private PageDTO<HospitalDTO> modelsToDTOs(PageDTO<Hospital> hospitals, Localization localization) {
        log.debug("Start HospitalService.modelsToDTOs");
        if (hospitals.isEmpty()) {
            log.error("Hospitals with " + localization + " with localization not found");
            throw new ObjectNotFoundException("Hospitals with " + localization + " with localization not found");
        }
        Map<Long, GradeData> grades = commentsRepo.allAverageGrades()
                .stream().collect(Collectors.toMap(GradeData::getId, Function.identity()));
        PageDTO<HospitalDTO> hospitalDTOs = hospitals
                .apply(hospital -> new HospitalDTO(hospital, localization))
                .apply(hospitalDTO -> addGrade(hospitalDTO,
                        grades.getOrDefault(hospitalDTO.getId(), null)));
        log.debug("End HospitalService.modelsToDTOs");
        return hospitalDTOs;
    }

//    public List<HospitalDTO> getAll(Localization localization) {
//        log.debug("Start HospitalService.getAll");
//        List<Hospital> hospitals = hospitalRepo.findAll(Sort.by("slug"));
//        log.debug("End HospitalService.getAll");
//        return modelsToDTOs(hospitals.stream(), localization);
//    }

    @Transactional
    public HospitalDTO uploadImage(Long id, Localization local, MultipartFile image) throws IOException {
        log.debug("Start HospitalService.uploadImage");
        String url = imageService.saveImage(ModelType.HOSPITAL, image);
        HospitalMedia hospitalMedia = new HospitalMedia(url);
        hospitalMediaRepo.saveAndFlush(hospitalMedia);
        Hospital hospital = findById(id);
        hospital.addHeader(hospitalMedia);
        hospitalRepo.saveAndFlush(hospital);
        log.debug("End HospitalService.uploadImage");
        return new HospitalDTO(hospital, local);
    }

    @Transactional
    public HospitalDTO uploadImages(Long id, Localization local, List<MultipartFile> images) throws IOException {
        log.debug("Start HospitalService.uploadImages");
        List<String> urls = imageService.saveImages(ModelType.HOSPITAL, images);
        List<HospitalMedia> hospitalMedias = urls.stream().map(HospitalMedia::new).toList();
        Hospital hospital = findById(id);
        for (HospitalMedia hospitalMedia : hospitalMedias) {
            hospitalMediaRepo.saveAndFlush(hospitalMedia);
            hospital.addMedia(hospitalMedia);
        }
        hospitalRepo.saveAndFlush(hospital);
        log.debug("End HospitalService.uploadImages");
        return new HospitalDTO(hospital, local);
    }

    public HospitalDTO deleteImages(Long id, Localization local, List<String> images) {
        log.debug("Start HospitalService.deleteImages");
        imageService.deleteImages(images);
        List<HospitalMedia> hospitalMedias = hospitalMediaRepo.findByHospital_id(id);
        for (HospitalMedia hospitalMedia : hospitalMedias) {
            Hospital hospital = findById(id);
            if (hospital.getHeader() == hospitalMedia) {
                hospital.removeHeader(hospitalMedia);
                hospitalRepo.saveAndFlush(hospital);
            }
            if (images.contains(hospitalMedia.getMediaPath())) {
                hospitalMediaRepo.delete(hospitalMedia);
            }
        }
        log.debug("End HospitalService.deleteImages");
        return new HospitalDTO(findById(id), local);
    }

    public PageDTO<HospitalDTO> getAllPaged(Localization localization, Integer page, Integer size) {
        log.debug("Start HospitalService.getAllPaged");
        Pageable hospitalPage = null;
        if (page != null && size != null) {
            hospitalPage = PageRequest.of(page, size);
        }
        Page<Hospital> hospitals = hospitalRepo.findAllByIsDeletedFalse(hospitalPage);
        log.debug("End HospitalService.getAllPaged");
        return modelsToDTOs(new PageDTO<>(hospitals), localization);
    }

    public HospitalDTO getById(Long id, Localization localization) {
        log.debug("Start HospitalService.getById");
        return addGrade(new HospitalDTO(findById(id), localization));
    }

    public HospitalDTO getBySlug(String slug, Localization localization) {
        log.debug("Start HospitalService.getBySlug");
        return addGrade(new HospitalDTO(findBySlug(slug), localization));
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Start HospitalService.deleteById");
        Hospital hospital = findById(id);
        hospital.setIsDeleted(true);
        hospitalRepo.save(hospital);
        log.debug("End HospitalService.deleteById");
    }

    @Transactional
    public HospitalDTO create(HospitalLocalDTO hospitalDTO, Localization localization) {
        log.debug("Start HospitalService.create");
        Hospital hospital = new Hospital();
        hospitalRepo.saveAndFlush(hospital);

        HospitalLocal local = new HospitalLocal(hospitalDTO.getName(),
                hospitalDTO.getCity(),
                hospitalDTO.getDescription(),
                hospitalDTO.getDescription(),
                hospitalDTO.getAddress(),
                localization);
        localRepo.saveAndFlush(local);
        hospital.addLocal(local);

        hospital.setSlug(slugUtils.getSlug(hospital));
        hospitalRepo.saveAndFlush(hospital);
        log.debug("End HospitalService.create");
        return new HospitalDTO(hospital, localization);
    }

    @Transactional
    public HospitalDTO addLocal(Long id, HospitalLocalDTO hospitalDTO, Localization localization) {
        log.debug("Start HospitalService.addLocal");
        Hospital hospital = findById(id);
        boolean isExists = hospital.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            log.error(localization + " localization for hospital with id=" + id + " already exists");
            throw new ObjectAlreadyExistsException(
                    localization + " localization for hospital with id=" + id + " already exists");
        }

        HospitalLocal hospitalLocal =
                new HospitalLocal(hospitalDTO.getName(),
                        hospitalDTO.getCity(),
                        hospitalDTO.getDescription(),
                        hospitalDTO.getDescription(),
                        hospitalDTO.getAddress(),
                        localization);
        hospitalLocal = localRepo.saveAndFlush(hospitalLocal);
        hospital.addLocal(hospitalLocal);

        hospital.setSlug(slugUtils.getSlug(hospital));
        hospitalRepo.saveAndFlush(hospital);
        log.debug("End HospitalService.addLocal");
        return new HospitalDTO(hospital, localization);
    }

    @Transactional
    public HospitalDTO updateLocal(Long id, HospitalLocalDTO hospitalDTO, Localization localization) {
        log.debug("Start HospitalService.updateLocal");
        Hospital hospital = findById(id);
        HospitalLocal cur_local = hospital.getLocals().stream()
                .filter(local -> local.getLocalization() == localization)
                .findFirst()
                .orElseThrow(() -> {
                    log.error(localization + " localization for hospital with id=" + id + " not found");
                    return new ObjectNotFoundException(
                            localization + " localization for hospital with id=" + id + " not found");
                });

        cur_local.setName(hospitalDTO.getName());
        cur_local.setCity(hospitalDTO.getCity());
        cur_local.setDescription(hospitalDTO.getDescription());
        cur_local.setAddress(hospitalDTO.getAddress());
        localRepo.saveAndFlush(cur_local);

        hospital.setSlug(slugUtils.getSlug(hospital));
        hospitalRepo.saveAndFlush(hospital);
        log.debug("End HospitalService.updateLocal");
        return new HospitalDTO(hospital, localization);
    }

    public PageDTO<HospitalDTO> findByGeoData(Double minLongitude,
                                        Double maxLongitude,
                                        Double minLatitude,
                                        Double maxLatitude,
                                        Localization localization,
                                        Integer page, Integer size) {
        log.debug("Start HotelService.findByGeoData");

        return modelsToDTOs(new PageDTO<>(
                hospitalRepo.findByGeoData(
                        minLongitude,
                        maxLongitude,
                        minLatitude,
                        maxLatitude, getPageable(page, size))), localization);
    }

    @Transactional
    public HospitalDTO updateGeoData(Long id, HospitalGeoDTO hospitalDTO, Localization localization) {
        log.debug("Start HospitalService.updateGeoData");
        Hospital hospital = findById(id);

        hospital.setLatitude(hospitalDTO.getLatitude());
        hospital.setLongitude(hospitalDTO.getLongitude());

        hospitalRepo.saveAndFlush(hospital);
        log.debug("End HospitalService.updateGeoData");
        return new HospitalDTO(hospital, localization);
    }

    private List<HospitalCommentDTO> commentsToDTOs(Stream<HospitalComment> comments) {
        log.debug("Start HospitalService.commentsToDTOs");
        List<HospitalCommentDTO> commentDTOs = comments
                .filter(comment -> !comment.getIsDeleted())
                .map(HospitalCommentDTO::new)
                .toList();
        if (commentDTOs.isEmpty()) {
            log.error("Comments not found");
            throw new ObjectNotFoundException("Comments not found");
        }
        log.debug("End HospitalService.commentsToDTOs");
        return commentDTOs;
    }

    private HospitalComment findCommentByUserIdAndHospitalId(Long authorId, Long hospitalId) {
        log.debug("Start HospitalService.findCommentByUserIdAndHospitalId");
        HospitalComment comment = commentsRepo.findByUser_IdAndHospital_Id(authorId, hospitalId)
                .orElseThrow(() -> {
                    log.error("Hospital comment with author id=" + authorId + " not found");
                    return new ObjectNotFoundException("Hospital comment with author id=" + authorId + " not found");
                });
        if (comment.getIsDeleted()) {
            log.error("Hospital comment with author id=" + authorId + " marked as deleted");
            throw new ObjectNotFoundException("Hospital comment with author id=" + authorId + " marked as deleted");
        }
        log.debug("End HospitalService.findCommentByUserIdAndHospitalId");
        return comment;
    }

    public List<HospitalCommentDTO> getAllCommentsById(Long id) {
        log.debug("Start HospitalService.getAllCommentsById");
        return commentsToDTOs(commentsRepo.findAllByHospital_IdOrderByCreatedAtDesc(id).stream());
    }

    public List<HospitalCommentDTO> getPaginatedCommentsById(Long id, int page, int size) {
        log.debug("Start HospitalService.getPaginatedCommentsById");
        Pageable commentsPage = PageRequest.of(page, size);
        log.debug("End HospitalService.getPaginatedCommentsById");
        return commentsToDTOs(commentsRepo.findAllByHospital_IdOrderByCreatedAtDesc(id, commentsPage).stream());
    }

    @Transactional
    public HospitalCommentDTO addComment(Long id, CommentDTO commentDTO) {
        log.debug("Start HospitalService.addComment");
        Hospital hospital = findById(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (commentsRepo.existsByUser_IdAndHospital_Id(user.getId(), id)) {
            log.error("Comment for this hospital already exists");
            throw new ObjectAlreadyExistsException("Comment for this hospital already exists");
        }

        HospitalComment comment = new HospitalComment(user, commentDTO.getGrade(), commentDTO.getComment());
        commentsRepo.saveAndFlush(comment);

        hospital.addComment(comment);
        hospitalRepo.saveAndFlush(hospital);
        log.debug("End HospitalService.addComment");
        return new HospitalCommentDTO(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        log.debug("Start HospitalService.deleteComment");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HospitalComment comment = findCommentByUserIdAndHospitalId(user.getId(), id);

        commentsRepo.delete(comment);
        log.debug("End HospitalService.deleteComment");
    }

    @Transactional
    @SneakyThrows
    public HospitalCommentDTO updateComment(Long id, CommentDTO commentDTO) {
        log.debug("Start HospitalService.updateComment");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HospitalComment comment = findCommentByUserIdAndHospitalId(user.getId(), id);

        comment.setComment(commentDTO.getComment());
        comment.setGrade(commentDTO.getGrade());

        commentsRepo.saveAndFlush(comment);
        log.debug("End HospitalService.updateComment");
        return new HospitalCommentDTO(comment);
    }
}
