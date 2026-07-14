package com.learn.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.learn.models.bo.CitationBO;
import com.learn.models.bo.ContentBO;
import com.learn.models.bo.RelatedEntryBO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "百科词条保存入参")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WikiSaveDTO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "词条id")
    private String citation;

    @Valid
    @NotNull(message = "词条基础信息不能为空！")
    @Schema(description = "词条基础信息")
    private WikiEntryInfoDTO wikiEntryInfo;

    @Valid
    @NotNull(message = "词条内容不能为空！")
    @Schema(description = "词条内容")
    @JsonProperty(value = "content")
    private ContentBO wikiContent;

    @Valid
    @Schema(description = "引用来源列表")
    private List<CitationBO> citations;

    @Schema(description = "相关词条列表")
    private List<RelatedEntryBO> relatedEntries;
}
