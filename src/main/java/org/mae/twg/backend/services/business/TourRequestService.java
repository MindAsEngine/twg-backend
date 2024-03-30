package org.mae.twg.backend.services.business;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.business.TourReqResponseDTO;
import org.mae.twg.backend.dto.business.TourRequestDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.business.TourRequest;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.business.TourRequestRepo;
import org.mae.twg.backend.repositories.travel.TourRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourRequestService {
    private final TourRequestRepo tourRequestRepo;
    private final TourRepo tourRepo;
    private TourRequest findById(Long id) {
        TourRequest tourRequest = tourRequestRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("TourRequest with id=" + id + " not found"));
        return tourRequest;
    }
    @Transactional
    public TourReqResponseDTO addRequest(TourRequestDTO tourRequestDTO, Localization localization) {
        Tour tour = tourRepo.findById(tourRequestDTO.getTour())
                .orElseThrow(() -> new ObjectNotFoundException("Tour with id=" + tourRequestDTO.getTour() + " not found"));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TourRequest tourRequest = new TourRequest(user, tour, tourRequestDTO.getAdults(), tourRequestDTO.getChildren(), tourRequestDTO.getTransferNotes());
        tourRequestRepo.saveAndFlush(tourRequest);
        return new TourReqResponseDTO(tourRequest, localization);
    }

    public List<TourReqResponseDTO> getAll(Localization localization) {
        List<TourRequest> tourRequests = tourRequestRepo.findByClosedAtIsNull();
        return tourRequests.stream()
                .map(tourRequest -> new TourReqResponseDTO(tourRequest, localization))
                .collect(Collectors.toList());
    }

    public List<TourReqResponseDTO> resolve(Long request_id, Localization localization) {
        TourRequest tourRequest = findById(request_id);
        tourRequest.setClosedAt(LocalDateTime.now());
        tourRequestRepo.saveAndFlush(tourRequest);
        return getAll(localization);
    }
}
