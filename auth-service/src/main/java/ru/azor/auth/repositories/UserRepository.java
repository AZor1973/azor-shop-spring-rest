package ru.azor.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.azor.api.enums.AccountStatus;
import ru.azor.auth.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("select count(u) from User u where u.username = ?1")
    Long countByUsername(String username);

    @Query("select count(u) from User u where u.email = ?1")
    Long countByEmail(String email);

    @Modifying
    @Query("update User u set u.status = ?1, u.updatedAt = CURRENT_TIMESTAMP where u.username = ?2")
    void changeStatusByUsername(AccountStatus status, String username);

    @Modifying
    @Query("update User u set u.enabled = ?1, u.updatedAt = CURRENT_TIMESTAMP where u.username = ?2")
    void changeEnabledByUsername(boolean enabled, String username);

    @Modifying
    @Query(value = "update users_roles set role_id = ?1 where user_id = ?2", nativeQuery = true)
    void changeRole(Long roleId, Long userId);

    @Modifying
    @Query("update User u set u.updatedAt = CURRENT_TIMESTAMP where u.id = ?1")
    void changeUpdateAt(Long userId);
}
