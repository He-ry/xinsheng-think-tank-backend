package com.learn.domain.vo.user;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XinShengUserInfo {

    private String xinShengUsername;
    private String xinShengPhoneNumber;
}
