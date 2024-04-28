package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.request.locals.ResortLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.ResortLogicDTO;
import org.mae.twg.backend.dto.travel.response.ResortDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.models.travel.Resort;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.ResortLocal;
import org.mae.twg.backend.repositories.travel.ResortRepo;
import org.mae.twg.backend.repositories.travel.localization.ResortLocalRepo;
import org.mae.twg.backend.services.TravelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Log4j2
public class ResortService implements TravelService<ResortDTO, ResortLocalDTO> {
    private final ResortRepo resortRepo;
    private final ResortLocalRepo localRepo;
    private final CountryService countryService;

    public Resort findById(Long id) {
        log.debug("Start ResortService.findById");
        Resort resort = resortRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Resort with id=" + id + " not found");
                    return new ObjectNotFoundException("Resort with id=" + id + " not found");
                });
        if (resort.getIsDeleted()) {
            log.error("Resort with id=" + id + " marked as deleted");
            throw new ObjectNotFoundException("Resort with id=" + id + " marked as deleted");
        }
        log.debug("End ResortService.findById");
        return resort;
    }

    private List<ResortDTO> modelsToDTOs(Stream<Resort> resorts, Localization localization) {
        log.debug("Start ResortService.modelsToDTOs");
        List<ResortDTO> resortsDTOs = resorts
                .filter(resort -> !resort.getIsDeleted())
                .filter(resort -> resort.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(resort -> new ResortDTO(resort, localization))
                .toList();
        if (resortsDTOs.isEmpty()) {
            log.error("Resorts with " + localization + " not found");
            throw new ObjectNotFoundException("Resorts with " + localization + " not found");
        }
        log.debug("End ResortService.modelsToDTOs");
        return resortsDTOs;
    }

//    public List<ResortDTO> getAll(Localization localization) {
//        log.debug("Start ResortService.getAll");
//        List<Resort> resorts = resortRepo.findAll();
//        log.debug("End ResortService.getAll");
//        return modelsToDTOs(resorts.stream(), localization);
//    }

    public List<ResortDTO> getAllPaged(Localization localization, Integer page, Integer size) {
        log.debug("Start ResortService.getAllPaged");
        Pageable resortPage = PageRequest.of(page, size);
        Page<Resort> resorts = resortRepo.findAll(resortPage);
        log.debug("End ResortService.getAllPaged");
        return modelsToDTOs(resorts.stream(), localization);
    }

    public List<ResortDTO> getByFilters(List<Long> countryIds, Localization localization,
                                       Integer page, Integer size) {
        log.debug("Start ResortService.getByFilters");
        Pageable pageable = null;
        if (page != null && size != null) {
            pageable = PageRequest.of(page, size);
        }
        log.debug("End ResortService.getByFilters");
        return modelsToDTOs(resortRepo.findAllByFilters(countryIds, pageable).stream(), localization);
    }

    public ResortDTO getById(Long id, Localization local) {
        log.debug("Start ResortService.getById");
        return new ResortDTO(findById(id), local);
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Start ResortService.deleteById");
        Resort resort = findById(id);
        resort.setIsDeleted(true);
        resortRepo.save(resort);
        log.debug("End ResortService.deleteById");
    }

    @Transactional
    public ResortDTO create(ResortLocalDTO sightDTO, Localization local) {
        log.debug("Start ResortService.create");
        Resort resort = new Resort();
        resortRepo.saveAndFlush(resort);
        ResortLocal resortLocal =
                new ResortLocal(sightDTO.getName(),
                        local);
        resortLocal = localRepo.saveAndFlush(resortLocal);
        resort.addLocal(resortLocal);

        resortRepo.saveAndFlush(resort);
        log.debug("End ResortService.create");
        return new ResortDTO(resort, local);
    }

    @Transactional
    public ResortDTO addLocal(Long id, ResortLocalDTO sightDTO, Localization localization) {
        log.debug("Start ResortService.addLocal");
        Resort resort = findById(id);
        boolean isExists = resort.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            log.error(localization + " localization for resort with id=" + id + " already exists");
            throw new ObjectAlreadyExistsException(
                    localization + " localization for resort with id=" + id + " already exists");
        }

        ResortLocal resortLocal =
                new ResortLocal(sightDTO.getName(),
                        localization);
        resortLocal = localRepo.saveAndFlush(resortLocal);
        resort.addLocal(resortLocal);

        resortRepo.saveAndFlush(resort);
        log.debug("End ResortService.addLocal");
        return new ResortDTO(resort, localization);
    }

    @Transactional
    public ResortDTO updateLocal(Long id, ResortLocalDTO sightDTO, Localization localization) {
        log.debug("Start ResortService.updateLocal");
        Resort resort = findById(id);
        ResortLocal cur_local =
                resort.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error(localization + " localization for resort with id=" + id + "not found");
                            return new ObjectNotFoundException(
                                    localization + " localization for resort with id=" + id + "not found");
                        });
        cur_local.setName(sightDTO.getName());
        localRepo.saveAndFlush(cur_local);

        resortRepo.saveAndFlush(resort);
        log.debug("End ResortService.updateLocal");
        return new ResortDTO(resort, localization);
    }

    @Transactional
    public ResortDTO updateLogicData(Long id, ResortLogicDTO resortDTO, Localization localization) {
        log.debug("Start ResortService.updateLogicData");
        Resort resort = findById(id);
        Country oldCountry = resort.getCountry();
        if (oldCountry != null) {
            oldCountry.removeResort(resort);
        }

        Country newCountry = countryService.findById(resortDTO.getCountryId());
        newCountry.addResort(resort);

        resortRepo.saveAndFlush(resort);
        log.debug("End ResortService.updateLogicData");
        return new ResortDTO(resort, localization);
    }
}
