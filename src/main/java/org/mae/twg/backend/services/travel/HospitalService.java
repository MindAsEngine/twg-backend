package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mae.twg.backend.dto.GradeData;
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
public class HospitalService implements TravelService<HospitalDTO, HospitalLocalDTO> {
    private final HospitalRepo hospitalRepo;
    private final HospitalLocalRepo localRepo;
    private final HospitalCommentsRepo commentsRepo;
    private final SlugUtils slugUtils;
    private final ImageService imageService;
    private final HospitalMediaRepo hospitalMediaRepo;

    public Hospital findById(Long id) {
        Hospital hospital = hospitalRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Hospital with id=" + id + " not found"));
        if (hospital.getIsDeleted()) {
            throw new ObjectNotFoundException("Hospital with id=" + id + " marked as deleted");
        }
        return hospital;
    }

    private Hospital findBySlug(String slug) {
        Hospital hospital = hospitalRepo.findBySlug(slug)
                .orElseThrow(() -> new ObjectNotFoundException("Hospital with slug=" + slug + " not found"));
        if (hospital.getIsDeleted()) {
            throw new ObjectNotFoundException("Hospital with slug=" + slug + " marked as deleted");
        }
        return hospital;
    }

    private HospitalDTO addGrade(HospitalDTO hospitalDTO) {
        GradeData gradeData = commentsRepo.averageGradeByHospitalId(hospitalDTO.getId());
        if (gradeData == null) {
            hospitalDTO.setCommentAmount(0L);
            return hospitalDTO;
        }
        hospitalDTO.setGrade(gradeData.getGrade());
        hospitalDTO.setCommentAmount(gradeData.getCount());
        return hospitalDTO;
    }

    private HospitalDTO addGrade(HospitalDTO hospitalDTO, GradeData gradeData) {
        if (gradeData == null) {
            hospitalDTO.setCommentAmount(0L);
            return hospitalDTO;
        }
        hospitalDTO.setGrade(gradeData.getGrade());
        hospitalDTO.setCommentAmount(gradeData.getCount());
        return hospitalDTO;
    }

    private List<HospitalDTO> modelsToDTOs(Stream<Hospital> hospitals, Localization localization) {
        Map<Long, GradeData> grades = commentsRepo.allAverageGrades()
                .stream().collect(Collectors.toMap(GradeData::getId, Function.identity()));
        List<HospitalDTO> hospitalDTOs = hospitals
                .filter(hospital -> !hospital.getIsDeleted())
                .filter(hospital -> hospital.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(hospital -> new HospitalDTO(hospital, localization))
                .map(hospitalDTO -> addGrade(hospitalDTO, grades.getOrDefault(hospitalDTO.getId(), null)))
                .toList();
        if (hospitalDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Hospitals with " + localization + " with localization not found");
        }
        return hospitalDTOs;
    }

    public List<HospitalDTO> getAll(Localization localization) {
        List<Hospital> hospitals = hospitalRepo.findAll();
        return modelsToDTOs(hospitals.stream(), localization);
    }

    @Transactional
    public HospitalDTO uploadImage(Long id, Localization local, MultipartFile image) throws IOException {
        String url = imageService.saveImage(ModelType.HOTEL, image);
        HospitalMedia hospitalMedia = new HospitalMedia(url);
        hospitalMediaRepo.saveAndFlush(hospitalMedia);
        Hospital hospital = findById(id);
        hospital.addHeader(hospitalMedia);
        hospitalRepo.saveAndFlush(hospital);
        return new HospitalDTO(hospital, local);
    }

    @Transactional
    public HospitalDTO uploadImages(Long id, Localization local, List<MultipartFile> images) throws IOException {
        List<String> urls = imageService.saveImages(ModelType.HOTEL, images);
        List<HospitalMedia> hospitalMedias = urls.stream().map(HospitalMedia::new).toList();
        Hospital hospital = findById(id);
        for (HospitalMedia hospitalMedia : hospitalMedias) {
            hospitalMediaRepo.saveAndFlush(hospitalMedia);
            hospital.addMedia(hospitalMedia);
        }
        hospitalRepo.saveAndFlush(hospital);
        return new HospitalDTO(hospital, local);
    }

    public HospitalDTO deleteImages(Long id, Localization local, List<String> images) {
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
        return new HospitalDTO(findById(id), local);
    }

    public List<HospitalDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable hospitalPage = PageRequest.of(page, size);
        Page<Hospital> hospitals = hospitalRepo.findAll(hospitalPage);
        return modelsToDTOs(hospitals.stream(), localization);
    }

