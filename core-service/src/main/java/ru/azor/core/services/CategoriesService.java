package ru.azor.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.azor.api.core.CategoryDto;
import ru.azor.api.exceptions.ClientException;
import ru.azor.api.exceptions.ValidationException;
import ru.azor.core.converters.ProductConverter;
import ru.azor.core.entities.Category;
import ru.azor.core.entities.Product;
import ru.azor.core.repositories.CategoriesRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final ProductConverter productConverter;

    public Page<Category> findAll(Integer page, Integer pageSize) {
        Page<Category> categories = categoriesRepository.findAll(PageRequest.of(page - 1, pageSize));
        log.info("Search: " + categories.getTotalElements() + " categories");
        return categories;
    }

    public Optional<Category> findById(Long id) {
        if (id == null) {
            log.error("Find by id: id = null");
            return Optional.empty();
        }
        Optional<Category> optionalCategory = categoriesRepository.findById(id);
        log.info("Find by id: id = " + id);
        return optionalCategory;
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new ClientException("Невалидный параметр, идентификатор:" + null, HttpStatus.BAD_REQUEST);
        }
        try {
            categoriesRepository.deleteById(id);
            log.info("Deleted: id = " + id);
        } catch (Exception ex) {
            throw new ClientException("Ошибка удаления категории. Категория " + id + "не существует", HttpStatus.NOT_FOUND);
        }
    }

    private Category save(Category category) {
        if (category == null) {
            throw new ClientException("Невалидный параметр 'category': null", HttpStatus.BAD_REQUEST);
        }
        if (category.getId() == null && isTitlePresent(category.getTitle())) {
            throw new ClientException("Товар с таким наименованием уже существует:" + category.getTitle(), HttpStatus.CONFLICT);
        }
        Category savedCategory = categoriesRepository.save(category);
        log.info("Saved: " + savedCategory.getTitle());
        return savedCategory;
    }

    public Category tryToSave(CategoryDto categoryDto, BindingResult bindingResult) {
        if (categoryDto == null) {
            throw new ClientException("Невалидный параметр 'categoryDto': null", HttpStatus.BAD_REQUEST);
        }
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            throw new ValidationException("Ошибка валидации", errors, HttpStatus.BAD_REQUEST);
        }
        return save(productConverter.categoryDtoToCategory(categoryDto));
    }

    private Boolean isTitlePresent(String title) {
        return categoriesRepository.countByTitle(title) > 0;
    }
}
