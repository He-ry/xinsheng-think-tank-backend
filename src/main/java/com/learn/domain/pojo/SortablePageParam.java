package com.learn.domain.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.learn.domain.pojo.SortingField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Schema(description="可排序的分页参数")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortablePageParam {

    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE_NONE = -1;
    @Schema(description="页码，从 1 开始", requiredMode=Schema.RequiredMode.REQUIRED, example="1")
    @NotNull(message="页码不能为空")
    @Min(value=1L, message="页码最小值为 1")
    private @NotNull(message="页码不能为空") @Min(value=1L, message="页码最小值为 1") Integer page = PAGE_NO;
    @Schema(description="每页条数，最大值为 100", requiredMode=Schema.RequiredMode.REQUIRED, example="10")
    @NotNull(message="每页条数不能为空")
    @Min(value=1L, message="每页条数最小值为 1")
    @Max(value=100L, message="每页条数最大值为 100")
    @JsonProperty(value="per_page")
    private @NotNull(message="每页条数不能为空") @Min(value=1L, message="每页条数最小值为 1") @Max(value=100L, message="每页条数最大值为 100") Integer perPage = PAGE_SIZE;
    @Schema(description="排序字段")
    private List<SortingField> sortingFields;
}
