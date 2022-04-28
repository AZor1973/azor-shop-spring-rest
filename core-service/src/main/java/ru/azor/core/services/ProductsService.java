package ru.azor.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.azor.api.common.StringResponseRequestDto;
import ru.azor.api.core.ProductDto;
import ru.azor.api.exceptions.ResourceNotFoundException;
import ru.azor.core.converters.ProductConverter;
import ru.azor.core.entities.Product;
import ru.azor.core.repositories.ProductsRepository;
import ru.azor.core.repositories.specifications.ProductsSpecifications;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final ProductConverter productConverter;

    public Page<Product> findAll(Integer minPrice, Integer maxPrice, String partTitle, String categoryTitle, Integer page) {
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

        return productsRepository.findAll(spec, PageRequest.of(page - 1, 8));
    }

    public Optional<Product> findById(Long id) {
        return productsRepository.findById(id);
    }

    public void deleteById(Long id) {
        productsRepository.deleteById(id);
    }

    public Product save(Product product) {
        return productsRepository.save(product);
    }

    @Transactional
    public Product update(ProductDto productDto) {
        Product product = productsRepository.findById(productDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Невозможно обновить продукта, не надйен в базе, id: " + productDto.getId()));
        product.setPrice(productDto.getPrice());
        product.setTitle(productDto.getTitle());
        return product;
    }

    public StringResponseRequestDto tryToSaveNewProduct(ProductDto productDto, BindingResult bindingResult) {
        String response;
        HttpStatus httpStatus;
        List<String> errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        if (bindingResult.hasErrors()) {
            response = String.join(" ,", errors);
            httpStatus = HttpStatus.BAD_REQUEST;
            log.error("Ошибка ввода данных при создании нового продукта");
            log.error(response);
        } else if (isTitleOfProductPresent(productDto.getTitle())) {
            response = "Продукт с таким именем уже существует";
            httpStatus = HttpStatus.CONFLICT;
            log.error("Продукт с таким именем уже существует");
        } else {
            save(productConverter.productDtoToProduct(productDto));
            response = "Новый продукт создан";
            httpStatus = HttpStatus.CREATED;
            log.info("Новый продукт создан");
        }
        return StringResponseRequestDto.builder().value(response)
                .httpStatus(httpStatus).build();
    }

    private Boolean isTitleOfProductPresent(String title) {
        return productsRepository.isTitleOfProductPresent(title) > 0;
    }
}
