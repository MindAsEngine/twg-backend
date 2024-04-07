package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.response.CountryDTO;
import org.mae.twg.backend.dto.travel.request.geo.CountryGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.CountryLocalDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.enums.TourType;
import org.mae.twg.backend.models.travel.localization.CountryLocal;
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
@Log4j2
public class CountryService implements TravelService<CountryDTO, CountryLocalDTO> {
    private final CountryRepo countryRepo;
    private final CountryLocalRepo localRepo;
    private final ImageService imageService;

    public Country findById(Long id) {
        log.debug("Start CountryService.findById");
        Country country = countryRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Country with id=" + id + " not found");
                    return new ObjectNotFoundException("Country with id=" + id + " not found");
                });
        if (country.getIsDeleted()) {
            log.error("Country with id=" + id + " marked as deleted");
            throw new ObjectNotFoundException("Country with id=" + id + " marked as deleted");
        }
        log.debug("End CountryService.findById");
        return country;
    }

    private List<CountryDTO> modelsToDTOs(Stream<Country> hotels, Localization localization) {
        log.debug("Start CountryService.modelsToDTOs");
        List<CountryDTO> countryDTOS = hotels
                .filter(country -> !country.getIsDeleted())
                .filter(country -> country.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(country -> new CountryDTO(country, localization))
                .toList();
        if (countryDTOS.isEmpty()) {
            log.error("Countries with " + localization + " with localization not found");
            throw new ObjectNotFoundException("Countries with " + localization + " with localization not found");
        }
        log.debug("End CountryService.modelsToDTOs");
        return countryDTOS;
    }

    private CountryLocal createLocal(CountryLocalDTO requestDTO, Localization localization) {
        log.debug("Start CountryService.createLocal");
        CountryLocal local = new CountryLocal(
                requestDTO.getName(),
                localization);
        localRepo.saveAndFlush(local);
        log.debug("End CountryService.createLocal");
        return local;
    }

    private Country createModel(CountryLocalDTO requestDTO, Localization localization) {
        log.debug("Start CountryService.createModel");
        Country model = new Country();
        countryRepo.saveAndFlush(model);

        CountryLocal local = createLocal(requestDTO, localization);
        model.addLocal(local);
        log.debug("End CountryService.createModel");
        return model;
    }

    @Transactional
    public CountryDTO uploadImages(Long id, Localization local, List<MultipartFile> images) throws IOException {
        log.debug("Start CountryService.uploadImages");
        List<String> urls = imageService.saveImages(ModelType.COUNTRY, images);
        Country country = findById(id);
        country.setMediaPath(urls.getFirst());
        countryRepo.saveAndFlush(country);
        log.debug("End CountryService.uploadImages");
        return new CountryDTO(country, local);
    }

    public CountryDTO deleteImages(Long id, Localization local, List<String> images) {
        log.debug("Start CountryService.deleteImages");
        imageService.deleteImages(images);
        Country country = findById(id);
        country.setMediaPath(null);
        countryRepo.save(country);
        log.debug("End CountryService.deleteImages");
        return new CountryDTO(findById(id), local);
    }

    public List<CountryDTO> getAll(Localization localization) {
        log.debug("Start CountryService.getAll");
        List<Country> countries = countryRepo.findAll();
        log.debug("End CountryService.getAll");
        return modelsToDTOs(countries.stream(), localization);
    }

    public List<CountryDTO> getAllPaged(Localization localization, int page, int size) {
        log.debug("Start CountryService.getAllPaged");
        Pageable countryPage = PageRequest.of(page, size);
        Page<Country> countries = countryRepo.findAll(countryPage);
        log.debug("End CountryService.getAllPaged");
        return modelsToDTOs(countries.stream(), localization);
    }

    public List<CountryDTO> getByFilters(List<TourType> types, Localization localization,
                                         Integer page, Integer size) {
        log.debug("Start CountryService.getByFilters");
        Pageable pageable = null;
        if (page != null && size != null) {
            pageable = PageRequest.of(page, size);
        }
        log.debug("End CountryService.getByFilters");
        return modelsToDTOs(countryRepo.findAllByTourType(
                types.stream().map(TourType::name).toList(), pageable).stream(), localization);
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Start CountryService.deleteById");
        Country country = findById(id);
        country.setIsDeleted(true);
        countryRepo.save(country);
        log.debug("End CountryService.deleteById");
    }

    @Transactional
    public CountryDTO create(CountryLocalDTO countryDTO, Localization localization) {
        log.debug("Start CountryService.create");
        Country country = createModel(countryDTO, localization);
        log.debug("End CountryService.create");
        return new CountryDTO(country, localization);
    }

    @Transactional
    public CountryDTO addLocal(Long id, CountryLocalDTO countryDTO, Localization localization) {
        log.debug("Start CountryService.addLocal");
        Country country = findById(id);
        boolean isExists = country.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            log.error(localization + " localization for country with id=" + id + " already exists");
            throw new ObjectAlreadyExistsException(
                    localization + " localization for country with id=" + id + " already exists");
        }

        CountryLocal local = createLocal(countryDTO, localization);
        country.addLocal(local);
        log.debug("End CountryService.addLocal");
        return new CountryDTO(country, localization);
    }

    @Transactional
    public CountryDTO updateLocal(Long id, CountryLocalDTO propertyDTO, Localization localization) {
        log.debug("Start CountryService.updateLocal");
        Country country = findById(id);
        CountryLocal cur_local =
                country.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error(localization + " localization for country with id=" + id + " not found");
                            return new ObjectNotFoundException(
                                    localization + " localization for country with id=" + id + " not found");
                        });
        cur_local.setName(propertyDTO.getName());
        localRepo.saveAndFlush(cur_local);
        log.debug("End CountryService.updateLocal");
        return new CountryDTO(country, localization);
    }

    @Transactional
    public CountryDTO updateGeo(Long id, CountryGeoDTO countryDTO, Localization localization) {
        log.debug("Start CountryService.updateGeo");
        Country country = findById(id);
        country.setGeoData(countryDTO.getGeoData());
        countryRepo.saveAndFlush(country);
        log.debug("End CountryService.updateGeo");
        return new CountryDTO(country, localization);
    }
}
