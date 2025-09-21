package com.sporty.f1bet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.domain.Page;

public class BetOptionsResponse {

    @JsonProperty("event")
    private final List<SessionResponse> sessionResponse;

    @JsonProperty("page")
    private final int page;

    @JsonProperty("size")
    private final int size;

    @JsonProperty("totalElements")
    private final long totalElements;

    @JsonProperty("totalPages")
    private final int totalPages;

    public BetOptionsResponse(Page<SessionResponse> pageData) {
        this.sessionResponse = pageData.getContent();
        this.page = pageData.getNumber();
        this.size = pageData.getSize();
        this.totalElements = pageData.getTotalElements();
        this.totalPages = pageData.getTotalPages();
    }

    public List<SessionResponse> getSessionResponse() {
        return sessionResponse;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
