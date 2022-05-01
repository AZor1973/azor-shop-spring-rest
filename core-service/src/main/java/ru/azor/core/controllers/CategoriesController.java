package ru.azor.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.core.CategoryDto;
import ru.azor.api.core.ProductDto;
import ru.azor.core.converters.ProductConverter;
import ru.azor.core.services.CategoriesService;

import javax.validation.Valid;
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

    @Operation(
            summary = "Создание категории",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<?> saveNewCategory(@RequestBody @Parameter(description = "Новая категория", required = true) @Valid CategoryDto categoryDto,
                                             @Parameter(description = "Ошибки валидации", required = true) BindingResult bindingResult){
        StringResponseRequestDto response = categoriesService.tryToSaveNewCategory(categoryDto,bindingResult);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
