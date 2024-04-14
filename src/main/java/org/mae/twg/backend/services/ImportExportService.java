package org.mae.twg.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.*;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.repositories.travel.*;
import org.mae.twg.backend.utils.excel.*;
import org.mae.twg.backend.utils.excel.models.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImportExportService {
    private final TourRepo tourRepo;
    private final HotelRepo hotelRepo;
    private final HospitalRepo hospitalRepo;
    private final SightRepo sightRepo;
    private final ResortRepo resortRepo;
    private final ExcelUtils excelUtils;
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
                        .introductionRU(local.getIntroduction().replaceAll("\n", "|"))
                        .descriptionRU(local.getDescription().replaceAll("\n", "|"))
                        .additionalRU(local.getAdditional().replaceAll("\n", "|"));
            }
            if (local.getLocalization() == Localization.EN) {
                builder
                        .titleEN(local.getTitle())
                        .introductionEN(local.getIntroduction().replaceAll("\n", "|"))
                        .descriptionEN(local.getDescription().replaceAll("\n", "|"))
                        .additionalEN(local.getAdditional().replaceAll("\n", "|"));
            }
            if (local.getLocalization() == Localization.UZ) {
                builder
                        .titleUZ(local.getTitle())
                        .introductionUZ(local.getIntroduction().replaceAll("\n", "|"))
                        .descriptionUZ(local.getDescription().replaceAll("\n", "|"))
                        .additionalUZ(local.getAdditional().replaceAll("\n", "|"));
            }
        }
        log.debug("End ImportExportService.tourToRow");
        return builder.build();
    }

    private HotelRow hotelToRow(Hotel hotel) {
        return null;
    }

    private ResortRow resortToRow(Resort resort) {
        return null;
    }

    private SightRow sightToRow(Sight sight) {
        return null;
    }

    private HospitalRow hospitalToRow(Hospital hospital) {
        return null;
    }

    private Hotel rowToHotel(HotelRow row) {
        return null;
    }

    private Resort rowToResort(ResortRow row) {
        return null;
    }

    private Sight rowToSight(SightRow row) {
        return null;
    }

    private Hospital rowToHospital(HospitalRow row) {
        return null;
    }

    private Tour rowToTour(TourRow row) {
        log.debug("Start ImportExportService.rowToTour");
        log.debug("End ImportExportService.rowToTour");
        return null;
    }

    @Transactional
    public void loadToursFromExcel(MultipartFile file) throws IOException {
        log.debug("Start ImportExportService.loadToursFromExcel");
        List<TourRow> tourRows = excelUtils.parseTourExcel(file);
        List<Tour> tours = tourRows.stream()
                .map(this::rowToTour)
                .toList();
        log.debug("End ImportExportService.loadToursFromExcel");
        //tourRepo.saveAll(tours);
    }

    @Transactional
    public void loadHotelsFromExcel(MultipartFile file) throws IOException {
        log.debug("Start ImportExportService.loadToursFromExcel");
        List<HotelRow> hotelRows = excelUtils.parseHotelExcel(file);
        List<Hotel> hotels = hotelRows.stream()
                .map(this::rowToHotel)
                .toList();
        log.debug("End ImportExportService.loadToursFromExcel");
        //tourRepo.saveAll(tours);
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

    @Transactional
    public void loadResortsFromExcel(MultipartFile file) throws IOException {
        log.debug("Start ImportExportService.loadResortsFromExcel");
        List<ResortRow> resortRows = excelUtils.parseResortExcel(file);
        List<Resort> resorts = resortRows.stream()
                .map(this::rowToResort)
                .toList();
        log.debug("End ImportExportService.loadResortsFromExcel");
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

    public String loadResortsToExcel() throws IOException {
        log.debug("Start ImportExportService.loadResortsToExcel");
        List<ResortRow> resortRows = resortRepo.findAll().stream()
                .filter(resort -> !resort.getIsDeleted())
                .map(this::resortToRow).toList();
        log.debug("End ImportExportService.loadResortsToExcel");
        return excelUtils.convertResortsToExcel(resortRows);
    }
}
