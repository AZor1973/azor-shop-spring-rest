package ru.azor.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.azor.api.core.ProductDto;
import ru.azor.api.exceptions.ClientException;
import ru.azor.api.exceptions.ValidationException;
import ru.azor.core.converters.ProductConverter;
import ru.azor.core.entities.Product;
import ru.azor.core.repositories.ProductsRepository;
import ru.azor.core.repositories.specifications.ProductsSpecifications;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final ProductConverter productConverter;

    public Page<Product> search(Integer minPrice, Integer maxPrice, String partTitle, String categoryTitle, Integer page, Integer pageSize) {
        Specification<Product> spec = Specification.where(null);
        if (minPrice != null) {
            spec = spec.and(ProductsSpecifications.priceGreaterOrEqualsThan(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(ProductsSpecifications.priceLessThanOrEqualsThan(maxPrice));
        }
        if (partTitle != null) {
            spec = spec.and(ProductsSpecifications.titleLike(partTitle));
        }
        if (categoryTitle != null) {
            spec = spec.and(ProductsSpecifications.findByCategory(categoryTitle));
        }
        Page<Product> products = productsRepository.findAll(spec, PageRequest.of(page - 1, pageSize));
        log.info("Search: " + products.getTotalElements() + " products");
        return products;
    }

    public Optional<Product> findById(Long id) {
        if (id == null) {
            log.error("Find by id: id = null");
            return Optional.empty();
        }
        Optional<Product> optionalProduct = productsRepository.findById(id);
        log.info("Find by id: id = " + id);
        return optionalProduct;
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new ClientException("Невалидный параметр, идентификатор:" + null, HttpStatus.BAD_REQUEST);
        }
        try {
            productsRepository.deleteById(id);
            log.info("Deleted: id = " + id);
        } catch (Exception ex) {
            throw new ClientException("Ошибка удаления товара. Товар " + id + "не существует", HttpStatus.NOT_FOUND);
        }
    }

    private Product save(Product product) {
        if (product == null) {
            throw new ClientException("Невалидный параметр 'product': null", HttpStatus.BAD_REQUEST);
        }
        if (product.getId() == null && isTitlePresent(product.getTitle())) {
            throw new ClientException("Товар с таким наименованием уже существует:" + product.getTitle(), HttpStatus.CONFLICT);
        }
        Product savedProduct = productsRepository.save(product);
        log.info("Saved: " + savedProduct.getTitle());
        return savedProduct;
    }

    public Product tryToSave(ProductDto productDto, BindingResult bindingResult) {
        if (productDto == null) {
            throw new ClientException("Невалидный параметр 'productDto': null", HttpStatus.BAD_REQUEST);
        }
        if (productDto.getCategories().contains(null)){
            bindingResult.addError(new ObjectError("productDto", "Должна быть выбрана хотя бы одна категория"));
        }
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            throw new ValidationException("Ошибка валидации", errors, HttpStatus.BAD_REQUEST);
        }
        return save(productConverter.dtoToEntity(productDto));
    }

    private Boolean isTitlePresent(String title) {
        return productsRepository.countByTitle(title) > 0;
    }
}
