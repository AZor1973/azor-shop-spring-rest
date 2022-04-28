package ru.azor.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.azor.core.entities.Category;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Long> {
    Category findCategoryByTitle(String title);
}
