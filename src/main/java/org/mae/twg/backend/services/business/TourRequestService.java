package org.mae.twg.backend.services.business;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.business.TourRequestDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.business.TourRequest;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.repositories.business.TourRequestRepo;
import org.mae.twg.backend.repositories.travel.TourRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public TourRequestDTO addRequest(TourRequestDTO tourRequestDTO) {
        Tour tour = tourRepo.findById(tourRequestDTO.getTour())
                .orElseThrow(() -> new ObjectNotFoundException("Tour with id=" + tourRequestDTO.getTour() + " not found"));
        TourRequest tourRequest = new TourRequest(tour, tourRequestDTO.getAdults(), tourRequestDTO.getChildren(), tourRequestDTO.getTransferNotes());
        tourRequestRepo.saveAndFlush(tourRequest);
        return new TourRequestDTO(tourRequest);
    }

    public List<TourRequestDTO> getAll() {
        List<TourRequest> tourRequests = tourRequestRepo.findByClosedAtIsNull();
        List<TourRequestDTO> tourRequestDTOs = new ArrayList<>();

        for (TourRequest tourRequest : tourRequests) {
            tourRequestDTOs.add(new TourRequestDTO(tourRequest));
        }

        return tourRequestDTOs;
    }

    public List<TourRequestDTO> resolve(Long request_id) {
        TourRequest tourRequest = findById(request_id);
        tourRequest.setClosedAt(LocalDateTime.now());
        tourRequestRepo.saveAndFlush(tourRequest);
        return getAll();
    }
}
