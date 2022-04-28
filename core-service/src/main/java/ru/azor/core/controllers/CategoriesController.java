package ru.azor.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.azor.api.core.CategoryDto;
import ru.azor.core.converters.ProductConverter;
import ru.azor.core.services.CategoriesService;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Продукты", description = "Методы работы с продуктами")
public class CategoriesController {

    private final CategoriesService categoriesService;
    private final ProductConverter productConverter;

    @Operation(
            summary = "Запрос на получение списка всех категорий",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Set.class))
                    )
            }
    )
    @GetMapping
    public Set<CategoryDto> getAllProducts(){
        return productConverter.setCategoryToSetCategoryDto(categoriesService.findAllCategories());
    }
}
