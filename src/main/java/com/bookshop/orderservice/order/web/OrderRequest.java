package com.bookshop.orderservice.order.web;


import javax.validation.constraints.*;

public record OrderRequest(
        @NotBlank(message = "ISBN must be specified")
        String isbn,

        @NotNull(message = "Quantity must be defined")
        @Min(value = 1, message = "Min = 1")
        @Max(value = 5, message = "Max = 5")
        Integer quantity
) {
        @Override
        public String toString() {
                return "OrderRequest{" +
                        "isbn='" + isbn + '\'' +
                        ", quantity=" + quantity +
                        '}';
        }
}
