package org.mae.twg.backend.services.business;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.business.AgencyDTO;
import org.mae.twg.backend.dto.business.AgencyRequestDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.business.Agency;
import org.mae.twg.backend.models.business.AgencyLocal;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.business.AgencyLocalRepo;
import org.mae.twg.backend.repositories.business.AgencyRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AgencyService {
    private final AgencyRepo agencyRepo;
    private final AgencyLocalRepo localRepo;

    private Agency findById(Long id) {
        Agency agency = agencyRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Agency with id=" + id + " not found"));
        if (agency.getIsDeleted()) {
            throw new ObjectNotFoundException("Agency with id=" + id + " marked as deleted");
        }
        return agency;
    }

    public List<AgencyDTO> getAll(Localization localization) {
        List<Agency> agencies = agencyRepo.findAll();
        List<AgencyDTO> agencyDTOs = agencies.stream()
                .filter(agency -> !agency.getIsDeleted())
                .filter(agency -> agency.getLocals().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(agency -> new AgencyDTO(agency, localization))
                .toList();
        if (agencyDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Agencies with " + localization + " not found");
        }
        return agencyDTOs;
    }

    public AgencyDTO getById(Long id, Localization local) {
        return new AgencyDTO(findById(id), local);
    }

    @Transactional
    public void deleteById(Long id) {
        Agency agency = findById(id);
        agency.setIsDeleted(true);
        agencyRepo.save(agency);
    }

    @Transactional
    public AgencyDTO create(AgencyRequestDTO agencyDTO, Localization local) {
        Agency agency = new Agency();
        agencyRepo.saveAndFlush(agency);
        AgencyLocal agencyLocal =
                new AgencyLocal(agencyDTO.getName(),
                        agencyDTO.getDescription(),
                        agencyDTO.getContacts(),
                        agencyDTO.getAddress(), local);
        agencyLocal = localRepo.saveAndFlush(agencyLocal);
        agency.addLocal(agencyLocal);
        return new AgencyDTO(agency, local);
    }

    @Transactional
    public AgencyDTO addLocal(Long id, AgencyRequestDTO agencyDTO, Localization localization) {
        Agency agency = findById(id);
        boolean isExists = agency.getLocals().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for agency with id=" + id + " already exists");
        }

        AgencyLocal resortLocal =
                new AgencyLocal(agencyDTO.getName(),
                        agencyDTO.getDescription(),
                        agencyDTO.getContacts(),
                        agencyDTO.getAddress(),
                        localization);
        resortLocal = localRepo.saveAndFlush(resortLocal);
        agency.addLocal(resortLocal);
        return new AgencyDTO(agency, localization);
    }

    @Transactional
    public AgencyDTO updateLocal(Long id, AgencyRequestDTO agencyDTO, Localization localization) {
        Agency agency = findById(id);
        AgencyLocal cur_local =
                agency.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException(
                                localization + " localization for agency with id=" + id + " not found"));
        cur_local.setName(agencyDTO.getName());
        cur_local.setDescription(agencyDTO.getDescription());
        cur_local.setContacts(agencyDTO.getContacts());
        cur_local.setAddress(agencyDTO.getAddress());
        localRepo.saveAndFlush(cur_local);
        return new AgencyDTO(agency, localization);
    }
}
