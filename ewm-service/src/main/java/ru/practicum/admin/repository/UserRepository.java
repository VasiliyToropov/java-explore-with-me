package ru.practicum.admin.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.admin.model.user.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public List<User> findByIdIn(List<Long> ids);

    @Query("SELECT u FROM User u ORDER BY u.id ASC")
    public List<User> getUsersUsingFromAndSize(Pageable pageable);
}
