package org.mae.twg.backend.services.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class AgencyService implements TravelService<AgencyDTO, AgencyRequestDTO> {
    private final AgencyRepo agencyRepo;
    private final AgencyLocalRepo localRepo;

    public Agency findById(Long id) {
        log.debug("Start AgencyService.findById");
        Agency agency = agencyRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Agency with id=" + id + " not found");
                    return new ObjectNotFoundException("Agency with id=" + id + " not found");
                });
        if (agency.getIsDeleted()) {
            log.error("Agency with id=" + id + " marked as deleted");
            throw new ObjectNotFoundException("Agency with id=" + id + " marked as deleted");
        }
        log.debug("End AgencyService.findById");
        return agency;
    }

    private List<AgencyDTO> modelsToDTOs(Stream<Agency> agencies, Localization localization) {
        log.debug("Start AgencyService.modelsToDTOs");
        List<AgencyDTO> agencyDTOs = agencies
                .filter(agency -> !agency.getIsDeleted())
                 .filter(agency -> agency.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(tour -> new AgencyDTO(tour, localization))
                .toList();
        if (agencyDTOs.isEmpty()) {
            log.error("Agencies with " + localization + " with localization not found");
            throw new ObjectNotFoundException("Agencies with " + localization + " with localization not found");
        }
        log.debug("End AgencyService.modelsToDTOs");
        return agencyDTOs;
    }

    public List<AgencyDTO> getAll(Localization localization) {
        log.debug("Start AgencyService.getAll");
        return modelsToDTOs(agencyRepo.findAll().stream(), localization);
    }

    @Override
    public List<AgencyDTO> getAllPaged(Localization localization, int page, int size) {
        log.debug("Start AgencyService.getAllPaged");
        Pageable agencyPage = PageRequest.of(page, size);
        Page<Agency> agencies = agencyRepo.findAll(agencyPage);
        log.debug("End AgencyService.getAllPaged");
        return modelsToDTOs(agencies.stream(), localization);
    }

    public AgencyDTO getById(Long id, Localization local) {
        log.debug("Start AgencyService.getById");
        return new AgencyDTO(findById(id), local);
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Start AgencyService.deleteById");
        Agency agency = findById(id);
        agency.setIsDeleted(true);
        agencyRepo.save(agency);
        log.debug("End AgencyService.deleteById");
    }

    @Transactional
    public AgencyDTO create(AgencyRequestDTO agencyDTO, Localization local) {
        log.debug("Start AgencyService.create");
        Agency agency = new Agency();
        agencyRepo.saveAndFlush(agency);
        AgencyLocal agencyLocal =
                new AgencyLocal(agencyDTO.getName(),
                        agencyDTO.getDescription(),
                        agencyDTO.getContacts(),
                        agencyDTO.getAddress(), local);
        agencyLocal = localRepo.saveAndFlush(agencyLocal);
        agency.addLocal(agencyLocal);
        log.debug("End AgencyService.create");
        return new AgencyDTO(agency, local);
    }

    @Transactional
    public AgencyDTO addLocal(Long id, AgencyRequestDTO agencyDTO, Localization localization) {
        log.debug("Start AgencyService.addLocal");
        Agency agency = findById(id);
        boolean isExists = agency.getLocals().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            log.error(localization + " localization for agency with id=" + id + " already exists");
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
        log.debug("End AgencyService.addLocal");
        return new AgencyDTO(agency, localization);
    }

    @Transactional
    public AgencyDTO updateLocal(Long id, AgencyRequestDTO agencyDTO, Localization localization) {
        log.debug("Start AgencyService.updateLocal");
        Agency agency = findById(id);
        AgencyLocal cur_local =
                agency.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error(localization + " localization for agency with id=" + id + " not found");
                        return new ObjectNotFoundException(
                                localization + " localization for agency with id=" + id + " not found");
                        });
        cur_local.setName(agencyDTO.getName());
        cur_local.setDescription(agencyDTO.getDescription());
        cur_local.setContacts(agencyDTO.getContacts());
        cur_local.setAddress(agencyDTO.getAddress());
        localRepo.saveAndFlush(cur_local);
        log.debug("End AgencyService.updateLocal");
        return new AgencyDTO(agency, localization);
    }
}
