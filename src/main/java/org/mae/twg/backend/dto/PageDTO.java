package org.mae.twg.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Data
@AllArgsConstructor
public class PageDTO<T> {
    private Integer number;
    private Integer size;
    private Integer totalPages;
    private Long totalElements;
    private List<T> content;

    public PageDTO(Page<T> page) {
        number = page.getNumber();
        size = page.getSize();
        totalPages = page.getTotalPages();
        totalElements = page.getTotalElements();
        content = page.getContent();
    }

    public <R> PageDTO<R> apply(Function<T, R> function) {
        return new PageDTO<>(
                number, size, totalPages, totalElements,
                content.stream().map(function).toList()
        );
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
