package ru.azor.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.azor.api.core.CategoryDto;
import ru.azor.core.converters.ProductConverter;
import ru.azor.core.entities.Category;
import ru.azor.core.repositories.CategoriesRepository;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final ProductConverter productConverter;

    public Set<Category> findAllCategories(){
        return Set.copyOf(categoriesRepository.findAll());
    }

    public CategoryDto findCategoryByTitle(String title){
        return productConverter.categoryToCategoryDto(categoriesRepository.findCategoryByTitle(title));
    }
}
