package org.mae.twg.backend.services.travel;

import jakarta.transaction.Transactional;
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
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SightService {
    private final SightRepo sightRepo;
    private final SightLocalRepo localRepo;

    public List<SightDTO> getAll(Localization localization) {
        List<Sight> sights = sightRepo.findAll();
        List<SightDTO> sightDTOs = sights.stream()
                .filter(sight -> sight.getLocals().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(sight -> new SightDTO(sight, localization))
                .toList();
        if (sightDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Sights with " + localization + " not found");
        }
        return sightDTOs;
    }

    public SightDTO getById(Long id, Localization local) {
        Sight sight = sightRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Sight with id=" + id + " not found"));
        return new SightDTO(sight, local);
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
        return new SightDTO(sight, local);
    }

    @Transactional
    public SightDTO addLocal(Long id, SightRequestDTO sightDTO, Localization localization) {
        Sight sight = sightRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Sight with id=" + id + " not found"));
        boolean isExists = sight.getLocals().stream()
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
        return new SightDTO(sight, localization);
    }

    @Transactional
    public SightDTO updateLocal(Long id, SightRequestDTO sightDTO, Localization localization) {
        Sight sight = sightRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Sight with id=" + id + " not found"));
        SightLocal cur_local =
                sight.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Sight "
                                + localization.name() + " localization not found"));
        cur_local.setName(sightDTO.getName());
        cur_local.setDescription(sightDTO.getDescription());
        cur_local.setAddress(sightDTO.getAddress());
        localRepo.saveAndFlush(cur_local);
        return new SightDTO(sight, localization);
    }
}
