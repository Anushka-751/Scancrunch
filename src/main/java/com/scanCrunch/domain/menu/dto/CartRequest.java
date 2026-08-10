package com.scanCrunch.domain.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartRequest {

    @NotNull
    private Long foodItemId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
