package org.mae.twg.backend.services;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.*;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.enums.Stars;
import org.mae.twg.backend.models.travel.enums.TourType;
import org.mae.twg.backend.models.travel.localization.HotelLocal;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.repositories.travel.*;
import org.mae.twg.backend.repositories.travel.localization.HospitalLocalRepo;
import org.mae.twg.backend.repositories.travel.localization.HotelLocalRepo;
import org.mae.twg.backend.repositories.travel.localization.SightLocalRepo;
import org.mae.twg.backend.repositories.travel.localization.TourLocalRepo;
import org.mae.twg.backend.utils.SlugUtils;
import org.mae.twg.backend.utils.excel.*;
import org.mae.twg.backend.utils.excel.models.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImportExportService {
    private final TourRepo tourRepo;
    private final TourLocalRepo tourLocalRepo;
    private final HotelRepo hotelRepo;
    private final HotelLocalRepo hotelLocalRepo;
    private final HospitalRepo hospitalRepo;
    private final HospitalLocalRepo hospitalLocalRepo;
    private final SightRepo sightRepo;
    private final SightLocalRepo sightLocalRepo;
    private final ExcelUtils excelUtils;
    private final SlugUtils slugUtils;
    private String getName(Model model) {
        log.debug("Start ImportExportService.getName");
        if (model == null) {
            return null;
        }
        List<? extends Local> locals = model.getLocalizations();
        List<Localization> localizations = locals.stream().map(Local::getLocalization).toList();
        if (localizations.contains(Localization.RU)) {
            log.debug("End ImportExportService.getName");
            return locals.get(localizations.indexOf(Localization.RU)).getString();
        }
        if (localizations.contains(Localization.EN)) {
            log.debug("End ImportExportService.getName");
            return locals.get(localizations.indexOf(Localization.EN)).getString();
        }
        if (localizations.contains(Localization.UZ)) {
            log.debug("End ImportExportService.getName");
            return locals.get(localizations.indexOf(Localization.UZ)).getString();
        }
        log.debug("End ImportExportService.getName");
        return null;
    }

    private TourRow tourToRow(Tour tour) {
        log.debug("Start ImportExportService.tourToRow");
        TourRow.TourRowBuilder builder = TourRow.builder();
        builder
                .price(tour.getPrice())
                .tourType(tour.getType() == null ? "Undefined" : tour.getType().name())
                .duration(tour.getDuration());
        List<TourLocal> locals = tour.getLocals();
        for (TourLocal local : locals) {
            if (local.getIntroduction() == null) {
                local.setIntroduction("");
            }
            if (local.getDescription() == null) {
                local.setDescription("");
            }
            if (local.getAdditional() == null) {
                local.setAdditional("");
            }
            if (local.getLocalization() == Localization.RU) {
                builder
                        .titleRU(local.getTitle())
                        .introductionRU(local.getIntroduction())
                        .descriptionRU(local.getDescription())
                        .additionalRU(local.getAdditional());
            }
            if (local.getLocalization() == Localization.EN) {
                builder
                        .titleEN(local.getTitle())
                        .introductionEN(local.getIntroduction())
                        .descriptionEN(local.getDescription())
                        .additionalEN(local.getAdditional());
            }
            if (local.getLocalization() == Localization.UZ) {
                builder
                        .titleUZ(local.getTitle())
                        .introductionUZ(local.getIntroduction())
                        .descriptionUZ(local.getDescription())
                        .additionalUZ(local.getAdditional());
            }
        }
        log.debug("End ImportExportService.tourToRow");
        return builder.build();
    }

    private HotelRow hotelToRow(Hotel hotel) {
        log.debug("Start ImportExportService.hotelToRow");
        HotelRow.HotelRowBuilder builder = HotelRow.builder();
        builder
                .stars(hotel.getStars().name())
                .longitude(hotel.getLongitude())
                .latitude(hotel.getLatitude());
        List<HotelLocal> locals = hotel.getLocals();
        for (HotelLocal local : locals) {
            if (local.getIntroduction() == null) {
                local.setIntroduction("");
            }
            if (local.getDescription() == null) {
                local.setDescription("");
            }
            if (local.getAddress() == null) {
                local.setAddress("");
            }
            if (local.getCity() == null) {
                local.setCity("");
            }
            if (local.getLocalization() == Localization.RU) {
                builder
                        .nameRU(local.getName())
                        .introductionRU(local.getIntroduction())
                        .descriptionRU(local.getDescription())
                        .cityRU(local.getCity())
                        .addressRU(local.getAddress());
            }
            if (local.getLocalization() == Localization.EN) {
                builder
                        .nameEN(local.getName())
                        .introductionEN(local.getIntroduction())
                        .descriptionEN(local.getDescription())
                        .cityEN(local.getCity())
                        .addressEN(local.getAddress());
            }
            if (local.getLocalization() == Localization.UZ) {
                builder
                        .nameUZ(local.getName())
                        .introductionUZ(local.getIntroduction())
                        .descriptionUZ(local.getDescription())
                        .cityUZ(local.getCity())
                        .addressUZ(local.getAddress());
            }
        }
        log.debug("End ImportExportService.hotelToRow");
        return null;
    }

    private SightRow sightToRow(Sight sight) {
        return null;
    }

    private HospitalRow hospitalToRow(Hospital hospital) {
        return null;
    }

    private Hotel rowToHotel(HotelRow row) {
        log.debug("Start ImportExportService.rowToHotel");
        Hotel hotel = new Hotel();
        hotelRepo.save(hotel);
        try {
            hotel.setStars(Stars.valueOf(row.getStars()));
        } catch (Exception e) {
            log.error("Could not parse hotel stars: " + row.getStars());
            throw new ValidationException("Could not parse hotel stars: " + row.getStars());
        }

        List<HotelLocal> locals = new ArrayList<>();
        if (row.getNameRU() != null) {
            locals.add(new HotelLocal(
                    row.getNameRU(),
                    row.getCityRU(),
                    row.getIntroductionRU(),
                    row.getDescriptionRU(),
                    row.getAddressRU(),
                    Localization.RU));
        }
        if (row.getNameEN() != null) {
            locals.add(new HotelLocal(
                    row.getNameEN(),
                    row.getCityEN(),
                    row.getIntroductionEN(),
                    row.getDescriptionEN(),
                    row.getAddressEN(),
                    Localization.EN));
        }
        if (row.getNameUZ() != null) {
            locals.add(new HotelLocal(
                    row.getNameUZ(),
                    row.getCityUZ(),
                    row.getIntroductionUZ(),
                    row.getDescriptionUZ(),
                    row.getAddressUZ(),
                    Localization.UZ));
        }
        hotelLocalRepo.saveAll(locals);
        for (HotelLocal local : locals) {
            hotel.addLocal(local);
        }
        hotel.setSlug(slugUtils.getSlug(hotel));
        log.debug("End ImportExportService.rowToHotel");
        return hotel;
    }

    private Sight rowToSight(SightRow row) {
        return null;
    }

    private Hospital rowToHospital(HospitalRow row) {
        return null;
    }

    private Tour rowToTour(TourRow row) {
        log.debug("Start ImportExportService.rowToTour");
        Tour tour = new Tour();
        tourRepo.save(tour);
        tour.setDuration(row.getDuration());
        tour.setIsActive(false);
        tour.setPrice(row.getPrice());
        try {
            tour.setType(TourType.valueOf(row.getTourType()));
        } catch (Exception e) {
            log.error("Could not parse tour type: " + row.getTourType());
            throw new ValidationException("Could not parse tour type: " + row.getTourType());
        }

        List<TourLocal> locals = new ArrayList<>();
        if (row.getTitleRU() != null) {
            locals.add(new TourLocal(
                    row.getTitleRU(),
                    row.getIntroductionRU(),
                    row.getDescriptionRU(),
                    row.getAdditionalRU(),
                    Localization.RU));
        }
        if (row.getTitleEN() != null) {
            locals.add(new TourLocal(
                    row.getTitleEN(),
                    row.getIntroductionEN(),
                    row.getDescriptionEN(),
                    row.getAdditionalEN(),
                    Localization.EN));
        }
        if (row.getTitleUZ() != null) {
            locals.add(new TourLocal(
                    row.getTitleUZ(),
                    row.getIntroductionUZ(),
                    row.getDescriptionUZ(),
                    row.getAdditionalUZ(),
                    Localization.UZ));
        }
        tourLocalRepo.saveAll(locals);
        for (TourLocal local : locals) {
            tour.addLocal(local);
        }
        tour.setSlug(slugUtils.getSlug(tour));
        log.debug("End ImportExportService.rowToTour");
        return tour;
    }

    @Transactional
    public void loadToursFromExcel(MultipartFile file) throws IOException {
        log.debug("Start ImportExportService.loadToursFromExcel");
        List<TourRow> tourRows = excelUtils.parseTourExcel(file);
        List<Tour> tours = tourRows.stream()
                .map(this::rowToTour)
                .toList();
        tourRepo.saveAll(tours);
        log.debug("End ImportExportService.loadToursFromExcel");
    }

    @Transactional
    public void loadHotelsFromExcel(MultipartFile file) throws IOException {
        log.debug("Start ImportExportService.loadToursFromExcel");
        List<HotelRow> hotelRows = excelUtils.parseHotelExcel(file);
        List<Hotel> hotels = hotelRows.stream()
                .map(this::rowToHotel)
                .toList();
        //tourRepo.saveAll(tours);
        log.debug("End ImportExportService.loadToursFromExcel");
    }

    @Transactional
    public void loadHospitalsFromExcel(MultipartFile file) throws IOException {
        log.debug("Start ImportExportService.loadHospitalsFromExcel");
        List<HospitalRow> hospitalRows = excelUtils.parseHospitalExcel(file);
        List<Hospital> hospitals = hospitalRows.stream()
                .map(this::rowToHospital)
                .toList();
        log.debug("End ImportExportService.loadHospitalsFromExcel");
        //tourRepo.saveAll(tours);
    }

    @Transactional
    public void loadSightsFromExcel(MultipartFile file) throws IOException {
        log.debug("Start ImportExportService.loadSightsFromExcel");
        List<SightRow> sightRows = excelUtils.parseSightExcel(file);
        List<Sight> sights = sightRows.stream()
                .map(this::rowToSight)
                .toList();
        log.debug("End ImportExportService.loadSightsFromExcel");
        //tourRepo.saveAll(tours);
    }

    public String loadToursToExcel() throws IOException {
        log.debug("Start ImportExportService.loadToursToExcel");
        List<TourRow> tourRows = tourRepo.findAll().stream()
                .filter(tour -> !tour.getIsDeleted())
                .map(this::tourToRow).toList();
        log.debug("End ImportExportService.loadToursToExcel");
        return excelUtils.convertToursToExcel(tourRows);
    }

    public String loadHotelsToExcel() throws IOException {
        log.debug("Start ImportExportService.loadHotelsToExcel");
        List<HotelRow> hotelRows = hotelRepo.findAll().stream()
                .filter(hotel -> !hotel.getIsDeleted())
                .map(this::hotelToRow).toList();
        log.debug("End ImportExportService.loadHotelsToExcel");
        return excelUtils.convertHotelsToExcel(hotelRows);
    }

    public String loadHospitalsToExcel() throws IOException {
        log.debug("Start ImportExportService.loadHospitalsToExcel");
        List<HospitalRow> hospitalRows = hospitalRepo.findAll().stream()
                .filter(hospital -> !hospital.getIsDeleted())
                .map(this::hospitalToRow).toList();
        log.debug("End ImportExportService.loadHospitalsToExcel");
        return excelUtils.convertHospitalsToExcel(hospitalRows);
    }

    public String loadSightsToExcel() throws IOException {
        log.debug("Start ImportExportService.loadSightsToExcel");
        List<SightRow> sightRows = sightRepo.findAll().stream()
                .filter(sight -> !sight.getIsDeleted())
                .map(this::sightToRow).toList();
        log.debug("End ImportExportService.loadSightsToExcel");
        return excelUtils.convertSightsToExcel(sightRows);
    }
}
