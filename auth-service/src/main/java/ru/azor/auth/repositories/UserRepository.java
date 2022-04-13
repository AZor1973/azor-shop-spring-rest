package ru.azor.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.azor.auth.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("select count(u) from User u where u.username = ?1")
    Long isUsernamePresent(String username);

    @Query("select count(u) from User u where u.email = ?1")
    Long isEmailPresent(String email);

    @Modifying
    @Query("update User u set u.status = ?1 where u.username = ?2")
    void updateUserStatus(User.AccountStatus status, String username);

    @Modifying
    @Query("update User u set u.enabled = ?1 where u.username = ?2")
    void updateUserEnabled(boolean enabled, String username);
}
