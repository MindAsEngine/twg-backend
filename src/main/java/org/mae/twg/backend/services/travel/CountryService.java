package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.CountryDTO;
import org.mae.twg.backend.dto.travel.HotelDTO;
import org.mae.twg.backend.dto.travel.request.CountryRequestDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.models.travel.Hotel;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.CountryLocal;
import org.mae.twg.backend.models.travel.media.HotelMedia;
import org.mae.twg.backend.repositories.travel.CountryRepo;
import org.mae.twg.backend.repositories.travel.localization.CountryLocalRepo;
import org.mae.twg.backend.services.ImageService;
import org.mae.twg.backend.services.ModelType;
import org.mae.twg.backend.services.TravelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CountryService implements TravelService<CountryRequestDTO, CountryRequestDTO> {
    private final CountryRepo countryRepo;
    private final CountryLocalRepo localRepo;
    private final ImageService imageService;

    private Country findById(Long id) {
        Country country = countryRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Country with id=" + id + " not found"));
        if (country.getIsDeleted()) {
            throw new ObjectNotFoundException("Country with id=" + id + " marked as deleted");
        }
        return country;
    }

    private List<CountryDTO> modelsToDTOs(Stream<Country> hotels, Localization localization) {
        List<CountryDTO> countryDTOS = hotels
                .filter(country -> !country.getIsDeleted())
                .filter(country -> country.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(country -> new CountryDTO(country, localization))
                .toList();
        if (countryDTOS.isEmpty()) {
            throw new ObjectNotFoundException("Countries with " + localization + " with localization not found");
        }
        return countryDTOS;
    }

    @Transactional
    public CountryDTO uploadImages(Long id, Localization local, List<MultipartFile> images) throws IOException {
        List<String> urls = imageService.saveImages(ModelType.COUNTRY, images);
        Country country = findById(id);
        country.setMediaPath(urls.get(0));
        countryRepo.saveAndFlush(country);
        return new CountryDTO(country, local);
    }

    public CountryDTO deleteImages(Long id, Localization local, List<String> images) {
        imageService.deleteImages(images);
        Country country = findById(id);
        country.setMediaPath(null);
        countryRepo.save(country);
        return new CountryDTO(findById(id), local);
    }

    private CountryLocal createLocal(CountryRequestDTO requestDTO, Localization localization) {
        CountryLocal local = new CountryLocal(
                requestDTO.getName(),
                localization);
        localRepo.saveAndFlush(local);
        return local;
    }

    private Country createModel(CountryRequestDTO requestDTO, Localization localization) {
        Country model = new Country();
        countryRepo.saveAndFlush(model);

        CountryLocal local = createLocal(requestDTO, localization);
        model.addLocal(local);
        return model;
    }

    public List<CountryDTO> getAll(Localization localization) {
        List<Country> countries = countryRepo.findAll();
        return modelsToDTOs(countries.stream(), localization);
    }

    public List<CountryDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable countryPage = PageRequest.of(page, size);
        Page<Country> countries = countryRepo.findAll(countryPage);
        return modelsToDTOs(countries.stream(), localization);
    }

    @Transactional
    public void deleteById(Long id) {
        Country country = findById(id);
        country.setIsDeleted(true);
        countryRepo.save(country);
    }

    @Transactional
    public CountryDTO create(CountryRequestDTO countryDTO, Localization localization) {
        Country country = createModel(countryDTO, localization);
        return new CountryDTO(country, localization);
    }

    @Transactional
    public CountryDTO addLocal(Long id, CountryRequestDTO countryDTO, Localization localization) {
        Country country = findById(id);
        boolean isExists = country.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for country with id=" + id + " already exists");
        }

        CountryLocal local = createLocal(countryDTO, localization);
        country.addLocal(local);

        return new CountryDTO(country, localization);
    }

    @Transactional
    public CountryDTO updateLocal(Long id, CountryRequestDTO propertyDTO, Localization localization) {
        Country country = findById(id);
        CountryLocal cur_local =
                country.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException(
                                localization + " localization for country with id=" + id + " not found"));
        cur_local.setName(propertyDTO.getName());
        localRepo.saveAndFlush(cur_local);
        return new CountryDTO(country, localization);
    }
}
