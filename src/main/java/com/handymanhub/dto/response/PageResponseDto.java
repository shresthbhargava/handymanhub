package com.handymanhub.dto.response;

import org.springframework.data.domain.Page;
import java.util.List;

public class PageResponseDto<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    private PageResponseDto() {}

    public List<T> getContent()          { return content; }
    public int getPageNumber()           { return pageNumber; }
    public int getPageSize()             { return pageSize; }
    public long getTotalElements()       { return totalElements; }
    public int getTotalPages()           { return totalPages; }
    public boolean isLast()              { return last; }

    // Static factory method — converts Spring's Page into our DTO
    public static <T> PageResponseDto<T> from(Page<T> page) {
        PageResponseDto<T> dto = new PageResponseDto<>();
        dto.content = page.getContent();
        dto.pageNumber = page.getNumber();
        dto.pageSize = page.getSize();
        dto.totalElements = page.getTotalElements();
        dto.totalPages = page.getTotalPages();
        dto.last = page.isLast();
        return dto;
    }
}