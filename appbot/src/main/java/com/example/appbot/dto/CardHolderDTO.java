package com.example.appbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardHolderDTO {
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String name;
    private String email;
    @JsonProperty("zip_code")
    private String zipCode;
    private String address;
    @JsonProperty("national_id")
    private String nationalId;
    @JsonProperty("member_id")
    private String memberId;
}