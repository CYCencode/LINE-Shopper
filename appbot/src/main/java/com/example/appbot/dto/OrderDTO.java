package com.example.appbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Integer id;
    private String orderNo;
    private String lineUserId;
    private String lineUsername;
    private Integer orderStatus;
    private Integer total;
    private ZonedDateTime createAt;
    private ZonedDateTime lastModifiedAt;
}
