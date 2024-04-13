package org.mae.twg.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.repositories.travel.TourRepo;
import org.mae.twg.backend.utils.excel.ExcelUtils;
import org.mae.twg.backend.utils.excel.TourRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImportExportService {
    private final TourRepo tourRepo;
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

    public String loadToursToExcel() throws IOException {
        log.debug("Start ImportExportService.loadToursToExcel");
        List<TourRow> tourRows = tourRepo.findAll().stream()
                .filter(tour -> !tour.getIsDeleted())
                .map(this::tourToRow).toList();
        log.debug("End ImportExportService.loadToursToExcel");
        return excelUtils.convertToursToExcel(tourRows);
    }
}
