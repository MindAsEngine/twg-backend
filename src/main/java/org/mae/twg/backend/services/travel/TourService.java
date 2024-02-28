package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.TourDTO;
import org.mae.twg.backend.dto.travel.request.TourLocalRequestDTO;
import org.mae.twg.backend.dto.travel.request.TourRequestDTO;
import org.mae.twg.backend.dto.travel.request.TourUpdateDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.business.Agency;
import org.mae.twg.backend.models.travel.*;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.repositories.business.AgencyRepo;
import org.mae.twg.backend.repositories.travel.*;
import org.mae.twg.backend.repositories.travel.localization.TourLocalRepo;
import org.mae.twg.backend.utils.SlugUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TourService {
    private final TourRepo tourRepo;
    private final TourLocalRepo localRepo;
    private final HotelRepo hotelRepo;
    private final ResortRepo resortRepo;
    private final CountryRepo countryRepo;
    private final AgencyRepo agencyRepo;
    private final SlugUtils slugUtils;

    private Tour findById(Long id) {
        Tour tour = tourRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Tour with id=" + id + " not found"));
        if (tour.getIsDeleted()) {
            throw new ObjectNotFoundException("Tour with id=" + id + " marked as deleted");
        }
        return tour;
    }

    public List<TourDTO> getAll(Localization localization) {
        List<Tour> tours = tourRepo.findAll();
        List<TourDTO> hotelDTOs = tours.stream()
                .filter(tour -> !tour.getIsDeleted())
                .filter(tour -> tour.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(tour -> new TourDTO(tour, localization))
                .toList();
        if (hotelDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Tours with " + localization + " with localization not found");
        }
        return hotelDTOs;
    }

    public TourDTO getById(Long id, Localization localization) {
        return new TourDTO(findById(id), localization);
    }

    @Transactional
    public void deleteById(Long id) {
        Tour tour = findById(id);
        tour.setIsDeleted(true);
        tourRepo.save(tour);
    }

    @Transactional
    public TourDTO create(TourRequestDTO tourDTO, Localization localization) {
        Tour tour = new Tour();
        tour.setIsActive(tourDTO.getIsActive());
        tour.setIsCustom(tourDTO.getIsCustom());
        tour.setIsBurning(tourDTO.getIsBurning());
        tour.setStartDate(tourDTO.getStartDate());
        tour.setEndDate(tourDTO.getEndDate());
        tour.setType(tourDTO.getType());
        Agency agency = agencyRepo.findById(tourDTO.getAgencyId())
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Agency with id=" + tourDTO.getAgencyId() + " not found"));
        tour.setAgency(agency);
        Country country = countryRepo.findById(tourDTO.getCountryId())
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Country with id=" + tourDTO.getCountryId() + " not found"));
        tour.setCountry(country);
        tourRepo.saveAndFlush(tour);

        for (Long id : tourDTO.getHotelIds()) {
            Hotel hotel = hotelRepo.findById(id)
                    .orElseThrow(() -> new ObjectNotFoundException("Hotel with id=" + id + " not found"));
            tour.addHotel(hotel);
        }

        for (Long id : tourDTO.getResortIds()) {
            Resort resort = resortRepo.findById(id)
                    .orElseThrow(() -> new ObjectNotFoundException("Resort with id=" + id + " not found"));
            tour.addResort(resort);
        }

        tourRepo.saveAndFlush(tour);

        TourLocal local = new TourLocal(tourDTO.getTitle(),
                tourDTO.getDescription(),
                tour, localization);
        localRepo.saveAndFlush(local);
        tour.addLocal(local);

        tour.setSlug(slugUtils.getSlug(tour));
        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO addLocal(Long id, TourLocalRequestDTO tourDTO, Localization localization) {
        Tour tour = findById(id);
        boolean isExists = tour.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for tour with id=" + id + " already exists");
        }

        TourLocal tourLocal =
                new TourLocal(tourDTO.getTitle(),
                        tourDTO.getDescription(),
                        tour, localization);
        tourLocal = localRepo.saveAndFlush(tourLocal);
        tour.addLocal(tourLocal);

        tour.setSlug(slugUtils.getSlug(tour));
        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO updateLocal(Long id, TourLocalRequestDTO tourDTO, Localization localization) {
        Tour tour = findById(id);
        TourLocal cur_local = tour.getLocals().stream()
                .filter(local -> local.getLocalization() == localization)
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException(
                        localization + " localization for tour with id=" + id + " not found"));

        cur_local.setTitle(tourDTO.getTitle());
        cur_local.setDescription(tourDTO.getDescription());
        localRepo.saveAndFlush(cur_local);

        tour.setSlug(slugUtils.getSlug(tour));
        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO updateResorts(Long id, List<Long> resortIds, Localization localization) {
        Tour tour = findById(id);
        for (Resort resort : tour.getResorts().stream().toList()) {
            tour.removeResort(resort);
        }

        for (Long resortId : resortIds) {
            Resort resort = resortRepo.findById(resortId)
                    .orElseThrow(() -> new ObjectNotFoundException("Resort with id=" + resortId + " not found"));
            tour.addResort(resort);
        }

        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO updateHotels(Long id, List<Long> hotelIds, Localization localization) {
        Tour tour = findById(id);
        for (Resort resort : tour.getResorts().stream().toList()) {
            tour.removeResort(resort);
        }

        for (Long hotelId : hotelIds) {
            Hotel hotel = hotelRepo.findById(hotelId)
                    .orElseThrow(() -> new ObjectNotFoundException("Hotel with id=" + hotelId + " not found"));
            tour.addHotel(hotel);
        }

        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, localization);
    }

    @Transactional
    public TourDTO update(Long id, TourUpdateDTO tourDTO, Localization localization) {
        Tour tour = findById(id);
        if (tourDTO.getCountryId() != null) {
            Country country = countryRepo.findById(tourDTO.getCountryId())
                    .orElseThrow(() -> new ObjectNotFoundException(
                            "Country with id=" + tourDTO.getCountryId() + " not found"));
            tour.setCountry(country);
        } else {
            tour.setCountry(null);
        }
        if (tourDTO.getAgencyId() != null) {
            Agency agency = agencyRepo.findById(tourDTO.getAgencyId())
                    .orElseThrow(() -> new ObjectNotFoundException(
                            "Agency with id=" + tourDTO.getAgencyId() + " not found"));
            tour.setAgency(agency);
        } else {
            tour.setAgency(null);
        }
        tour.setType(tourDTO.getType());
        tour.setIsBurning(tourDTO.getIsBurning());
        tour.setIsActive(tourDTO.getIsActive());
        tour.setIsCustom(tourDTO.getIsCustom());
        tour.setStartDate(tourDTO.getStartDate());
        tour.setEndDate(tourDTO.getEndDate());
        tourRepo.saveAndFlush(tour);
        return new TourDTO(tour, localization);
    }
}
