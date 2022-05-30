package ru.azor.api.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель продукта")
public class ProductDto implements Serializable {
    private static final long serialVersionUID = 6472552774592433071L;

    @Schema(description = "ID продукта", required = true, example = "1")
    private Long id;
    @NotBlank(message = "Поле названия продукта не должно быть пустым")
    @Size(min = 5, message = "Название продукта должно быть не короче 5 символов")
    @Schema(description = "Название продукта", required = true, maxLength = 255, minLength = 3, example = "Коробка конфет")
    private String title;
    @NotNull(message = "Поле цены продукта не должно быть пустым")
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=10, fraction=2)
    @Schema(description = "Цена продукта", required = true, example = "120.00")
    private BigDecimal price;
    @Schema(description = "Категории продукта", required = true, example = "{Молочные продукты, Скоропортящиеся товары}")
    private Set<CategoryDto> categories;
}
