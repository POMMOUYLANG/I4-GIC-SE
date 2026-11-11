package edu.itc.shop.api.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record PlaceOrder(
        @NotEmpty @Valid List<LineItem> lines
) {
    public record LineItem(
            @Positive long productId,
            @Min(1) int quantity
    ) {
    }
}
