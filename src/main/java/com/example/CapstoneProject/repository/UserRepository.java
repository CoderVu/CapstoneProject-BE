package com.example.CapstoneProject.repository;


import com.example.CapstoneProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByPhoneNumber(String username);
    Optional<User> findByEmail(String username);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.phoneNumber = ?1 OR u.email = ?2")
    Optional<User> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    List<User> findAllButNotDeleted();
}
