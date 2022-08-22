package ru.azor.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.azor.api.enums.AccountStatus;
import ru.azor.auth.entities.User;

import java.math.BigInteger;
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
    @Query("update User u set u.status = ?1, u.updatedAt = CURRENT_TIMESTAMP where u.id = ?2")
    void changeStatusById(AccountStatus status, Long id);

    @Modifying
    @Query("update User u set u.enabled = ?1, u.updatedAt = CURRENT_TIMESTAMP where u.username = ?2")
    void changeEnabledByUsername(boolean enabled, String username);

    @Modifying
    @Query("update User u set u.updatedAt = CURRENT_TIMESTAMP where u.id = ?1")
    void changeUpdateAt(Long userId);

    @Modifying
    @Query(value = "delete from users_roles where user_id = ?1", nativeQuery = true)
    void deleteRolesByUserId(BigInteger userId);

    @Modifying
    @Query(value = "insert into users_roles (role_id, user_id) values (?1, ?2)", nativeQuery = true)
    void insertRoleByUserId(BigInteger roleId, BigInteger userId);
}
