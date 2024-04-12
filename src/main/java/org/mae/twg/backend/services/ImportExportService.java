package org.mae.twg.backend.services;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.TourLocal;
import org.mae.twg.backend.repositories.travel.TourRepo;
import org.mae.twg.backend.utils.csv.CSVUtils;
import org.mae.twg.backend.utils.csv.TourRow;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImportExportService {
    private final TourRepo tourRepo;
    private final CSVUtils csvUtils;

    private String getName(Model model) {
        if (model == null) {
            return null;
        }
        List<? extends Local> locals = model.getLocalizations();
        List<Localization> localizations = locals.stream().map(Local::getLocalization).toList();
        if (localizations.contains(Localization.RU)) {
            return locals.get(localizations.indexOf(Localization.RU)).getString();
        }
        if (localizations.contains(Localization.EN)) {
            return locals.get(localizations.indexOf(Localization.EN)).getString();
        }
        if (localizations.contains(Localization.UZ)) {
            return locals.get(localizations.indexOf(Localization.UZ)).getString();
        }
        return null;
    }

    private String buildListString(Set<? extends Model> modelList) {
        if (modelList.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Model model : modelList) {
            builder.append(getName(model)).append(", ");
        }
        builder.delete(builder.length() - 3, builder.length() - 1);
        return builder.toString();
    }

    private TourRow tourToRow(Tour tour) {
        TourRow.TourRowBuilder builder = TourRow.builder();
        builder
//                Set logic fields
                .price(tour.getPrice())
                .tourType(tour.getType() == null ? "Undefined" : tour.getType().name())
                .duration(tour.getDuration())
//                Set single model names
                .countryName(getName(tour.getCountry()))
                .hospitalName(getName(tour.getHospital()))
//                Set list model names
                .hotelList(buildListString(tour.getHotels()))
                .tagList(buildListString(tour.getTags()));
//
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
        return builder.build();
    }

    private Tour rowToTour(TourRow row) {
        return null;
    }

    @Transactional
    public void loadToursFromCSV(MultipartFile file) {
        List<TourRow> tourRows = csvUtils.parseTourCSV(file);
        List<Tour> tours = tourRows.stream()
                .map(this::rowToTour)
                .toList();
        tourRepo.saveAll(tours);
    }

    public InputStreamResource loadToursToCSV() {
        List<TourRow> tourRows = tourRepo.findAll().stream()
                .filter(tour -> !tour.getIsDeleted())
                .map(this::tourToRow).toList();
        return csvUtils.convertToursToCSV(tourRows);
    }
}
