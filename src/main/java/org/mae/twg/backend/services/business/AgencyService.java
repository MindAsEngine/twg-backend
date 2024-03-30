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
public class AgencyService implements TravelService<AgencyDTO, AgencyRequestDTO> {
    private final AgencyRepo agencyRepo;
    private final AgencyLocalRepo localRepo;

    public Agency findById(Long id) {
        Agency agency = agencyRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Agency with id=" + id + " not found"));
        if (agency.getIsDeleted()) {
            throw new ObjectNotFoundException("Agency with id=" + id + " marked as deleted");
        }
        return agency;
    }

    private List<AgencyDTO> modelsToDTOs(Stream<Agency> agencies, Localization localization) {
        List<AgencyDTO> agencyDTOs = agencies
                .filter(agency -> !agency.getIsDeleted())
                 .filter(agency -> agency.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(tour -> new AgencyDTO(tour, localization))
                .toList();
        if (agencyDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Agencies with " + localization + " with localization not found");
        }
        return agencyDTOs;
    }

    public List<AgencyDTO> getAll(Localization localization) {
        return modelsToDTOs(agencyRepo.findAll().stream(), localization);
    }

    @Override
    public List<AgencyDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable agencyPage = PageRequest.of(page, size);
        Page<Agency> agencies = agencyRepo.findAll(agencyPage);
        return modelsToDTOs(agencies.stream(), localization);
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
