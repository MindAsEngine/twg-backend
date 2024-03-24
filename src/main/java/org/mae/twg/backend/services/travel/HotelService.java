package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.response.HotelDTO;
import org.mae.twg.backend.dto.travel.request.geo.HotelGeoDTO;
import org.mae.twg.backend.dto.travel.request.locals.HotelLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.HotelLogicDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Hotel;
import org.mae.twg.backend.models.travel.Property;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.HotelLocal;
import org.mae.twg.backend.models.travel.media.HotelMedia;
import org.mae.twg.backend.repositories.travel.HotelRepo;
import org.mae.twg.backend.repositories.travel.PropertyRepo;
import org.mae.twg.backend.repositories.travel.SightRepo;
import org.mae.twg.backend.repositories.travel.images.HotelMediaRepo;
import org.mae.twg.backend.repositories.travel.localization.HotelLocalRepo;
import org.mae.twg.backend.services.ImageService;
import org.mae.twg.backend.services.ModelType;
import org.mae.twg.backend.services.TravelService;
import org.mae.twg.backend.utils.SlugUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class HotelService implements TravelService<HotelLocalDTO, HotelLocalDTO> {
    private final HotelRepo hotelRepo;
    private final HotelLocalRepo localRepo;
    private final PropertyRepo propertyRepo;
    private final SightRepo sightRepo;
    private final SlugUtils slugUtils;
    private final ImageService imageService;
    private final HotelMediaRepo hotelMediaRepo;

    private Hotel findById(Long id) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Hotel with id=" + id + " not found"));
        if (hotel.getIsDeleted()) {
            throw new ObjectNotFoundException("Hotel with id=" + id + " marked as deleted");
        }
        return hotel;
    }

    private Hotel findBySlug(String slug) {
        Hotel hotel = hotelRepo.findBySlug(slug)
                .orElseThrow(() -> new ObjectNotFoundException("Hotel with slug=" + slug + " not found"));
        if (hotel.getIsDeleted()) {
            throw new ObjectNotFoundException("Hotel with slug=" + slug + " marked as deleted");
        }
        return hotel;
    }

    private List<HotelDTO> modelsToDTOs(Stream<Hotel> hotels, Localization localization) {
        List<HotelDTO> hotelDTOs = hotels
                .filter(hotel -> !hotel.getIsDeleted())
                .filter(hotel -> hotel.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(hotel -> new HotelDTO(hotel, localization))
                .toList();
        if (hotelDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Hotels with " + localization + " with localization not found");
        }
        return hotelDTOs;
    }

    public List<HotelDTO> getAll(Localization localization) {
        List<Hotel> hotels = hotelRepo.findAll();
        return modelsToDTOs(hotels.stream(), localization);
    }
    @Transactional
    public HotelDTO uploadImages(Long id, Localization local, List<MultipartFile> images) throws IOException {
        List<String> urls = imageService.saveImages(ModelType.HOTEL, images);
        List<HotelMedia> hotelMedias = urls.stream().map(HotelMedia::new).toList();
        Hotel hotel = findById(id);
        for (HotelMedia hotelMedia : hotelMedias) {
            hotel.addMedia(hotelMedia);
        }
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, local);
    }

    public HotelDTO deleteImages(Long id, Localization local, List<String> images) {
        imageService.deleteImages(images);
        List<HotelMedia> hotelMedias = hotelMediaRepo.findByHotel_id(id);
        for (HotelMedia hotelMedia : hotelMedias) {
            if (images.contains(hotelMedia.getMediaPath())) {
                hotelMediaRepo.delete(hotelMedia);
            }
        }
        return new HotelDTO(findById(id), local);
    }

    public List<HotelDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable hotelPage = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepo.findAll(hotelPage);
        return modelsToDTOs(hotels.stream(), localization);
    }

    public HotelDTO getById(Long id, Localization localization) {
        return new HotelDTO(findById(id), localization);
    }

    public HotelDTO getBySlug(String slug, Localization localization) {
        return new HotelDTO(findBySlug(slug), localization);
    }

    @Transactional
    public void deleteById(Long id) {
        Hotel hotel = findById(id);
        hotel.setIsDeleted(true);
        hotelRepo.save(hotel);
    }

    @Transactional
    public HotelDTO create(HotelLocalDTO hotelDTO, Localization localization) {
        Hotel hotel = new Hotel();
        hotelRepo.saveAndFlush(hotel);

        HotelLocal local = new HotelLocal(hotelDTO.getName(),
                hotelDTO.getCity(),
                hotelDTO.getDescription(),
                hotelDTO.getDescription(),
                hotelDTO.getAddress(),
                localization);
        localRepo.saveAndFlush(local);
        hotel.addLocal(local);

        hotel.setSlug(slugUtils.getSlug(hotel));
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO addLocal(Long id, HotelLocalDTO hotelDTO, Localization localization) {
        Hotel hotel = findById(id);
        boolean isExists = hotel.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for hotel with id=" + id + " already exists");
        }

        HotelLocal hotelLocal =
                new HotelLocal(hotelDTO.getName(),
                        hotelDTO.getCity(),
                        hotelDTO.getDescription(),
                        hotelDTO.getDescription(),
                        hotelDTO.getAddress(),
                        localization);
        hotelLocal = localRepo.saveAndFlush(hotelLocal);
        hotel.addLocal(hotelLocal);

        hotel.setSlug(slugUtils.getSlug(hotel));
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO updateLocal(Long id, HotelLocalDTO hotelDTO, Localization localization) {
        Hotel hotel = findById(id);
        HotelLocal cur_local = hotel.getLocals().stream()
                .filter(local -> local.getLocalization() == localization)
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(
                        localization + " localization for hotel with id=" + id + " not found"));

        cur_local.setName(hotelDTO.getName());
        cur_local.setCity(hotelDTO.getCity());
        cur_local.setDescription(hotelDTO.getDescription());
        cur_local.setAddress(hotelDTO.getAddress());
        localRepo.saveAndFlush(cur_local);

        hotel.setSlug(slugUtils.getSlug(hotel));
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO updateLogicData(Long id, HotelLogicDTO hotelDTO, Localization localization) {
        Hotel hotel = findById(id);
        hotel.setStars(hotelDTO.getStars());

        for (Property property : hotel.getProperties().stream().toList()) {
            hotel.removeProperty(property);
        }
        for (Long propertyId : hotelDTO.getPropertyIds()) {
            Property property = propertyRepo.findById(propertyId)
                    .orElseThrow(() -> new ObjectNotFoundException("Property with id=" + propertyId + " not found"));
            hotel.addProperty(property);
        }

        for (Sight sight : hotel.getSights().stream().toList()) {
            hotel.removeSight(sight);
        }
        for (Long sightId : hotelDTO.getSightIds()) {
            Sight sight = sightRepo.findById(sightId)
                    .orElseThrow(() -> new ObjectNotFoundException("Sight with id=" + sightId + " not found"));
            hotel.addSight(sight);
        }

        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO updateGeoData(Long id, HotelGeoDTO hotelDTO, Localization localization) {
        Hotel hotel = findById(id);

        hotel.setLatitude(hotelDTO.getLatitude());
        hotel.setLongitude(hotelDTO.getLongitude());

        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }
}
