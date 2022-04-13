package ru.azor.core.repositories.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.azor.core.entities.Category;
import ru.azor.core.entities.Product;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ProductsSpecifications {
    public static Specification<Product> priceGreaterOrEqualsThan(Integer price) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<Product> priceLessThanOrEqualsThan(Integer price) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<Product> titleLike(String titlePart) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), String.format("%%%s%%", titlePart));
    }

    public static Specification<Product> category(String category) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isMember(category, root.get("categories"));
    }

    public static Specification<Product> findByCategory(final String title) {
        return (root, arg1, cb) -> cb.equal(root.join("categories").get("title"), title);
    }
}
