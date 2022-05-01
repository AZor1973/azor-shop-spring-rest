package ru.azor.api.core;

import com.sun.source.doctree.SerialDataTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель категории продукта")
public class CategoryDto implements Serializable {
    private static final long serialVersionUID = 1810048542525448858L;
    @Schema(description = "ID категории", required = true, example = "1")
    private Long id;
    @NotBlank(message = "Поле названия категории не должно быть пустым")
    @Size(min = 5, message = "Название категории должно быть не короче 5 символов")
    @Schema(description = "Название категории", required = true, example = "Молочные продукты")
    private String title;
}
