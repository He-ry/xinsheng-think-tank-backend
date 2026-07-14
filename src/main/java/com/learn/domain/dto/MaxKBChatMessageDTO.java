package com.learn.domain.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaxKBChatMessageDTO {

    private String message;
    private boolean re_chat;
    private boolean stream;
    private Map<String, Object> form_data;
    private List<DocumentInfo> document_list;
    private List<Object> audio_list;
    private List<Object> image_list;
    private List<Object> video_list;
    private List<Object> other_list;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentInfo {
        private String name;
        private int percentage;
        private String status;
        private long size;
        private Map<String, Object> raw;
        private long uid;
        private String url;
        private String file_id;
    }
}
