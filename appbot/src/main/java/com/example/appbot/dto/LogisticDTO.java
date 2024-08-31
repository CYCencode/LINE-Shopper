package com.example.appbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogisticDTO {
    private Integer id;
    private Integer orderId;
    private String orderNo;
    private String status;
    private String shipping;
    private String allPayLogisticId;
    private String bookingNote;
    private String receiverAddress;
    private String receiverCellPhone;
    private String receiverName;
    private String receiverZipcode;
    private String receiverEmail;
}
