package com.example.CapstoneProject.repository;


import com.example.CapstoneProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.phoneNumber = ?1 OR u.sub = ?2 OR u.facebookId = ?3")
    Optional<User> findByUsernameOrSubOrFacebookId(String username, String sub, String facebookId);

    Optional<User> findByPhoneNumber(String username);

    Optional<User> findByEmail(String username);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
