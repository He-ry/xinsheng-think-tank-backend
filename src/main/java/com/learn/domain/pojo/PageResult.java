package com.learn.domain.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> {

    @Schema(description = "数据")
    private List<T> list;

    @Schema(description = "分页信息")
    private Pagination pagination;

    public PageResult(List<T> list, Long total, Long currentPage, Long pages) {
        this.list = list;
        this.pagination = new Pagination(total, currentPage, pages);
    }
}
