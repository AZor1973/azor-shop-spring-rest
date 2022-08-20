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
    private static final long serialVersionUID = -5488232298642258760L;
    @Schema(description = "ID продукта", required = true, example = "1")
    private Long id;
    @NotBlank(message = "Поле названия продукта не должно быть пустым")
    @Size(min = 5, message = "Название продукта должно быть не короче 5 символов")
    @Schema(description = "Название продукта", required = true, maxLength = 255, minLength = 3, example = "Коробка конфет")
    private String title;
    @NotNull(message = "Поле цены продукта не должно быть пустым")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена продукта не должна быть 0.0")
    @Digits(integer = 10, fraction = 2, message = "Поле цены продукта не должно быть пустым")
    @Schema(description = "Цена продукта", required = true, example = "120.00")
    private BigDecimal price;
    @NotEmpty(message = "Должна быть выбрана хотя бы одна категория")
    @Schema(description = "Категории продукта", required = true, example = "{Молочные продукты, Скоропортящиеся товары}")
    private Set<CategoryDto> categories;
}
