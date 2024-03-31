package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.request.locals.TagLocalDTO;
import org.mae.twg.backend.dto.travel.request.logic.TagLogicDTO;
import org.mae.twg.backend.dto.travel.response.TagDTO;
import org.mae.twg.backend.exceptions.ObjectAlreadyExistsException;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Tag;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.TagLocal;
import org.mae.twg.backend.repositories.travel.TagRepo;
import org.mae.twg.backend.repositories.travel.localization.TagLocalRepo;
import org.mae.twg.backend.services.TravelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class TagService implements TravelService<TagDTO, TagLocalDTO> {
    private final TagRepo tagRepo;
    private final TagLocalRepo localRepo;

    public Tag findById(Long id) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Tag with id=" + id + " not found"));
        if (tag.getIsDeleted()) {
            throw new ObjectNotFoundException("Tag with id=" + id + " marked as deleted");
        }
        return tag;
    }

    private List<TagDTO> modelsToDTOs(Stream<Tag> resorts, Localization localization) {
        List<TagDTO> tagDTOs = resorts
                .filter(tag -> !tag.getIsDeleted())
                .filter(tag -> tag.getLocalizations().stream().anyMatch(local -> local.getLocalization() == localization))
                .map(tag -> new TagDTO(tag, localization))
                .toList();
        if (tagDTOs.isEmpty()) {
            throw new ObjectNotFoundException("Tag with " + localization + " not found");
        }
        return tagDTOs;
    }

    public List<TagDTO> getAll(Localization localization) {
        List<Tag> tags = tagRepo.findAll();
        return modelsToDTOs(tags.stream(), localization);
    }

    public List<TagDTO> getAllPaged(Localization localization, int page, int size) {
        Pageable tagPage = PageRequest.of(page, size);
        Page<Tag> tags = tagRepo.findAll(tagPage);
        return modelsToDTOs(tags.stream(), localization);
    }

    public TagDTO getById(Long id, Localization local) {
        return new TagDTO(findById(id), local);
    }

    @Transactional
    public void deleteById(Long id) {
        Tag tag = findById(id);
        tag.setIsDeleted(true);
        tagRepo.save(tag);
    }

    @Transactional
    public TagDTO create(TagLocalDTO tagDTO, Localization local) {
        Tag tag = new Tag();
        tagRepo.saveAndFlush(tag);
        TagLocal tagLocal =
                new TagLocal(tagDTO.getName(), local);
        tagLocal = localRepo.saveAndFlush(tagLocal);
        tag.addLocal(tagLocal);

        tagRepo.saveAndFlush(tag);
        return new TagDTO(tag, local);
    }

    @Transactional
    public TagDTO addLocal(Long id, TagLocalDTO tagDTO, Localization localization) {
        Tag tag = findById(id);
        boolean isExists = tag.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            throw new ObjectAlreadyExistsException(
                    localization + " localization for tag with id=" + id + " already exists");
        }

        TagLocal tagLocal =
                new TagLocal(tagDTO.getName(), localization);
        tagLocal = localRepo.saveAndFlush(tagLocal);
        tag.addLocal(tagLocal);

        tagRepo.saveAndFlush(tag);
        return new TagDTO(tag, localization);
    }

    @Transactional
    public TagDTO updateLocal(Long id, TagLocalDTO tagDTO, Localization localization) {
        Tag tag = findById(id);
        TagLocal curLocal =
                tag.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException(
                                localization + " localization for tag with id=" + id + "not found"));
        curLocal.setName(tagDTO.getName());
        localRepo.saveAndFlush(curLocal);

        tagRepo.saveAndFlush(tag);
        return new TagDTO(tag, localization);
    }

    @Transactional
    public TagDTO updateLogic(Long id, TagLogicDTO tagDTO, Localization localization) {
        Tag tag = findById(id);
        tag.setIcon(tagDTO.getIcon());
        tagRepo.saveAndFlush(tag);
        return new TagDTO(tag, localization);
    }


}
