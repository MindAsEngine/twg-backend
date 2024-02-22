package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.CountryDTO;
import org.mae.twg.backend.dto.travel.request.CountryRequestDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.CountryLocal;
import org.mae.twg.backend.repositories.travel.CountryRepo;
import org.mae.twg.backend.repositories.travel.localization.CountryLocalRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepo countryRepo;
    private final CountryLocalRepo localRepo;

    private Country findById(Long id) {
        Country country = countryRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Country with id=" + id + " not found"));
        if (country.getIsDeleted()) {
            throw new ObjectNotFoundException("Country with id=" + id + " marked as deleted");
        }
        return country;
    }

    public List<CountryDTO> getAll(Localization localization) {
        List<Country> countries = countryRepo.findAll();
        List<CountryDTO> countryDTOs = countries.stream()
                .filter(country -> !country.getIsDeleted())
                .filter(country -> country.getLocals().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(country -> new CountryDTO(country, localization))
                .toList();
        if (countryDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Countries with " + localization + " localization not found");
        }
        return countryDTOs;
    }

    @Transactional
    public CountryDTO create(CountryRequestDTO countryDTO, Localization localization) {
        Country country = new Country();
        countryRepo.saveAndFlush(country);

        CountryLocal local = new CountryLocal(countryDTO.getName(),
                countryDTO.getDescription(),
                localization, country);
        local = localRepo.saveAndFlush(local);
        country.addLocal(local);
        return new CountryDTO(country, localization);
    }

    @Transactional
    public CountryDTO addLocal(Long id, CountryRequestDTO propertyDTO, Localization localization) {
        Country country = findById(id);
        boolean isExists = country.getLocals().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for country with id=" + id + " already exists");
        }

        CountryLocal countryLocal =
                new CountryLocal(propertyDTO.getName(),
                        propertyDTO.getDescription(),
                        localization, country);
        countryLocal = localRepo.saveAndFlush(countryLocal);
        country.addLocal(countryLocal);
        return new CountryDTO(country, localization);
    }

    @Transactional
    public CountryDTO updateLocal(Long id, CountryRequestDTO propertyDTO, Localization localization) {
        Country country = findById(id);
        CountryLocal cur_local =
                country.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Country "
                                + localization + " localization not found"));
        cur_local.setName(propertyDTO.getName());
        cur_local.setDescription(propertyDTO.getDescription());
        localRepo.saveAndFlush(cur_local);
        return new CountryDTO(country, localization);
    }
}
