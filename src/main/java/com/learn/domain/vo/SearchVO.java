package com.learn.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchVO {

    @Schema(description = "搜索结果总数")
    private Long total;

    @Schema(description = "搜索结果列表")
    private List<SearchItemVO> search;

    @Schema(description = "分页信息")
    private PaginationVO pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchItemVO {
        private String title;
        private String canonicalUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationVO {
        private Integer pageNum;
        private Long total;
        private Boolean hasPrev;
        private Boolean hasNext;
    }
}
