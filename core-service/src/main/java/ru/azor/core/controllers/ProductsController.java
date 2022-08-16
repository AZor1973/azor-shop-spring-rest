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
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.core.CategoryDto;
import ru.azor.api.core.ProductDto;
import ru.azor.api.exceptions.ClientException;
import ru.azor.core.converters.ProductConverter;
import ru.azor.core.entities.Product;
import ru.azor.core.services.CategoriesService;
import ru.azor.core.services.ProductsService;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Продукты", description = "Методы работы с продуктами")
public class ProductsController {
    private final ProductsService productsService;
    private final CategoriesService categoriesService;
    private final ProductConverter productConverter;

    @Operation(
            summary = "Запрос на получение страницы продуктов",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Page.class))
                    )
            }
    )
    @GetMapping
    public Page<ProductDto> getAllProducts(
            @RequestParam(name = "p", defaultValue = "1") Integer page,
            @RequestParam(name = "min_price", required = false) Integer minPrice,
            @RequestParam(name = "max_price", required = false) Integer maxPrice,
            @RequestParam(name = "title_part", required = false) String titlePart,
            @RequestParam(name = "category_title", required = false) String categoryTitle,
            @RequestParam(name = "page_size", defaultValue = "9") Integer pageSize) {
        if (page < 1) {
            page = 1;
        }
        return productsService.searchProducts(minPrice, maxPrice, titlePart, categoryTitle, page, pageSize).map(
                productConverter::entityToDto
        );
    }

    @Operation(
            summary = "Запрос на получение продукта по id",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ProductDto.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "404",
                            content = @Content(schema = @Schema(implementation = CoreServiceAppError.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public ProductDto getProductById(
            @PathVariable @Parameter(description = "Идентификатор продукта", required = true) Long id
    ) {
        Product product = productsService.findById(id).orElseThrow(() -> new ClientException("Product not found, id: " + id));
        return productConverter.entityToDto(product);
    }

    @Operation(
            summary = "Создание нового продукта",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ProductDto.class))
                    )
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveNewProduct(@RequestBody @Parameter(description = "Новый продукт", required = true) @Valid ProductDto productDto,
                                            @Parameter(description = "Ошибки валидации", required = true) BindingResult bindingResult) {
        StringResponseRequestDto response = productsService.tryToSave(productDto, bindingResult);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    private void setCategoriesToProductDto(Set<CategoryDto> categories, ProductDto productDto) {
        productDto.setCategories(categories
                .stream().map(c -> categoriesService.findCategoryByTitle(c.getTitle()))
                .collect(Collectors.toSet()));
    }

    @Operation(
            summary = "Изменение продукта",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ProductDto.class))
                    ),
                    @ApiResponse(
                            description = "Ошибка", responseCode = "404",
                            content = @Content(schema = @Schema(implementation = CoreServiceAppError.class))
                    )
            }
    )
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateProduct(@RequestBody @Parameter(description = "Изменённый продукт", required = true) @Valid ProductDto productDto,
                                    @Parameter(description = "Ошибки валидации", required = true) BindingResult bindingResult) {
        setCategoriesToProductDto(productDto.getCategories(), productDto);
        StringResponseRequestDto response = productsService.tryToUpdateProduct(productDto, bindingResult);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Operation(
            summary = "Удаление продукта",
            responses = {
                    @ApiResponse(
                            description = "Успешный ответ", responseCode = "200"
                    )
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable @Parameter(description = "Идентификатор продукта", required = true) Long id) {
        productsService.deleteById(id);
    }
}
