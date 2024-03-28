package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
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
public class PropertyService implements TravelService<PropertyDTO, PropertyLocalDTO> {
    private final PropertyRepo propertyRepo;
    private final PropertyLocalRepo localRepo;

    private Property findById(Long id) {
        Property property = propertyRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Property with id=" + id + " not found"));
        if (property.getIsDeleted()) {
            throw new ObjectNotFoundException("Property with id=" + id + " marked as deleted");
        }
        return property;
    }

    private List<PropertyDTO> modelsToDTOs(Stream<Property> properties, Localization localization) {
        List<PropertyDTO> propertyDTOs = properties
                .filter(property -> !property.getIsDeleted())
                .filter(property -> property.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(property -> new PropertyDTO(property, localization))
                .toList();
        if (propertyDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Properties with " + localization + " localization not found");
        }
        return propertyDTOs;
    }

    public List<PropertyDTO> getAll(Localization localization) {
        List<Property> properties = propertyRepo.findAll();
        return modelsToDTOs(properties.stream(), localization);
    }

    public List<PropertyDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable propertyPage = PageRequest.of(page, size);
        Page<Property> properties = propertyRepo.findAll(propertyPage);
        return modelsToDTOs(properties.stream(), localization);
    }

    @Transactional
    public void deleteById(Long id) {
        Property property = findById(id);
        property.setIsDeleted(true);
        propertyRepo.save(property);
    }

    @Transactional
    public PropertyDTO create(PropertyLocalDTO propertyDTO, Localization localization) {
        Property property = new Property();
        propertyRepo.saveAndFlush(property);

        PropertyLocal local = new PropertyLocal(propertyDTO.getTitle(),
                propertyDTO.getDescription(),
                localization);
        local = localRepo.saveAndFlush(local);
        property.addLocal(local);
        return new PropertyDTO(property, localization);
    }

    @Transactional
    public PropertyDTO addLocal(Long id, PropertyLocalDTO propertyDTO, Localization localization) {
        Property property = findById(id);
        boolean isExists = property.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for property with id=" + id + " already exists");
        }

        PropertyLocal propertyLocal =
                new PropertyLocal(propertyDTO.getTitle(),
                        propertyDTO.getDescription(),
                        localization);
        propertyLocal = localRepo.saveAndFlush(propertyLocal);
        property.addLocal(propertyLocal);
        return new PropertyDTO(property, localization);
    }

    @Transactional
    public PropertyDTO updateLocal(Long id, PropertyLocalDTO propertyDTO, Localization localization) {
        Property property = findById(id);
        PropertyLocal cur_local =
                property.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException(
                                localization + " localization for property with id=" + id + " not found"));
        cur_local.setTitle(propertyDTO.getTitle());
        cur_local.setDescription(propertyDTO.getDescription());
        localRepo.saveAndFlush(cur_local);
        return new PropertyDTO(property, localization);
    }
}
