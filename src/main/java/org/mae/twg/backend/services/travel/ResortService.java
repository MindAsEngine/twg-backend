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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ResortService {
    private final ResortRepo resortRepo;
    private final ResortLocalRepo localRepo;

    public List<ResortDTO> getAll(Localization localization) {
        List<Resort> resorts = resortRepo.findAll();
        List<ResortDTO> resortsDTOs = resorts.stream()
                .filter(resort -> resort.getLocals().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(resort -> new ResortDTO(resort, localization))
                .toList();
        if (resortsDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Resorts with " + localization + " not found");
        }
        return resortsDTOs;
    }

    public ResortDTO getById(Long id, Localization local) {
        Resort resort = resortRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Resort with id=" + id + " not found"));
        return new ResortDTO(resort, local);
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
        return new ResortDTO(resort, local);
    }

    @Transactional
    public ResortDTO addLocal(Long id, ResortRequestDTO sightDTO, Localization localization) {
        Resort resort = resortRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Resort with id=" + id + " not found"));
        boolean isExists = resort.getLocals().stream()
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
        return new ResortDTO(resort, localization);
    }

    @Transactional
    public ResortDTO updateLocal(Long id, ResortRequestDTO sightDTO, Localization localization) {
        Resort resort = resortRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Resort with id=" + id + " not found"));
        ResortLocal cur_local =
                resort.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Resort "
                                + localization + " localization not found"));
        cur_local.setName(sightDTO.getName());
        cur_local.setDescription(sightDTO.getDescription());
        localRepo.saveAndFlush(cur_local);
        return new ResortDTO(resort, localization);
    }
}
