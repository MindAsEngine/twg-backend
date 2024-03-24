package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.SightDTO;
import org.mae.twg.backend.dto.travel.request.geo.SightGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.SightLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.SightLogicDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.travel.SightType;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.SightLocal;
import org.mae.twg.backend.models.travel.media.SightMedia;
import org.mae.twg.backend.repositories.travel.SightRepo;
import org.mae.twg.backend.repositories.travel.images.SightMediaRepo;
import org.mae.twg.backend.repositories.travel.localization.SightLocalRepo;
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


@Service
@RequiredArgsConstructor
public class SightService implements TravelService<SightLocalDTO, SightLocalDTO> {
    private final SightRepo sightRepo;
    private final SightLocalRepo localRepo;
    private final SlugUtils slugUtils;
    private final ImageService imageService;
    private final SightMediaRepo sightMediaRepo;
    private final SightTypeService sightTypeService;

    private Sight findById(Long id) {
        Sight sight = sightRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Sight with id=" + id + " not found"));
        if (sight.getIsDeleted()) {
            throw new ObjectNotFoundException("Sight with id=" + id + " marked as deleted");
        }
        return sight;
    }

    private Sight findBySlug(String slug) {
        Sight sight = sightRepo.findBySlug(slug)
                .orElseThrow(() -> new ObjectNotFoundException("Sight with slug=" + slug + " not found"));
        if (sight.getIsDeleted()) {
            throw new ObjectNotFoundException("Sight with slug=" + slug + " marked as deleted");
        }
        return sight;
    }

    private List<SightDTO> modelsToDTOs(Stream<Sight> sights, Localization localization) {
        List<SightDTO> sightDTOs = sights
                .filter(sight -> !sight.getIsDeleted())
                .filter(sight -> sight.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(sight -> new SightDTO(sight, localization))
                .toList();
        if (sightDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Sights with " + localization + " not found");
        }
        return sightDTOs;
    }

    @Transactional
    public SightDTO uploadImages(Long id, Localization local, List<MultipartFile> images) throws IOException {
        List<String> urls = imageService.saveImages(ModelType.SIGHT, images);
        List<SightMedia> sightMedias = urls.stream().map(SightMedia::new).toList();
        Sight sight = findById(id);
        for (SightMedia sightMedia : sightMedias) {
            sight.addMedia(sightMedia);
        }
        sightRepo.saveAndFlush(sight);
        return new SightDTO(sight, local);
    }

    public SightDTO deleteImages(Long id, Localization local, List<String> images) {
        imageService.deleteImages(images);
        List<SightMedia> sightMedias = sightMediaRepo.findBySight_id(id);
        for (SightMedia sightMedia : sightMedias) {
            if (images.contains(sightMedia.getMediaPath())) {
                sightMediaRepo.delete(sightMedia);
            }
        }
        return new SightDTO(findById(id), local);
    }

    public List<SightDTO> getAll(Localization localization) {
        List<Sight> sights = sightRepo.findAll();
        return modelsToDTOs(sights.stream(), localization);
    }

    public List<SightDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable sightsPage = PageRequest.of(page, size);
        Page<Sight> sights = sightRepo.findAll(sightsPage);
        return modelsToDTOs(sights.stream(), localization);
    }

    public SightDTO getById(Long id, Localization local) {
        return new SightDTO(findById(id), local);
    }

    public SightDTO getBySlug(String slug, Localization local) {
        return new SightDTO(findBySlug(slug), local);
    }

    @Transactional
    public void deleteById(Long id) {
        Sight sight = findById(id);
        sight.setIsDeleted(true);
        sightRepo.save(sight);
    }

    @Transactional
    public SightDTO create(SightLocalDTO sightDTO, Localization local) {
        Sight sight = new Sight();
        sightRepo.saveAndFlush(sight);
        SightLocal sightLocal =
                new SightLocal(sightDTO.getName(),
                        sightDTO.getIntroduction(),
                        sightDTO.getDescription(),
                        sightDTO.getAddress(),
                        sight, local);
        sightLocal = localRepo.saveAndFlush(sightLocal);
        sight.addLocal(sightLocal);

        sight.setSlug(slugUtils.getSlug(sight));
        sightRepo.saveAndFlush(sight);
        return new SightDTO(sight, local);
    }

    @Transactional
    public SightDTO addLocal(Long id, SightLocalDTO sightDTO, Localization localization) {
        Sight sight = findById(id);
        boolean isExists = sight.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for sight with id=" + id + " already exists");
        }

        SightLocal sightLocal =
                new SightLocal(sightDTO.getName(),
                        sightDTO.getIntroduction(),
                        sightDTO.getDescription(),
                        sightDTO.getAddress(),
                        sight, localization);
        sightLocal = localRepo.saveAndFlush(sightLocal);
        sight.addLocal(sightLocal);

        sight.setSlug(slugUtils.getSlug(sight));
        sightRepo.saveAndFlush(sight);
        return new SightDTO(sight, localization);
    }

    @Transactional
    public SightDTO updateLocal(Long id, SightLocalDTO sightDTO, Localization localization) {
        Sight sight = findById(id);
        SightLocal cur_local =
                sight.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException(
                                localization + " localization for sight with id=" + id + " not found"));
        cur_local.setName(sightDTO.getName());
        cur_local.setDescription(sightDTO.getDescription());
        cur_local.setAddress(sightDTO.getAddress());
        localRepo.saveAndFlush(cur_local);

        sight.setSlug(slugUtils.getSlug(sight));
        sightRepo.saveAndFlush(sight);
        return new SightDTO(sight, localization);
    }

    @Transactional
    public SightDTO updateGeoData(Long id, SightGeoDTO sightDTO, Localization localization) {
        Sight sight = findById(id);
        sight.setLatitude(sightDTO.getLatitude());
        sight.setLongitude(sightDTO.getLongitude());
        sightRepo.saveAndFlush(sight);
        return new SightDTO(sight, localization);
    }

    @Transactional
    public SightDTO updateLogicData(Long id, SightLogicDTO sightDTO, Localization localization) {
        Sight sight = findById(id);
        SightType oldType = sight.getSightType();
        if (oldType != null) {
            oldType.removeSight(sight);
        }

        SightType newType = sightTypeService.findById(sightDTO.getSightTypeId());
        newType.addSight(sight);

        sightRepo.saveAndFlush(sight);
        return new SightDTO(sight, localization);
    }
}
