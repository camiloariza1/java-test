package com.global.logic.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
public class PhoneDTO {
    private Long number;

    @JsonProperty("cityCode")
    private Integer cityCode;

    @JsonProperty("countryCode")
    private String countryCode;
}