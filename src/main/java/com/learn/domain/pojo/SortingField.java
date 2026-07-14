package com.learn.domain.pojo;

import java.io.Serializable;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortingField
implements Serializable {

    public static final String ORDER_ASC = "asc";
    public static final String ORDER_DESC = "desc";
    private String field;
    private String order;
}
