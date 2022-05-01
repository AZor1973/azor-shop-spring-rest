package ru.azor.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.core.CategoryDto;
import ru.azor.api.core.ProductDto;
import ru.azor.core.converters.ProductConverter;
import ru.azor.core.entities.Category;
import ru.azor.core.repositories.CategoriesRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final ProductConverter productConverter;

    public Category save(Category category){
        return categoriesRepository.save(category);
    }

    public Set<Category> findAllCategories(){
        return Set.copyOf(categoriesRepository.findAll());
    }

    public CategoryDto findCategoryByTitle(String title){
        return productConverter.categoryToCategoryDto(categoriesRepository.findCategoryByTitle(title));
    }

    public StringResponseRequestDto tryToSaveNewCategory(CategoryDto categoryDto, BindingResult bindingResult) {
        String response;
        HttpStatus httpStatus;
        List<String> errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        if (bindingResult.hasErrors()) {
            response = String.join(" ,", errors);
            httpStatus = HttpStatus.BAD_REQUEST;
            log.error("Ошибка ввода данных при создании новой категории");
            log.error(response);
        } else if (isTitleOfProductPresent(categoryDto.getTitle())) {
            response = "Категория с таким именем уже существует";
            httpStatus = HttpStatus.CONFLICT;
            log.error("Категория с таким именем уже существует");
        } else {
            save(productConverter.categoryDtoToCategory(categoryDto));
            response = "Новая категория создана";
            httpStatus = HttpStatus.CREATED;
            log.info("Новая категория создана");
        }
        return StringResponseRequestDto.builder().value(response)
                .httpStatus(httpStatus).build();
    }

    private boolean isTitleOfProductPresent(String title) {
        return categoriesRepository.isTitleOfProductPresent(title) > 0;
    }
}
