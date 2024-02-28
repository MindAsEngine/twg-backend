package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.HotelDTO;
import org.mae.twg.backend.dto.travel.request.HotelLocalRequestDTO;
import org.mae.twg.backend.dto.travel.request.HotelRequestDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Hotel;
import org.mae.twg.backend.models.travel.Property;
import org.mae.twg.backend.models.travel.Sight;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.HotelLocal;
import org.mae.twg.backend.repositories.travel.HotelRepo;
import org.mae.twg.backend.repositories.travel.PropertyRepo;
import org.mae.twg.backend.repositories.travel.SightRepo;
import org.mae.twg.backend.repositories.travel.localization.HotelLocalRepo;
import org.mae.twg.backend.utils.SlugUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HotelService {
    private final HotelRepo hotelRepo;
    private final HotelLocalRepo localRepo;
    private final PropertyRepo propertyRepo;
    private final SightRepo sightRepo;
    private final SlugUtils slugUtils;

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

    public List<HotelDTO> getAll(Localization localization) {
        List<Hotel> hotels = hotelRepo.findAll();
        List<HotelDTO> hotelDTOs = hotels.stream()
                .filter(hotel -> !hotel.getIsDeleted())
                .filter(hotel -> hotel.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(hotel -> new HotelDTO(hotel, localization))
                .toList();
        if (hotelDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Hotels with " + localization + " with localization not found");
        }
        return hotelDTOs;
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
    public HotelDTO create(HotelRequestDTO hotelDTO, Localization localization) {
        Hotel hotel = new Hotel();
        hotel.setStars(hotelDTO.getStars());
        hotelRepo.saveAndFlush(hotel);
        for (Long id : hotelDTO.getPropertyIds()) {
            Property property = propertyRepo.findById(id)
                    .orElseThrow(() -> new ObjectNotFoundException("Property with id=" + id + " not found"));
            hotel.addProperty(property);
        }

        for (Long id : hotelDTO.getSightIds()) {
            Sight sight = sightRepo.findById(id)
                    .orElseThrow(() -> new ObjectNotFoundException("Sight with id=" + id + " not found"));
            hotel.addSight(sight);
        }

        hotelRepo.saveAndFlush(hotel);

        HotelLocal local = new HotelLocal(hotelDTO.getName(),
                hotelDTO.getCity(),
                hotelDTO.getDescription(),
                hotelDTO.getAddress(),
                localization, hotel);
        localRepo.saveAndFlush(local);
        hotel.addLocal(local);

        hotel.setSlug(slugUtils.getSlug(hotel));
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO addLocal(Long id, HotelLocalRequestDTO hotelDTO, Localization localization) {
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
                        hotelDTO.getAddress(),
                        localization, hotel);
        hotelLocal = localRepo.saveAndFlush(hotelLocal);
        hotel.addLocal(hotelLocal);

        hotel.setSlug(slugUtils.getSlug(hotel));
        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO updateLocal(Long id, HotelLocalRequestDTO hotelDTO, Localization localization) {
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
    public HotelDTO updateProperties(Long id, List<Long> propertyIds, Localization localization) {
        Hotel hotel = findById(id);
        for (Property property : hotel.getProperties().stream().toList()) {
            hotel.removeProperty(property);
        }

        for (Long propertyId : propertyIds) {
            Property property = propertyRepo.findById(propertyId)
                    .orElseThrow(() -> new ObjectNotFoundException("Property with id=" + propertyId + " not found"));
            hotel.addProperty(property);
        }

        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }

    @Transactional
    public HotelDTO updateSights(Long id, List<Long> sightIds, Localization localization) {
        Hotel hotel = findById(id);
        for (Sight sight : hotel.getSights().stream().toList()) {
            hotel.removeSight(sight);
        }

        for (Long sightId : sightIds) {
            Sight sight = sightRepo.findById(sightId)
                    .orElseThrow(() -> new ObjectNotFoundException("Sight with id=" + sightId + " not found"));
            hotel.removeSight(sight);
        }

        hotelRepo.saveAndFlush(hotel);
        return new HotelDTO(hotel, localization);
    }


}
