package com.learn.domain.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {

    @JsonProperty(value = "total")
    private Long total;

    @JsonProperty(value = "current_page")
    private Long currentPage;

    @JsonProperty(value = "pages")
    private Long pages;
}
