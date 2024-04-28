package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.response.PropertyDTO;
import org.mae.twg.backend.dto.travel.request.locals.PropertyLocalDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Property;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.PropertyLocal;
import org.mae.twg.backend.repositories.travel.PropertyRepo;
import org.mae.twg.backend.repositories.travel.localization.PropertyLocalRepo;
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
public class PropertyService implements TravelService<PropertyDTO, PropertyLocalDTO> {
    private final PropertyRepo propertyRepo;
    private final PropertyLocalRepo localRepo;

    private Property findById(Long id) {
        log.debug("Start PropertyService.findById");
        Property property = propertyRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Property with id=" + id + " not found");
                    return new ObjectNotFoundException("Property with id=" + id + " not found");
                });
        if (property.getIsDeleted()) {
            log.error("Property with id=" + id + " marked as deleted");
            throw new ObjectNotFoundException("Property with id=" + id + " marked as deleted");
        }
        log.debug("End PropertyService.findById");
        return property;
    }

    private List<PropertyDTO> modelsToDTOs(Stream<Property> properties, Localization localization) {
        log.debug("Start PropertyService.modelsToDTOs");
        List<PropertyDTO> propertyDTOs = properties
                .filter(property -> !property.getIsDeleted())
                .filter(property -> property.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(property -> new PropertyDTO(property, localization))
                .toList();
        if (propertyDTOs.isEmpty()) {
            log.error("Properties with " + localization + " localization not found");
            throw new ObjectNotFoundException("Properties with " + localization + " localization not found");
        }
        log.debug("End PropertyService.modelsToDTOs");
        return propertyDTOs;
    }

//    public List<PropertyDTO> getAll(Localization localization) {
//        log.debug("Start PropertyService.getAll");
//        List<Property> properties = propertyRepo.findAll();
//        log.debug("End PropertyService.getAll");
//        return modelsToDTOs(properties.stream(), localization);
//    }

    public List<PropertyDTO> getAllPaged(Localization localization, Integer page, Integer size) {
        log.debug("Start PropertyService.getAllPaged");
        Pageable propertyPage = null;
        if (page != null && size != null) {
            propertyPage = PageRequest.of(page, size);
        }
        Page<Property> properties = propertyRepo.findAllByIsDeletedFalse(propertyPage);
        log.debug("End PropertyService.getAllPaged");
        return modelsToDTOs(properties.stream(), localization);
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Start PropertyService.deleteById");
        Property property = findById(id);
        property.setIsDeleted(true);
        propertyRepo.save(property);
        log.debug("End PropertyService.deleteById");
    }

    @Transactional
    public PropertyDTO create(PropertyLocalDTO propertyDTO, Localization localization) {
        log.debug("Start PropertyService.create");
        Property property = new Property();
        propertyRepo.saveAndFlush(property);

        PropertyLocal local = new PropertyLocal(propertyDTO.getTitle(),
                propertyDTO.getDescription(),
                localization);
        local = localRepo.saveAndFlush(local);
        property.addLocal(local);
        log.debug("End PropertyService.create");
        return new PropertyDTO(property, localization);
    }

    @Transactional
    public PropertyDTO addLocal(Long id, PropertyLocalDTO propertyDTO, Localization localization) {
        log.debug("Start PropertyService.addLocal");
        Property property = findById(id);
        boolean isExists = property.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            log.error(localization + " localization for property with id=" + id + " already exists");
            throw new ObjectAlreadyExistsException(
                    localization + " localization for property with id=" + id + " already exists");
        }

        PropertyLocal propertyLocal =
                new PropertyLocal(propertyDTO.getTitle(),
                        propertyDTO.getDescription(),
                        localization);
        propertyLocal = localRepo.saveAndFlush(propertyLocal);
        property.addLocal(propertyLocal);
        log.debug("End PropertyService.addLocal");
        return new PropertyDTO(property, localization);
    }

    @Transactional
    public PropertyDTO updateLocal(Long id, PropertyLocalDTO propertyDTO, Localization localization) {
        log.debug("Start PropertyService.updateLocal");
        Property property = findById(id);
        PropertyLocal cur_local =
                property.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error(localization + " localization for property with id=" + id + " not found");
                            return new ObjectNotFoundException(
                                    localization + " localization for property with id=" + id + " not found");
                        });
        cur_local.setTitle(propertyDTO.getTitle());
        cur_local.setDescription(propertyDTO.getDescription());
        localRepo.saveAndFlush(cur_local);
        log.debug("End PropertyService.updateLocal");
        return new PropertyDTO(property, localization);
    }
}
