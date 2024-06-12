package org.mae.twg.backend.services.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.PageDTO;
import org.mae.twg.backend.dto.business.AgencyDTO;
import org.mae.twg.backend.dto.business.AgencyRequestDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.business.Agency;
import org.mae.twg.backend.models.business.AgencyLocal;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.business.AgencyLocalRepo;
import org.mae.twg.backend.repositories.business.AgencyRepo;
import org.mae.twg.backend.services.TravelService;
import org.mae.twg.backend.services.auth.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class AgencyService implements TravelService<AgencyDTO, AgencyRequestDTO> {
    private final AgencyRepo agencyRepo;
    private final AgencyLocalRepo localRepo;
    private final UserService userService;

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

    private PageDTO<AgencyDTO> modelsToDTOs(PageDTO<Agency> agencies, Localization localization) {
        log.debug("Start AgencyService.modelsToDTOs");
        if (agencies.isEmpty()) {
            log.error("Agencies with " + localization + " with localization not found");
            throw new ObjectNotFoundException("Agencies with " + localization + " with localization not found");
        }
        PageDTO<AgencyDTO> agencyDTOs = agencies
                .apply(tour -> new AgencyDTO(tour, localization));
        log.debug("End AgencyService.modelsToDTOs");
        return agencyDTOs;
    }

//    public List<AgencyDTO> getAll(Localization localization) {
//        log.debug("Start AgencyService.getAll");
//        return modelsToDTOs(agencyRepo.findAll().stream(), localization);
//    }

    @Override
    public PageDTO<AgencyDTO> getAllPaged(Localization localization, Integer page, Integer size) {
        log.debug("Start AgencyService.getAllPaged");
        Pageable agencyPage = null;
        if (page != null && size != null) {
            agencyPage = PageRequest.of(page, size);
        }
        Page<Agency> agencies = agencyRepo.findAllByIsDeletedFalse(agencyPage);
        log.debug("End AgencyService.getAllPaged");
        return modelsToDTOs(new PageDTO<>(agencies), localization);
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

    public AgencyDTO addAgent(Long id, String username, Localization localization) {
        log.debug("Start AgencyService.addAgent");
        Agency agency = findById(id);
        User agent = userService.loadUserByUsername(username);
        agency.addAgent(agent);
        agencyRepo.saveAndFlush(agency);
        log.debug("End AgencyService.addAgent");
        return new AgencyDTO(agency, localization);
    }
}
