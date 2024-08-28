package com.example.appbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private Integer id;
    private Integer lineUserId;
    private Integer orderStatus;
    private Integer total;
    private LocalDateTime createAt;
    private LocalDateTime lastModifiedAt;
}
