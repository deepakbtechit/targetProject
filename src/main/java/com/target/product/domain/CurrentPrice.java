package com.target.targetProject.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentPrice {
    Double value;

    @JsonProperty("currency_code")
    String currencyCode;
}
