package ru.azor.api.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель продукта")
public class ProductDto {
    @Schema(description = "ID продукта", required = true, example = "1")
    private Long id;

    @Schema(description = "Название продукта", required = true, maxLength = 255, minLength = 3, example = "Коробка конфет")
    private String title;

    @Schema(description = "Цена продукта", required = true, example = "120")
    private BigDecimal price;

    private Set<CategoryDto> categories;
}
