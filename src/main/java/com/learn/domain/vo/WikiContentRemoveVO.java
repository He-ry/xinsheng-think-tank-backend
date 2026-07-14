package com.learn.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WikiContentRemoveVO {

    @Schema(description="词条ids")
    private String ids;
    @Schema(description="相关词条id")
    private List<String> deleteCited;
}
