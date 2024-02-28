package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.PropertyDTO;
import org.mae.twg.backend.dto.travel.request.PropertyRequestDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Property;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.PropertyLocal;
import org.mae.twg.backend.repositories.travel.PropertyRepo;
import org.mae.twg.backend.repositories.travel.localization.PropertyLocalRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {
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

    public List<PropertyDTO> getAll(Localization localization) {
        List<Property> properties = propertyRepo.findAll();
        List<PropertyDTO> propertyDTOs = properties.stream()
                .filter(property -> !property.getIsDeleted())
                .filter(property -> property.getLocals().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(property -> new PropertyDTO(property, localization))
                .toList();
        if (propertyDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Properties with " + localization + " localization not found");
        }
        return propertyDTOs;
    }

    @Transactional
    public void deleteById(Long id) {
        Property property = findById(id);
        property.setIsDeleted(true);
        propertyRepo.save(property);
    }

    @Transactional
    public PropertyDTO create(PropertyRequestDTO propertyDTO, Localization localization) {
        Property property = new Property();
        propertyRepo.saveAndFlush(property);

        PropertyLocal local = new PropertyLocal(propertyDTO.getTitle(),
                propertyDTO.getDescription(),
                property, localization);
        local = localRepo.saveAndFlush(local);
        property.addLocal(local);
        return new PropertyDTO(property, localization);
    }

    @Transactional
    public PropertyDTO addLocal(Long id, PropertyRequestDTO propertyDTO, Localization localization) {
        Property property = findById(id);
        boolean isExists = property.getLocals().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for property with id=" + id + " already exists");
        }

        PropertyLocal propertyLocal =
                new PropertyLocal(propertyDTO.getTitle(),
                        propertyDTO.getDescription(),
                        property, localization);
        propertyLocal = localRepo.saveAndFlush(propertyLocal);
        property.addLocal(propertyLocal);
        return new PropertyDTO(property, localization);
    }

    @Transactional
    public PropertyDTO updateLocal(Long id, PropertyRequestDTO propertyDTO, Localization localization) {
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
