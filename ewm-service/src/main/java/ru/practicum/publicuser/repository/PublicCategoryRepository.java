package ru.practicum.publicuser.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.admin.model.category.Category;

import java.util.List;

@Repository
public interface PublicCategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c ORDER BY c.id ASC")
    List<Category> getCategories(Pageable pageable);
}
