package ru.azor.core.converters;

import org.springframework.stereotype.Component;
import ru.azor.api.core.CategoryDto;
import ru.azor.api.core.ProductDto;
import ru.azor.core.entities.Category;
import ru.azor.core.entities.Product;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductConverter {
    public Product productDtoToProduct(ProductDto productDto) {
        return new Product(productDto.getId(), productDto.getTitle(), productDto.getPrice(), this.setCategoryDtoToSetCategory(productDto.getCategories()));
    }

    public ProductDto productToProductDto(Product product) {
        return new ProductDto(product.getId(), product.getTitle(), product.getPrice(), this.setCategoryToSetCategoryDto(product.getCategories()));
    }

    private Set<Category> setCategoryDtoToSetCategory(Set<CategoryDto> categoryDtos){
        return categoryDtos.stream().map(this::categoryDtoToCategory).collect(Collectors.toSet());
    }

    private Set<CategoryDto> setCategoryToSetCategoryDto(Set<Category> categories){
        return categories.stream().map(this::categoryToCategoryDto).collect(Collectors.toSet());
    }

    private Category categoryDtoToCategory(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getTitle());
    }

    private CategoryDto categoryToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getTitle());
    }
}