    public HospitalDTO getById(Long id, Localization localization) {
        return addGrade(new HospitalDTO(findById(id), localization));
    }

    public HospitalDTO getBySlug(String slug, Localization localization) {
        return addGrade(new HospitalDTO(findBySlug(slug), localization));
    }

    @Transactional
    public void deleteById(Long id) {
        Hospital hospital = findById(id);
        hospital.setIsDeleted(true);
        hospitalRepo.save(hospital);
    }

    @Transactional
    public HospitalDTO create(HospitalLocalDTO hospitalDTO, Localization localization) {
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
        return new HospitalDTO(hospital, localization);
    }

    @Transactional
    public HospitalDTO addLocal(Long id, HospitalLocalDTO hospitalDTO, Localization localization) {
        Hospital hospital = findById(id);
        boolean isExists = hospital.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
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
        return new HospitalDTO(hospital, localization);
    }

    @Transactional
    public HospitalDTO updateLocal(Long id, HospitalLocalDTO hospitalDTO, Localization localization) {
        Hospital hospital = findById(id);
        HospitalLocal cur_local = hospital.getLocals().stream()
                .filter(local -> local.getLocalization() == localization)
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(
                        localization + " localization for hospital with id=" + id + " not found"));

        cur_local.setName(hospitalDTO.getName());
        cur_local.setCity(hospitalDTO.getCity());
        cur_local.setDescription(hospitalDTO.getDescription());
        cur_local.setAddress(hospitalDTO.getAddress());
        localRepo.saveAndFlush(cur_local);

        hospital.setSlug(slugUtils.getSlug(hospital));
        hospitalRepo.saveAndFlush(hospital);
        return new HospitalDTO(hospital, localization);
    }

    @Transactional
    public HospitalDTO updateGeoData(Long id, HospitalGeoDTO hospitalDTO, Localization localization) {
        Hospital hospital = findById(id);

        hospital.setLatitude(hospitalDTO.getLatitude());
        hospital.setLongitude(hospitalDTO.getLongitude());

        hospitalRepo.saveAndFlush(hospital);
        return new HospitalDTO(hospital, localization);
    }

    private List<HospitalCommentDTO> commentsToDTOs(Stream<HospitalComment> comments) {
        List<HospitalCommentDTO> commentDTOs = comments
                .filter(comment -> !comment.getIsDeleted())
                .map(HospitalCommentDTO::new)
                .toList();
        if (commentDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Comments not found");
        }
        return commentDTOs;
    }

    private HospitalComment findCommentByUserIdAndHospitalId(Long authorId, Long hospitalId) {
        HospitalComment comment = commentsRepo.findByUser_IdAndHospital_Id(authorId, hospitalId)
                .orElseThrow(() -> new ObjectNotFoundException("Hospital comment with author id=" + authorId + " not found"));
        if (comment.getIsDeleted()) {
            throw new ObjectNotFoundException("Hospital comment with author id=" + authorId + " marked as deleted");
        }
        return comment;
    }

    public List<HospitalCommentDTO> getAllCommentsById(Long id) {
        return commentsToDTOs(commentsRepo.findAllByHospital_IdOrderByCreatedAtDesc(id).stream());
    }

    public List<HospitalCommentDTO> getPaginatedCommentsById(Long id, int page, int size) {
        Pageable commentsPage = PageRequest.of(page, size);
        return commentsToDTOs(commentsRepo.findAllByHospital_IdOrderByCreatedAtDesc(id, commentsPage).stream());
    }

    @Transactional
    public HospitalCommentDTO addComment(Long id, CommentDTO commentDTO) {
        Hospital hospital = findById(id);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (commentsRepo.existsByUser_IdAndHospital_Id(user.getId(), id)) {
            throw new ObjectAlreadyExistsException("Comment for this hospital already exists");
        }

        HospitalComment comment = new HospitalComment(user, commentDTO.getGrade(), commentDTO.getComment());
        commentsRepo.saveAndFlush(comment);

        hospital.addComment(comment);
        hospitalRepo.saveAndFlush(hospital);

        return new HospitalCommentDTO(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HospitalComment comment = findCommentByUserIdAndHospitalId(user.getId(), id);

        commentsRepo.delete(comment);
    }

    @Transactional
    @SneakyThrows
    public HospitalCommentDTO updateComment(Long id, CommentDTO commentDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HospitalComment comment = findCommentByUserIdAndHospitalId(user.getId(), id);

        comment.setComment(commentDTO.getComment());
        comment.setGrade(commentDTO.getGrade());

        commentsRepo.saveAndFlush(comment);
        return new HospitalCommentDTO(comment);
    }
}
