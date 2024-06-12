package org.mae.twg.backend.services.travel;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.PageDTO;
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

@Service
@RequiredArgsConstructor
@Log4j2
public class TagService implements TravelService<TagDTO, TagLocalDTO> {
    private final TagRepo tagRepo;
    private final TagLocalRepo localRepo;

    public Tag findById(Long id) {
        log.debug("Start TagService.findById");
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Tag with id=" + id + " not found");
                    return new ObjectNotFoundException("Tag with id=" + id + " not found");
                });
        if (tag.getIsDeleted()) {
            log.error("Tag with id=" + id + " marked as deleted");
            throw new ObjectNotFoundException("Tag with id=" + id + " marked as deleted");
        }
        log.debug("End TagService.findById");
        return tag;
    }

    private PageDTO<TagDTO> modelsToDTOs(PageDTO<Tag> tags, Localization localization) {
        log.debug("Start TagService.modelsToDTOs");
        if (tags.isEmpty()) {
            log.error("Tag with " + localization + " not found");
            throw new ObjectNotFoundException("Tag with " + localization + " not found");
        }
        PageDTO<TagDTO> tagDTOs = tags
                .apply(tag -> new TagDTO(tag, localization));
        log.debug("End TagService.modelsToDTOs");
        return tagDTOs;
    }

//    public List<TagDTO> getAll(Localization localization) {
//        log.debug("Start TagService.getAll");
//        List<Tag> tags = tagRepo.findAll();
//        log.debug("End TagService.getAll");
//        return modelsToDTOs(tags.stream(), localization);
//    }

    public PageDTO<TagDTO> getAllPaged(Localization localization, Integer page, Integer size) {
        log.debug("Start TagService.getAllPaged");
        Pageable tagPage = null;
        if (page != null && size != null) {
            tagPage = PageRequest.of(page, size);
        }
        Page<Tag> tags = tagRepo.findAllByIsDeletedFalse(tagPage);
        log.debug("End TagService.getAllPaged");
        return modelsToDTOs(new PageDTO<>(tags), localization);
    }

    public TagDTO getById(Long id, Localization local) {
        log.debug("Start TagService.getById");
        return new TagDTO(findById(id), local);
    }

    @Transactional
    public void deleteById(Long id) {
        log.debug("Start TagService.deleteById");
        Tag tag = findById(id);
        tag.setIsDeleted(true);
        tagRepo.save(tag);
        log.debug("End TagService.deleteById");
    }

    @Transactional
    public TagDTO create(TagLocalDTO tagDTO, Localization local) {
        log.debug("Start TagService.create");
        Tag tag = new Tag();
        tagRepo.saveAndFlush(tag);
        TagLocal tagLocal =
                new TagLocal(tagDTO.getName(), local);
        tagLocal = localRepo.saveAndFlush(tagLocal);
        tag.addLocal(tagLocal);

        tagRepo.saveAndFlush(tag);
        log.debug("End TagService.create");
        return new TagDTO(tag, local);
    }

    @Transactional
    public TagDTO addLocal(Long id, TagLocalDTO tagDTO, Localization localization) {
        log.debug("Start TagService.addLocal");
        Tag tag = findById(id);
        boolean isExists = tag.getLocalizations().stream()
                .anyMatch(local -> local.getLocalization() == localization);
        if (isExists) {
            log.error(localization + " localization for tag with id=" + id + " already exists");
            throw new ObjectAlreadyExistsException(
                    localization + " localization for tag with id=" + id + " already exists");
        }

        TagLocal tagLocal =
                new TagLocal(tagDTO.getName(), localization);
        tagLocal = localRepo.saveAndFlush(tagLocal);
        tag.addLocal(tagLocal);

        tagRepo.saveAndFlush(tag);
        log.debug("End TagService.addLocal");
        return new TagDTO(tag, localization);
    }

    @Transactional
    public TagDTO updateLocal(Long id, TagLocalDTO tagDTO, Localization localization) {
        log.debug("Start TagService.updateLocal");
        Tag tag = findById(id);
        TagLocal curLocal =
                tag.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> {
                            log.error(localization + " localization for tag with id=" + id + "not found");
                            return new ObjectNotFoundException(
                                    localization + " localization for tag with id=" + id + "not found");
                        });
        curLocal.setName(tagDTO.getName());
        localRepo.saveAndFlush(curLocal);

        tagRepo.saveAndFlush(tag);
        log.debug("End TagService.updateLocal");
        return new TagDTO(tag, localization);
    }

    @Transactional
    public TagDTO updateLogic(Long id, TagLogicDTO tagDTO, Localization localization) {
        log.debug("Start TagService.updateLogic");
        Tag tag = findById(id);
        tag.setIcon(tagDTO.getIcon());
        tagRepo.saveAndFlush(tag);
        log.debug("End TagService.updateLogic");
        return new TagDTO(tag, localization);
    }


}
