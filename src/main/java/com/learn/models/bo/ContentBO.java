package com.learn.models.bo;

import com.learn.models.bo.SectionBO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Schema(description="词条内容信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentBO {

    @Schema(description="词条摘要")
    private String summary;
    @Valid
    @Schema(description="章节列表")
    private List<SectionBO> sections;
}
