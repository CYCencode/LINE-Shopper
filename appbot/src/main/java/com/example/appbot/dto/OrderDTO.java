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
    private Integer lineUserId;
    private Integer orderStatus;
    private Integer total;
    private ZonedDateTime createAt;
    private ZonedDateTime lastModifiedAt;
}
