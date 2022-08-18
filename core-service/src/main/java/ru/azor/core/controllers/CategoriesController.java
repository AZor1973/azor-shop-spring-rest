package ru.azor.core.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.azor.api.core.CategoryDto;
import ru.azor.api.exceptions.AppError;
import ru.azor.api.exceptions.ClientException;
import ru.azor.core.converters.ProductConverter;
import ru.azor.core.entities.Category;
import ru.azor.core.services.CategoriesService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Категории", description = "Методы работы с категориями")
public class CategoriesController {

    private final CategoriesService categoriesService;
    private final ProductConverter productConverter;

    @Operation(
            summary = "Запрос на получение страницы категорий",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Page.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/page")
    public Page<CategoryDto> getPage(
            @RequestParam(name = "p", defaultValue = "1") Integer page,
            @RequestParam(name = "page_size", defaultValue = "9") Integer pageSize) {
        if (page < 1) {
            page = 1;
        }
        return categoriesService.getPage(page, pageSize).map(
                productConverter::categoryToCategoryDto
        );
    }

    @Operation(
            summary = "Запрос на получение списка категорий",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = List.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping
    public List<CategoryDto> getAll() {
        return categoriesService.findAll().stream().map(
                productConverter::categoryToCategoryDto
        ).collect(Collectors.toList());
    }

    @Operation(
            summary = "Создание категории",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ResponseEntity.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Parameter(description = "Новая категория", required = true) @Valid CategoryDto categoryDto,
                                  @Parameter(description = "Ошибки валидации", required = true) BindingResult bindingResult) {
        Category category = categoriesService.tryToSave(categoryDto, bindingResult);
        return new ResponseEntity<>(productConverter.categoryToCategoryDto(category), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Запрос на получение категории по id",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = CategoryDto.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public CategoryDto getById(
            @PathVariable @Parameter(description = "Идентификатор категории", required = true) Long id
    ) {
        Category category = categoriesService.findById(id).orElseThrow(() -> new ClientException("Категория не найдена, id: " + id, HttpStatus.NOT_FOUND));
        return productConverter.categoryToCategoryDto(category);
    }

    @Operation(
            summary = "Изменение категории",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = CategoryDto.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @PutMapping
    public CategoryDto update(@RequestBody @Parameter(description = "Изменённая категория", required = true) @Valid CategoryDto categoryDto,
                              @Parameter(description = "Ошибки валидации", required = true) BindingResult bindingResult) {
        Category category = categoriesService.tryToSave(categoryDto, bindingResult);
        return productConverter.categoryToCategoryDto(category);
    }

    @Operation(
            summary = "Удаление категории",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "4XX",
                            content = @Content(schema = @Schema(implementation = AppError.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @Parameter(description = "Идентификатор категории", required = true) Long id) {
        categoriesService.deleteById(id);
    }
}
