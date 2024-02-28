package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.SightDTO;
import org.mae.twg.backend.dto.travel.request.SightRequestDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.SightLocal;
import org.mae.twg.backend.repositories.travel.SightRepo;
import org.mae.twg.backend.repositories.travel.localization.SightLocalRepo;
import org.mae.twg.backend.utils.SlugUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SightService {
    private final SightRepo sightRepo;
    private final SightLocalRepo localRepo;
    private final SlugUtils slugUtils;

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

    public List<SightDTO> getAll(Localization localization) {
        List<Sight> sights = sightRepo.findAll();

        List<SightDTO> sightDTOs = sights.stream()
                .filter(sight -> !sight.getIsDeleted())
                .filter(sight -> sight.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(sight -> new SightDTO(sight, localization))
                .toList();
        if (sightDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Sights with " + localization + " not found");
        }
        return sightDTOs;
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
    public SightDTO create(SightRequestDTO sightDTO, Localization local) {
        Sight sight = new Sight();
        sightRepo.saveAndFlush(sight);
        SightLocal sightLocal =
                new SightLocal(sightDTO.getName(),
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
    public SightDTO addLocal(Long id, SightRequestDTO sightDTO, Localization localization) {
        Sight sight = findById(id);
        boolean isExists = sight.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for sight with id=" + id + " already exists");
        }

        SightLocal sightLocal =
                new SightLocal(sightDTO.getName(),
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
    public SightDTO updateLocal(Long id, SightRequestDTO sightDTO, Localization localization) {
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
}
