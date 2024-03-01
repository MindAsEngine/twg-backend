package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.ResortDTO;
import org.mae.twg.backend.dto.travel.request.ResortRequestDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Resort;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.ResortLocal;
import org.mae.twg.backend.repositories.travel.ResortRepo;
import org.mae.twg.backend.repositories.travel.localization.ResortLocalRepo;
import org.mae.twg.backend.services.TravelService;
import org.mae.twg.backend.utils.SlugUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class ResortService implements TravelService<Resort, ResortRequestDTO, ResortRequestDTO> {
    private final ResortRepo resortRepo;
    private final ResortLocalRepo localRepo;
    private final SlugUtils<Resort> slugUtils;

    private Resort findById(Long id) {
        Resort resort = resortRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Resort with id=" + id + " not found"));
        if (resort.getIsDeleted()) {
            throw new ObjectNotFoundException("Resort with id=" + id + " marked as deleted");
        }
        return resort;
    }

    private Resort findBySlug(String slug) {
        Resort resort = resortRepo.findBySlug(slug)
                .orElseThrow(() -> new ObjectNotFoundException("Resort with slug=" + slug + " not found"));
        if (resort.getIsDeleted()) {
            throw new ObjectNotFoundException("Resort with slug=" + slug + " marked as deleted");
        }
        return resort;
    }

    private List<ResortDTO> modelsToDTOs(Stream<Resort> resorts, Localization localization) {
        List<ResortDTO> resortsDTOs = resorts
                .filter(resort -> !resort.getIsDeleted())
                .filter(resort -> resort.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(resort -> new ResortDTO(resort, localization))
                .toList();
        if (resortsDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Resorts with " + localization + " not found");
        }
        return resortsDTOs;
    }

    public List<ResortDTO> getAll(Localization localization) {
        List<Resort> resorts = resortRepo.findAll();
        return modelsToDTOs(resorts.stream(), localization);
    }

    public List<ResortDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable resortPage = PageRequest.of(page, size);
        Page<Resort> resorts = resortRepo.findAll(resortPage);
        return modelsToDTOs(resorts.stream(), localization);
    }

    public ResortDTO getById(Long id, Localization local) {
        return new ResortDTO(findById(id), local);
    }

    public ResortDTO getBySlug(String slug, Localization local) {
        return new ResortDTO(findBySlug(slug), local);
    }

    @Transactional
    public void deleteById(Long id) {
        Resort resort = findById(id);
        resort.setIsDeleted(true);
        resortRepo.save(resort);
    }

    @Transactional
    public ResortDTO create(ResortRequestDTO sightDTO, Localization local) {
        Resort resort = new Resort();
        resortRepo.saveAndFlush(resort);
        ResortLocal resortLocal =
                new ResortLocal(sightDTO.getName(),
                        sightDTO.getDescription(),
                        local, resort);
        resortLocal = localRepo.saveAndFlush(resortLocal);
        resort.addLocal(resortLocal);

        resort.setSlug(slugUtils.getSlug(resort));
        resortRepo.saveAndFlush(resort);
        return new ResortDTO(resort, local);
    }

    @Transactional
    public ResortDTO addLocal(Long id, ResortRequestDTO sightDTO, Localization localization) {
        Resort resort = findById(id);
        boolean isExists = resort.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for resort with id=" + id + " already exists");
        }

        ResortLocal resortLocal =
                new ResortLocal(sightDTO.getName(),
                        sightDTO.getDescription(),
                        localization, resort);
        resortLocal = localRepo.saveAndFlush(resortLocal);
        resort.addLocal(resortLocal);

        resort.setSlug(slugUtils.getSlug(resort));
        resortRepo.saveAndFlush(resort);
        return new ResortDTO(resort, localization);
    }

    @Transactional
    public ResortDTO updateLocal(Long id, ResortRequestDTO sightDTO, Localization localization) {
        Resort resort = findById(id);
        ResortLocal cur_local =
                resort.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException(
                                localization + " localization for resort with id=" + id + "not found"));
        cur_local.setName(sightDTO.getName());
        cur_local.setDescription(sightDTO.getDescription());
        localRepo.saveAndFlush(cur_local);

        resort.setSlug(slugUtils.getSlug(resort));
        resortRepo.saveAndFlush(resort);
        return new ResortDTO(resort, localization);
    }
}
