package org.mae.twg.backend.services.business;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.business.TourReqResponseDTO;
import org.mae.twg.backend.dto.business.TourRequestDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.business.Agency;
import org.mae.twg.backend.models.business.TourRequest;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.business.TourRequestRepo;
import org.mae.twg.backend.services.travel.TourService;
import org.mae.twg.backend.utils.BotUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TourRequestService {
    private final TourRequestRepo tourRequestRepo;
    private final TourService tourService;
    private final AgencyService agencyService;
    private final BotUtils botUtils;

    private TourRequest findById(Long id) {
        TourRequest tourRequest = tourRequestRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("TourRequest with id=" + id + " not found"));
        return tourRequest;
    }
    private List<TourReqResponseDTO> modelsToDTOs(Stream<TourRequest> tourRequests, Localization localization) {
        return tourRequests
                .map(tourRequest -> new TourReqResponseDTO(tourRequest, localization))
                .collect(Collectors.toList());
    }
    @Transactional
    public TourReqResponseDTO addRequest(TourRequestDTO tourRequestDTO, Localization localization) {
        Tour tour = tourService.findById(tourRequestDTO.getTourId());
        Agency agency = agencyService.findById(tourRequestDTO.getAgencyId());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TourRequest tourRequest = new TourRequest(user, agency, tour, tourRequestDTO.getAdults(), tourRequestDTO.getChildren(), tourRequestDTO.getTransferNotes());
        tourRequestRepo.saveAndFlush(tourRequest);

        botUtils.sendNotifications();
        return new TourReqResponseDTO(tourRequest, localization);
    }

    public List<TourReqResponseDTO> getAll(Long id, Localization localization) {
        List<TourRequest> tourRequests = tourRequestRepo.findByAgency_IdAndClosedAtIsNull(id);
        if (id == null) {
            tourRequests = tourRequestRepo.findByClosedAtIsNull();
        }
        return modelsToDTOs(tourRequests.stream(), localization);
    }

    public List<TourReqResponseDTO> resolve(Long request_id, Localization localization) {
        TourRequest tourRequest = findById(request_id);
        tourRequest.setClosedAt(LocalDateTime.now());
        tourRequestRepo.saveAndFlush(tourRequest);
        return getAll(null, localization);
    }


}
